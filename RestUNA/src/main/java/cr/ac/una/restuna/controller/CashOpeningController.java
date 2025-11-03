package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.CierreCajaDto;
import cr.ac.una.restuna.model.ParametroDto;
import cr.ac.una.restuna.service.ParametroService;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.UserSession;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class CashOpeningController extends Controller implements Initializable {

    @FXML
    private MFXButton btnKeypad;
    @FXML
    private MFXButton btnOk;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private MFXTextField txfInitialFund;
    @FXML
    private VBox keypadRoot;
    @FXML
    private VBox root;

    private Boolean onKeypadMode = false;
    public static CierreCajaDto activeOpening;

    private final ParametroService parametroService = new ParametroService();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadKeypad();
        onKeypadMode = false;
        keypadRoot.setVisible(false);
        keypadRoot.setManaged(false);

        Platform.runLater(() -> {
            if (root.getScene() != null && root.getScene().getWindow() != null) {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.sizeToScene();
            }
        });

        cargarAperturaActiva();
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnKeypad(ActionEvent event) {
        if (!onKeypadMode) {
            onKeypadMode = true;
            setKeypadVisibility(onKeypadMode);
            return;
        }
        onKeypadMode = false;
        setKeypadVisibility(onKeypadMode);
    }

    @FXML
    private void onActionBtnOk(ActionEvent event) {
        try {
            double initAmount = Double.parseDouble(txfInitialFund.getText().trim());
            
            Date fechaApertura = new Date();
            
            // Crear el objeto de cierre de caja (apertura) - solo en memoria
            activeOpening = new CierreCajaDto();
            activeOpening.setEfectivoInicial((long) initAmount);
            activeOpening.setFechaApertura(fechaApertura);
            activeOpening.setEstado("A"); // Estado Abierta
            
            // Guardar en parámetros del sistema
            saveParameters(initAmount, fechaApertura);
            
            showMessage("Caja abierta con éxito.");
            closeWindow();
        } catch (NumberFormatException e) {
            showMessage("Ingrese un monto válido.");
        }
    }
    
    private void saveParameters(double initAmount, Date fechaApertura){
        
        try{
            // Obtener el ID del usuario actual
            Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
            
            // Obtener parámetros existentes del usuario
            Respuesta resp = parametroService.getParametrosPorUsuario(idUsuario);
            List<ParametroDto> parametrosExistentes = new ArrayList<>();
            
            if (resp.getEstado()) {
                String jsonArray = (String) resp.getResultado("Parametros");
                parametrosExistentes = parsearParametros(jsonArray);
            } else {
                System.err.println("Error al obtener parámetros: " + resp.getMensaje());
            }
            
            // Buscar o crear parámetro CAJA_ESTADO
            ParametroDto status = buscarParametro(parametrosExistentes, "CAJA_ESTADO");
            if (status == null) {
                status = new ParametroDto();
                status.setIdUsuario(idUsuario);
                status.setClave("CAJA_ESTADO");
                status.setDescripcion("Estado de la caja");
            }
            status.setValor("A");
            
            // Buscar o crear parámetro FECHA_APERTURA (guardar como timestamp)
            ParametroDto date = buscarParametro(parametrosExistentes, "FECHA_APERTURA");
            if (date == null) {
                date = new ParametroDto();
                date.setIdUsuario(idUsuario);
                date.setClave("FECHA_APERTURA");
                date.setDescripcion("Fecha de apertura de caja");
            }
            date.setValor(String.valueOf(fechaApertura.getTime())); // Guardar timestamp en milisegundos
            
            // Buscar o crear parámetro MONTO_INICIAL
            ParametroDto amount = buscarParametro(parametrosExistentes, "MONTO_INICIAL");
            if (amount == null) {
                amount = new ParametroDto();
                amount.setIdUsuario(idUsuario);
                amount.setClave("MONTO_INICIAL");
                amount.setDescripcion("Monto inicial de apertura");
            }
            amount.setValor(String.valueOf(initAmount));
            
            // Guardar cada parámetro y verificar errores
            Respuesta respStatus = parametroService.guardarParametro(status);
            if (!respStatus.getEstado()) {
                System.err.println("Error al guardar CAJA_ESTADO: " + respStatus.getMensaje());
                throw new Exception("No se pudo guardar el estado de la caja");
            }
            
            Respuesta respDate = parametroService.guardarParametro(date);
            if (!respDate.getEstado()) {
                System.err.println("Error al guardar FECHA_APERTURA: " + respDate.getMensaje());
                throw new Exception("No se pudo guardar la fecha de apertura");
            }
            
            Respuesta respAmount = parametroService.guardarParametro(amount);
            if (!respAmount.getEstado()) {
                System.err.println("Error al guardar MONTO_INICIAL: " + respAmount.getMensaje());
                throw new Exception("No se pudo guardar el monto inicial");
            }
            
        }catch(Exception e){
            e.printStackTrace();
            showMessage("No se pudo abrir la caja. Intente nuevamente.");
        }
    }

    private void cargarAperturaActiva() {
        try {
            if (UserSession.getInstance().getCurrentUser() == null) {
                return;
            }

            Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
            Respuesta resp = parametroService.getParametrosPorUsuario(idUsuario);
            if (!resp.getEstado()) {
                System.err.println("No se pudieron consultar los parámetros de caja: " + resp.getMensaje());
                return;
            }

            String jsonArray = (String) resp.getResultado("Parametros");
            List<ParametroDto> parametros = parsearParametros(jsonArray);

            ParametroDto estado = buscarParametro(parametros, "CAJA_ESTADO");
            ParametroDto fecha = buscarParametro(parametros, "FECHA_APERTURA");
            ParametroDto monto = buscarParametro(parametros, "MONTO_INICIAL");

            if (estado == null || fecha == null || monto == null) {
                return;
            }

            if (!"A".equalsIgnoreCase(estado.getValor())) {
                return;
            }

            String valorFecha = fecha.getValor();
            String valorMonto = monto.getValor();
            if (valorFecha == null || valorFecha.isBlank() || valorMonto == null || valorMonto.isBlank()) {
                return;
            }

            long fechaTimestamp = Long.parseLong(valorFecha);
            long montoInicial = Long.parseLong(valorMonto.replace(".0", ""));

            if (activeOpening == null) {
                activeOpening = new CierreCajaDto();
            }
            activeOpening.setFechaApertura(new Date(fechaTimestamp));
            activeOpening.setEfectivoInicial(montoInicial);
            activeOpening.setEstado("A");

        } catch (Exception e) {
            System.err.println("No se pudo cargar la apertura activa: " + e.getMessage());
        }
    }
    
    private List<ParametroDto> parsearParametros(String jsonArray) {
        List<ParametroDto> parametros = new ArrayList<>();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return parametros;
        }

        // Extraer cada objeto del array JSON
        Pattern pattern = Pattern.compile("\\{[^{}]*\\}");
        Matcher matcher = pattern.matcher(jsonArray);

        while (matcher.find()) {
            String objetoJson = matcher.group();
            ParametroDto parametro = parsearParametro(objetoJson);
            if (parametro != null) {
                parametros.add(parametro);
            }
        }
        
        return parametros;
    }

    private ParametroDto parsearParametro(String objetoJson) {
        try {
            ParametroDto parametro = new ParametroDto();
            
            parametro.setIdParametro(JsonParser.extraerValorLong(objetoJson, "idParametro"));
            parametro.setIdUsuario(JsonParser.extraerValorLong(objetoJson, "idUsuario"));
            parametro.setClave(JsonParser.extraerValor(objetoJson, "clave"));
            parametro.setValor(JsonParser.extraerValor(objetoJson, "valor"));
            parametro.setDescripcion(JsonParser.extraerValor(objetoJson, "descripcion"));
            parametro.setTipoDato(JsonParser.extraerValor(objetoJson, "tipoDato"));
            
            return parametro;
        } catch (Exception e) {
            System.err.println("Error parseando parámetro: " + e.getMessage());
            return null;
        }
    }
    
    private ParametroDto buscarParametro(List<ParametroDto> parametros, String clave) {
        if (parametros == null) return null;
        return parametros.stream()
                .filter(p -> clave.equals(p.getClave()))
                .findFirst()
                .orElse(null);
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        closeWindow();
    }

    private void loadKeypad() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/NumberKeypad.fxml"));
            AnchorPane keypadView = loader.load();
            NumberKeypadController numberKeypad = loader.getController();

            numberKeypad.setInputRoot(txfInitialFund);
            keypadRoot.getChildren().add(keypadView);

            keypadView.prefHeightProperty().bind(keypadRoot.heightProperty());
            keypadView.prefWidthProperty().bind(keypadRoot.widthProperty());

        } catch (IOException ex) {
            System.getLogger(BillingController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void setKeypadVisibility(Boolean isVisible) {
        keypadRoot.setVisible(isVisible);
        keypadRoot.setManaged(isVisible);

        if (root.getScene() != null && root.getScene().getWindow() != null) {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.sizeToScene();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
