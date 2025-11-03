package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import cr.ac.una.restuna.model.CierreCajaDto;
import cr.ac.una.restuna.model.ParametroDto;
import cr.ac.una.restuna.model.ResumenCierreCajaDto;
import cr.ac.una.restuna.service.CierreCajaService;
import cr.ac.una.restuna.service.FacturaService;
import cr.ac.una.restuna.service.ParametroService;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.UserSession;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controlador encargado de ejecutar el cierre de caja en la aplicación cliente.
 * Muestra los montos calculados por el sistema y permite declarar los valores finales.
 */
public class CashClosingController extends Controller implements Initializable {

    @FXML
    private MFXButton btnCancel;
    @FXML
    private MFXButton btnCard;
    @FXML
    private MFXButton btnCash;
    @FXML
    private MFXButton btnEnter;
    @FXML
    private MFXButton btnOk;
    @FXML
    private MFXButton btnTip;
    @FXML
    private VBox keypadRoot;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<CierreCajaDto, String> tbcKey;
    @FXML
    private TreeTableColumn<CierreCajaDto, String> tbcTotalAmount;
    @FXML
    private JFXTreeTableView<CierreCajaDto> tbvPaymentBreakdown;
    @FXML
    private MFXTextField txfInput;
    @FXML
    private MFXButton btnPayPal;

    private MFXButton activeButton;
    private final CierreCajaDto currentClosing = new CierreCajaDto();

    // Totales que calcula el sistema
    private long dineroSistema = 0;
    private long efectivoFacturas = 0;
    private long tarjetaFacturas = 0;
    private int totalFacturas = 0;

    // Montos que declara el cajero (se inicializan con los totales del sistema)
    private long efectivoDeclarado = 0;
    private long tarjetaDeclarada = 0;

    private final ParametroService parametroService = new ParametroService();
    private final FacturaService facturaService = new FacturaService();
    private final CierreCajaService cierreCajaService = new CierreCajaService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvPaymentBreakdown.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvPaymentBreakdown.prefWidthProperty().bind(tableRoot.widthProperty());
        loadKeypad();
        initTableColumns();

        if (!validarApertura()) {
            return;
        }

        cargarTotalesSistema(true);
    }

    @Override
    public void initialize() {
        // Método requerido por Controller, no utilizado.
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void onActionBtnCard(ActionEvent event) {
        setActiveButton(btnCard);
    }

    @FXML
    private void onActionBtnCash(ActionEvent event) {
        setActiveButton(btnCash);
    }

    @FXML
    private void onActionBtnPaypal(ActionEvent event) {
        setActiveButton(btnPayPal);
    }

    @FXML
    private void onActionBtnEnter(ActionEvent event) {
        registrarMontoDeclarado();
    }

    @FXML
    private void onActionBtnOk(ActionEvent event) {
        if (!validarApertura()) {
            return;
        }

        // Actualizar los montos del sistema, conservando los declarados ingresados manualmente.
        cargarTotalesSistema(false);

        if (efectivoDeclarado == 0 && tarjetaDeclarada == 0) {
            showMessage("Debe declarar los montos de efectivo y tarjeta antes de cerrar.");
            return;
        }

        currentClosing.setIdUsuarioCajero(UserSession.getInstance().getCurrentUser().getIdUsuario());
        currentClosing.setFechaApertura(CashOpeningController.activeOpening.getFechaApertura());
        currentClosing.setFechaCierre(new Date());
        currentClosing.setEfectivoInicial(dineroSistema);
        currentClosing.setEfectivoSistema(efectivoFacturas);
        currentClosing.setEfectivoDeclarado(efectivoDeclarado);
        currentClosing.setTarjetaSistema(tarjetaFacturas);
        currentClosing.setTarjetaDeclarado(tarjetaDeclarada);
        currentClosing.setTotalFacturas(totalFacturas);

        long difEfectivo = efectivoDeclarado - (dineroSistema + efectivoFacturas);
        long difTarjeta = tarjetaDeclarada - tarjetaFacturas;

        currentClosing.setDiferenciaEfectivo(difEfectivo);
        currentClosing.setDiferenciaTarjeta(difTarjeta);
        currentClosing.setEstado("CERRADO");

        Respuesta respuesta = cierreCajaService.guardarCierreCaja(currentClosing);
        if (respuesta.getEstado()) {
            saveParameters();
            String mensaje = String.format(
                    "Cierre de caja completado correctamente.%n%n" +
                    "Resumen:%n" +
                    "Facturas procesadas: %d%n" +
                    "Dinero Sistema: CRC %,d%n" +
                    "Facturas (Efectivo): CRC %,d%n" +
                    "Dinero Declarado (Efectivo): CRC %,d%n" +
                    "Diferencia Efectivo: CRC %,d%n%n" +
                    "Facturas (Tarjeta): CRC %,d%n" +
                    "Dinero Declarado (Tarjeta): CRC %,d%n" +
                    "Diferencia Tarjeta: CRC %,d",
                    totalFacturas,
                    dineroSistema, efectivoFacturas, efectivoDeclarado, difEfectivo,
                    tarjetaFacturas, tarjetaDeclarada, difTarjeta
            );
            showMessage(mensaje);
            CashOpeningController.activeOpening = null;
            closeWindow();
        } else {
            showMessage("Error al guardar cierre: " + respuesta.getMensaje());
        }
    }

    @FXML
    private void onActionBtnTip(ActionEvent event) {
        setActiveButton(btnTip);
    }

    @FXML
    private void onActionTxfInput(ActionEvent event) {
        // No se requiere lógica adicional.
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void loadKeypad() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/NumberKeypad.fxml"));
            AnchorPane keypadView = loader.load();
            NumberKeypadController numberKeypad = loader.getController();

            numberKeypad.setInputRoot(txfInput);
            keypadRoot.getChildren().add(keypadView);

            keypadView.prefHeightProperty().bind(keypadRoot.heightProperty());
            keypadView.prefWidthProperty().bind(keypadRoot.widthProperty());
        } catch (IOException ex) {
            System.getLogger(BillingController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void initTableColumns() {
        tbcKey.setCellValueFactory(new TreeItemPropertyValueFactory<>("estado"));
        tbcTotalAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("observaciones"));

        TreeItem<CierreCajaDto> root = new TreeItem<>(new CierreCajaDto());
        addPaymentRow(root, "Dinero Sistema", "CRC 0");
        addPaymentRow(root, "Total Facturas", "0");
        addPaymentRow(root, "---", "---");
        addPaymentRow(root, "Dinero Declarado (Efectivo)", "CRC 0");
        addPaymentRow(root, "Dinero Declarado (Tarjeta)", "CRC 0");

        tbvPaymentBreakdown.setRoot(root);
        tbvPaymentBreakdown.setShowRoot(false);
    }

    private void addPaymentRow(TreeItem<CierreCajaDto> root, String key, String value) {
        CierreCajaDto row = new CierreCajaDto();
        row.setEstado(key);
        row.setObservaciones(value);
        root.getChildren().add(new TreeItem<>(row));
    }

    /**
     * Carga los totales del sistema. Cuando resetDeclarados es true, también
     * inicializa los montos declarados con los totales de las facturas.
     */
    private void cargarTotalesSistema(boolean resetDeclarados) {
        if (CashOpeningController.activeOpening == null) {
            if (!cargarAperturaDesdeParametros()) {
                showMessage("No hay apertura activa. Por favor, abra la caja primero.");
                return;
            }
        }

        Long montoInicial = CashOpeningController.activeOpening.getEfectivoInicial();
        dineroSistema = montoInicial != null ? montoInicial : 0;

        Date fechaApertura = CashOpeningController.activeOpening.getFechaApertura();
        if (fechaApertura == null) {
            showMessage("Error: No se puede determinar la fecha de apertura.");
            return;
        }

        Date fechaCierre = new Date();
        Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
        Respuesta respuesta = facturaService.obtenerResumenCierreCaja(idUsuario, fechaApertura, fechaCierre);

        if (respuesta.getEstado()) {
            Object resumenObj = respuesta.getResultado("ResumenCierreCaja");
            if (resumenObj instanceof ResumenCierreCajaDto resumen) {
                efectivoFacturas = resumen.getEfectivoSistemaAsLong();
                tarjetaFacturas = resumen.getTarjetaSistemaAsLong();
                totalFacturas = resumen.getTotalFacturas() != null ? Math.toIntExact(resumen.getTotalFacturas()) : 0;
            } else {
                efectivoFacturas = 0;
                tarjetaFacturas = 0;
                totalFacturas = 0;
                System.err.println("No se pudo interpretar el resumen del cierre de caja.");
            }
        } else {
            efectivoFacturas = 0;
            tarjetaFacturas = 0;
            totalFacturas = 0;
            System.err.println("Error al obtener resumen de facturas: " + respuesta.getMensaje());
        }

        if (resetDeclarados) {
            efectivoDeclarado = efectivoFacturas;
            tarjetaDeclarada = tarjetaFacturas;
        }

        actualizarTabla();
    }

    private boolean cargarAperturaDesdeParametros() {
        try {
            Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
            Respuesta resp = parametroService.getParametrosPorUsuario(idUsuario);
            if (!resp.getEstado()) {
                System.err.println("Error al obtener parametros: " + resp.getMensaje());
                return false;
            }

            String jsonArray = (String) resp.getResultado("Parametros");
            if (jsonArray == null || jsonArray.trim().isEmpty()) {
                return false;
            }

            List<String> parametrosJson = JsonParser.extraerObjetosDelArray(jsonArray);
            String estado = null;
            Long fechaTimestamp = null;
            Long montoInicial = null;

            for (String parametroJson : parametrosJson) {
                String clave = JsonParser.extraerValor(parametroJson, "clave");
                String valor = JsonParser.extraerValor(parametroJson, "valor");
                if (clave == null) {
                    continue;
                }

                switch (clave) {
                    case "CAJA_ESTADO":
                        estado = valor;
                        break;
                    case "FECHA_APERTURA":
                        try {
                            if (valor != null && !valor.isBlank()) {
                                fechaTimestamp = Long.parseLong(valor);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parseando fecha: " + valor);
                        }
                        break;
                    case "MONTO_INICIAL":
                        try {
                            if (valor != null && !valor.isBlank()) {
                                montoInicial = Long.parseLong(valor.replace(".0", ""));
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parseando monto: " + valor);
                        }
                        break;
                    default:
                        break;
                }
            }

            if (estado == null || !"A".equalsIgnoreCase(estado)) {
                return false;
            }

            if (fechaTimestamp != null && montoInicial != null) {
                CashOpeningController.activeOpening = new CierreCajaDto();
                CashOpeningController.activeOpening.setFechaApertura(new Date(fechaTimestamp));
                CashOpeningController.activeOpening.setEfectivoInicial(montoInicial);
                CashOpeningController.activeOpening.setEstado("A");
                return true;
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error cargando apertura desde parametros: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void actualizarTabla() {
        TreeItem<CierreCajaDto> root = tbvPaymentBreakdown.getRoot();
        if (root == null || root.getChildren().size() < 5) {
            return;
        }

        root.getChildren().get(0).getValue().setObservaciones(formatCurrency(dineroSistema));
        root.getChildren().get(1).getValue().setObservaciones(String.format("%,d", totalFacturas));
        root.getChildren().get(3).getValue().setObservaciones(formatCurrency(efectivoDeclarado));
        root.getChildren().get(4).getValue().setObservaciones(formatCurrency(tarjetaDeclarada));

        tbvPaymentBreakdown.refresh();
    }

    private String formatCurrency(long amount) {
        return String.format("CRC %,d", amount);
    }

    private void registrarMontoDeclarado() {
        if (activeButton == null) {
            showMessage("Seleccione un metodo de pago (Efectivo o Tarjeta).");
            return;
        }

        try {
            long monto = Long.parseLong(txfInput.getText().trim());
            if (monto < 0) {
                showMessage("El monto no puede ser negativo.");
                return;
            }

            if (activeButton == btnCash) {
                efectivoDeclarado = monto;
            } else if (activeButton == btnCard || activeButton == btnPayPal) {
                tarjetaDeclarada = monto;
            }

            actualizarTabla();
            txfInput.clear();
        } catch (NumberFormatException e) {
            showMessage("Ingrese un monto valido.");
        }
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void setActiveButton(MFXButton button) {
        if (activeButton != null) {
            activeButton.setStyle("");
        }
        button.setStyle("-fx-background-color: #475569;\n-fx-border-color: #475569");
        activeButton = button;
    }

    /**
     * Valida que exista una apertura de caja activa.
     */
    private boolean validarApertura() {
        if (CashOpeningController.activeOpening == null) {
            if (!cargarAperturaDesdeParametros()) {
                showMessage("No hay una caja abierta. Vaya al menu de apertura para iniciar una antes de cerrar.");
                return false;
            }
        }
        return true;
    }

    private void saveParameters() {
        try {
            Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
            Respuesta resp = parametroService.getParametrosPorUsuario(idUsuario);
            if (!resp.getEstado()) {
                System.err.println("No fue posible consultar el estado de la caja.");
                return;
            }

            String parametrosJson = (String) resp.getResultado("Parametros");

            ParametroDto estadoParametro = obtenerOCrearParametro(parametrosJson, idUsuario, "CAJA_ESTADO", "Estado de la caja");
            estadoParametro.setValor("C");
            Respuesta guardarEstado = parametroService.guardarParametro(estadoParametro);

            ParametroDto fechaParametro = obtenerOCrearParametro(parametrosJson, idUsuario, "FECHA_APERTURA", "Fecha de apertura de caja");
            fechaParametro.setValor("");
            Respuesta guardarFecha = parametroService.guardarParametro(fechaParametro);

            ParametroDto montoParametro = obtenerOCrearParametro(parametrosJson, idUsuario, "MONTO_INICIAL", "Monto inicial de apertura");
            montoParametro.setValor("0");
            Respuesta guardarMonto = parametroService.guardarParametro(montoParametro);

            if (!guardarEstado.getEstado() || !guardarFecha.getEstado() || !guardarMonto.getEstado()) {
                System.err.println("No se pudieron actualizar todos los parametros de caja.");
            }
        } catch (Exception e) {
            showMessage("Error al guardar parametros: " + e.getMessage());
        }
    }

    private ParametroDto obtenerOCrearParametro(String jsonArray, Long idUsuario, String clave, String descripcion) {
        ParametroDto parametro = null;

        if (jsonArray != null && !jsonArray.trim().isEmpty()) {
            List<String> parametrosJson = JsonParser.extraerObjetosDelArray(jsonArray);
            for (String parametroJson : parametrosJson) {
                String claveActual = JsonParser.extraerValor(parametroJson, "clave");
                if (clave.equals(claveActual)) {
                    parametro = new ParametroDto();
                    parametro.setIdParametro(JsonParser.extraerValorLong(parametroJson, "idParametro"));
                    parametro.setIdUsuario(JsonParser.extraerValorLong(parametroJson, "idUsuario"));
                    parametro.setClave(claveActual);
                    parametro.setValor(JsonParser.extraerValor(parametroJson, "valor"));
                    parametro.setDescripcion(JsonParser.extraerValor(parametroJson, "descripcion"));
                    parametro.setTipoDato(JsonParser.extraerValor(parametroJson, "tipoDato"));
                    break;
                }
            }
        }

        if (parametro == null) {
            parametro = new ParametroDto();
            parametro.setIdUsuario(idUsuario);
            parametro.setClave(clave);
            parametro.setDescripcion(descripcion);
        }

        return parametro;
    }
}
