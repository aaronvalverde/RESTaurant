package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.ParametroDto;
import cr.ac.una.restuna.service.ParametroService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.TextFieldValidator;
import cr.ac.una.restuna.util.UserSession;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.models.spinner.DoubleSpinnerModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;


public class SettingsController extends Controller implements Initializable {

    @FXML
    private MFXScrollPane settingsRoot;
    @FXML
    private VBox settingsContainer;
    @FXML
    private MFXComboBox<String> cmbLanguage;
    @FXML
    private MFXComboBox<String> cmbCurrency;
    @FXML
    private MFXTextField txfRestaurantName;
    @FXML
    private MFXSpinner<Double> spinnerIVA;
    @FXML
    private MFXSpinner<Double> spinnerServiceTax;
    @FXML
    private MFXSpinner<Double> spinnerCashierDiscount;
    @FXML
    private MFXTextField txfPhone;
    @FXML
    private MFXTextField txfSecondaryPhone;
    @FXML
    private MFXTextField txfEmail;
    @FXML
    private MFXTextField txfAddress;
    @FXML
    private MFXButton btnSave;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private MFXButton btnExit;

    private final ParametroService parametroService = new ParametroService();
    private final Map<String, ParametroDto> parametrosMap = new HashMap<>();
    private final Map<String, String> valoresOriginales = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadLanguageOptions();
        loadCurrencyOptions();
        configurarSpinners();

        
        TextFieldValidator.addPhoneValidation(txfPhone);
        TextFieldValidator.addPhoneValidation(txfSecondaryPhone);
        TextFieldValidator.addEmailValidation(txfEmail);

        settingsContainer.prefHeightProperty().bind(settingsRoot.heightProperty());
        settingsContainer.prefWidthProperty().bind(settingsRoot.widthProperty());

        
        cargarParametros();
    }

    @Override
    public void initialize() {
    }

    private void configurarSpinners() {
        
        

        if (spinnerIVA != null) {
            DoubleSpinnerModel modelIVA = new DoubleSpinnerModel();
            modelIVA.setMin(0.0);
            modelIVA.setMax(100.0);
            modelIVA.setIncrement(0.5);
            modelIVA.setValue(13.0); 
            spinnerIVA.setSpinnerModel(modelIVA);
        }

        if (spinnerServiceTax != null) {
            DoubleSpinnerModel modelService = new DoubleSpinnerModel();
            modelService.setMin(0.0);
            modelService.setMax(100.0);
            modelService.setIncrement(0.5);
            modelService.setValue(10.0); 
            spinnerServiceTax.setSpinnerModel(modelService);
        }

        if (spinnerCashierDiscount != null) {
            DoubleSpinnerModel modelDiscount = new DoubleSpinnerModel();
            modelDiscount.setMin(0.0);
            modelDiscount.setMax(100.0);
            modelDiscount.setIncrement(0.5);
            modelDiscount.setValue(5.0); 
            spinnerCashierDiscount.setSpinnerModel(modelDiscount);
        }
    }

    private void cargarParametros() {
        if (!UserSession.getInstance().isAuthenticated()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(FlowController.getInstance().getLanguage().getString("msg.session"));
            alert.setHeaderText(null);
            alert.setContentText(FlowController.getInstance().getLanguage().getString("msg.no.user"));
            alert.showAndWait();
            return;
        }

        Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
        if (idUsuario == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(FlowController.getInstance().getLanguage().getString("msg.user"));
            alert.setHeaderText(null);
            alert.setContentText(FlowController.getInstance().getLanguage().getString("msg.no.id"));
            alert.showAndWait();
            return;
        }

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = parametroService.getParametrosPorUsuario(idUsuario);

                Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("Parametros");
                        procesarParametros(jsonArray);
                        aplicarParametrosAUI();
                    } else {
                        
                        System.out.println("No hay parámetros guardados para el usuario, aplicando valores por defecto");
                        aplicarValoresPorDefecto();
                    }
                });

                return null;
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(FlowController.getInstance().getLanguage().getString("msg.error"));
                    alert.setHeaderText(null);
                    alert.setContentText(FlowController.getInstance().getLanguage().getString("msg.setting.error") + getException().getMessage());
                    alert.showAndWait();
                });
            }
        };

        new Thread(task).start();
    }

    private void procesarParametros(String jsonArray) {
        parametrosMap.clear();

        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }

        
        Pattern pattern = Pattern.compile("\\{[^{}]*\\}");
        Matcher matcher = pattern.matcher(jsonArray);

        while (matcher.find()) {
            String objetoJson = matcher.group();
            ParametroDto parametro = parsearParametro(objetoJson);
            if (parametro != null && parametro.getClave() != null) {
                parametrosMap.put(parametro.getClave(), parametro);
            }
        }
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

    private void aplicarParametrosAUI() {
        
        ParametroDto idioma = parametrosMap.get("IDIOMA");
        if (idioma != null && idioma.getValor() != null) {
            if ("es".equalsIgnoreCase(idioma.getValor())) {
                cmbLanguage.selectItem("Español");
            } else {
                cmbLanguage.selectItem("English");
            }
        }

        
        ParametroDto moneda = parametrosMap.get("MONEDA");
        if (moneda != null && moneda.getValor() != null) {
            cmbCurrency.selectItem(moneda.getValor());
        }

        
        ParametroDto nombreRestaurante = parametrosMap.get("NOMBRE_RESTAURANTE");
        if (nombreRestaurante != null && nombreRestaurante.getValor() != null) {
            txfRestaurantName.setText(nombreRestaurante.getValor());
        }

        
        ParametroDto iva = parametrosMap.get("IMPUESTO_VENTA");
        if (iva != null && iva.getValorComoDecimal() != null && spinnerIVA != null) {
            try {
                if (spinnerIVA.getSpinnerModel() != null) {
                    spinnerIVA.setValue(iva.getValorComoDecimal());
                }
            } catch (Exception e) {
                System.err.println("Error al establecer valor IVA: " + e.getMessage());
            }
        }

        
        ParametroDto impuestoServicio = parametrosMap.get("IMPUESTO_SERVICIO");
        if (impuestoServicio != null && impuestoServicio.getValorComoDecimal() != null && spinnerServiceTax != null) {
            try {
                if (spinnerServiceTax.getSpinnerModel() != null) {
                    spinnerServiceTax.setValue(impuestoServicio.getValorComoDecimal());
                }
            } catch (Exception e) {
                System.err.println("Error al establecer valor impuesto servicio: " + e.getMessage());
            }
        }

        
        ParametroDto descuentoMaximo = parametrosMap.get("DESCUENTO_MAXIMO_CAJERO");
        if (descuentoMaximo != null && descuentoMaximo.getValorComoDecimal() != null && spinnerCashierDiscount != null) {
            try {
                if (spinnerCashierDiscount.getSpinnerModel() != null) {
                    spinnerCashierDiscount.setValue(descuentoMaximo.getValorComoDecimal());
                }
            } catch (Exception e) {
                System.err.println("Error al establecer valor descuento cajero: " + e.getMessage());
            }
        }

        
        ParametroDto telefono = parametrosMap.get("TELEFONO");
        if (telefono != null && telefono.getValor() != null) {
            txfPhone.setText(telefono.getValor());
        }

        
        ParametroDto telefonoSecundario = parametrosMap.get("TELEFONO_SECUNDARIO");
        if (telefonoSecundario != null && telefonoSecundario.getValor() != null) {
            txfSecondaryPhone.setText(telefonoSecundario.getValor());
        }

        
        ParametroDto email = parametrosMap.get("EMAIL");
        if (email != null && email.getValor() != null) {
            txfEmail.setText(email.getValor());
        }

        
        ParametroDto direccion = parametrosMap.get("DIRECCION");
        if (direccion != null && direccion.getValor() != null) {
            txfAddress.setText(direccion.getValor());
        }

        
        guardarValoresOriginales();
    }

    
    private void aplicarValoresPorDefecto() {
        
        cmbLanguage.selectItem("Español");

        
        cmbCurrency.selectItem("CRC - Colón");

        
        txfRestaurantName.setText("");

        
        if (spinnerIVA != null) {
            try {
                if (spinnerIVA.getSpinnerModel() != null) {
                    spinnerIVA.setValue(13.0); 
                }
            } catch (Exception e) {
                System.err.println("Error al establecer valor por defecto IVA: " + e.getMessage());
            }
        }

        if (spinnerServiceTax != null) {
            try {
                if (spinnerServiceTax.getSpinnerModel() != null) {
                    spinnerServiceTax.setValue(10.0); 
                }
            } catch (Exception e) {
                System.err.println("Error al establecer valor por defecto servicio: " + e.getMessage());
            }
        }

        if (spinnerCashierDiscount != null) {
            try {
                if (spinnerCashierDiscount.getSpinnerModel() != null) {
                    spinnerCashierDiscount.setValue(5.0); 
                }
            } catch (Exception e) {
                System.err.println("Error al establecer valor por defecto descuento: " + e.getMessage());
            }
        }

        
        txfPhone.setText("");
        txfSecondaryPhone.setText("");
        txfEmail.setText("");
        txfAddress.setText("");
    }

    @FXML
    private void onActionBtnSave(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
        List<ParametroDto> parametrosAGuardar = new ArrayList<>();

        
        parametrosAGuardar.add(crearParametro("IDIOMA", obtenerCodigoIdioma(),
                "Idioma de la interfaz", "STRING", idUsuario));

        parametrosAGuardar.add(crearParametro("MONEDA", cmbCurrency.getSelectedItem(),
                "Moneda utilizada en el restaurante", "STRING", idUsuario));

        parametrosAGuardar.add(crearParametro("NOMBRE_RESTAURANTE", txfRestaurantName.getText().trim(),
                "Nombre del restaurante", "STRING", idUsuario));

        
        Double ivaValue = spinnerIVA != null && spinnerIVA.getValue() != null ? spinnerIVA.getValue() : 13.0;
        Double serviceTaxValue = spinnerServiceTax != null && spinnerServiceTax.getValue() != null ? spinnerServiceTax.getValue() : 10.0;
        Double cashierDiscountValue = spinnerCashierDiscount != null && spinnerCashierDiscount.getValue() != null ? spinnerCashierDiscount.getValue() : 5.0;

        parametrosAGuardar.add(crearParametro("IMPUESTO_VENTA",
                String.valueOf(ivaValue),
                "Porcentaje de impuesto sobre ventas (IVA)", "NUMBER", idUsuario));

        parametrosAGuardar.add(crearParametro("IMPUESTO_SERVICIO",
                String.valueOf(serviceTaxValue),
                "Porcentaje de impuesto por servicio", "NUMBER", idUsuario));

        parametrosAGuardar.add(crearParametro("DESCUENTO_MAXIMO_CAJERO",
                String.valueOf(cashierDiscountValue),
                "Descuento máximo que puede aplicar un cajero (%)", "NUMBER", idUsuario));

        parametrosAGuardar.add(crearParametro("TELEFONO", txfPhone.getText().trim(),
                "Teléfono principal del restaurante", "STRING", idUsuario));

        
        if (txfSecondaryPhone.getText() != null && !txfSecondaryPhone.getText().trim().isEmpty()) {
            parametrosAGuardar.add(crearParametro("TELEFONO_SECUNDARIO", txfSecondaryPhone.getText().trim(),
                    "Teléfono secundario del restaurante", "STRING", idUsuario));
        }

        parametrosAGuardar.add(crearParametro("EMAIL", txfEmail.getText().trim(),
                "Correo electrónico del restaurante", "STRING", idUsuario));

        parametrosAGuardar.add(crearParametro("DIRECCION", txfAddress.getText().trim(),
                "Dirección física del restaurante", "STRING", idUsuario));

        
        guardarParametrosEnServidor(parametrosAGuardar);
    }

    private ParametroDto crearParametro(String clave, String valor, String descripcion,
                                        String tipoDato, Long idUsuario) {
        ParametroDto parametro = parametrosMap.get(clave);
        if (parametro == null) {
            parametro = new ParametroDto();
            parametro.setClave(clave);
        }

        
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El valor para " + clave + " no puede estar vacío");
        }

        parametro.setValor(valor.trim());
        parametro.setDescripcion(descripcion);
        parametro.setTipoDato(tipoDato);
        parametro.setIdUsuario(idUsuario);
        parametro.setModificado(true);

        return parametro;
    }

    private void guardarParametrosEnServidor(List<ParametroDto> parametros) {
        
        String nuevoIdioma = obtenerCodigoIdioma();
        String idiomaOriginal = valoresOriginales.get("IDIOMA");
        final boolean cambioIdioma = idiomaOriginal != null && !nuevoIdioma.equals(idiomaOriginal);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = parametroService.guardarParametros(parametros);

                Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        if (cambioIdioma) {
                            
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(FlowController.getInstance().getLanguage().getString("msg.change.language"));
                            alert.setHeaderText(null);
                            alert.setContentText(FlowController.getInstance().getLanguage().getString("msg.success.save") + "\n\n" +
                                    FlowController.getInstance().getLanguage().getString("msg.save.language.content") + "\n" +
                                    FlowController.getInstance().getLanguage().getString("msg.relogin"));
                            alert.showAndWait();

                            
                            UserSession.getInstance().clearSession();
                            FlowController.getInstance().goMain(AppKeys.LOGIN);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(FlowController.getInstance().getLanguage().getString("msg.setting"));
                            alert.setHeaderText(null);
                            alert.setContentText(FlowController.getInstance().getLanguage().getString("msg.success.save"));
                            alert.showAndWait();

                            
                            String jsonArray = (String) respuesta.getResultado("Parametros");
                            procesarParametros(jsonArray);

                            
                            guardarValoresOriginales();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(FlowController.getInstance().getLanguage().getString("msg.error"));
                        alert.setHeaderText(null);
                        alert.setContentText(FlowController.getInstance().getLanguage().getString("msg.save.error") + respuesta.getMensaje());
                        alert.showAndWait();
                    }
                });

                return null;
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(FlowController.getInstance().getLanguage().getString("msg.error"));
                    alert.setHeaderText(null);
                    alert.setContentText(FlowController.getInstance().getLanguage().getString("msg.save.setting.error") + getException().getMessage());
                    alert.showAndWait();
                });
            }
        };

        new Thread(task).start();
    }

    private boolean validarCampos() {
        
        if (cmbLanguage.getSelectedItem() == null || cmbLanguage.getSelectedItem().trim().isEmpty()) {
            mostrarError(getLanguageString("msg.language.required"), null);
            return false;
        }

        
        if (cmbCurrency.getSelectedItem() == null || cmbCurrency.getSelectedItem().trim().isEmpty()) {
            mostrarError(getLanguageString("msg.currency.required"), null);
            return false;
        }

        
        if (txfRestaurantName.getText() == null || txfRestaurantName.getText().trim().isEmpty()) {
            mostrarError(getLanguageString("msg.restaurant.name.required"), txfRestaurantName);
            return false;
        }

        
        if (txfPhone.getText() == null || txfPhone.getText().trim().isEmpty()) {
            mostrarError(getLanguageString("msg.phone.required"), txfPhone);
            return false;
        }

        
        String phoneRegex = "^[+]?[0-9\\s()-]{7,20}$";
        if (!txfPhone.getText().trim().matches(phoneRegex)) {
            mostrarError(getLanguageString("msg.phone.format"), txfPhone);
            return false;
        }

        
        if (txfSecondaryPhone.getText() != null && !txfSecondaryPhone.getText().trim().isEmpty()) {
            if (!txfSecondaryPhone.getText().trim().matches(phoneRegex)) {
                mostrarError(getLanguageString("msg.secondaryphone.format"), txfSecondaryPhone);
                return false;
            }
        }

        
        if (txfEmail.getText() == null || txfEmail.getText().trim().isEmpty()) {
            mostrarError(getLanguageString("msg.email.required"), txfEmail);
            return false;
        }

        
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!txfEmail.getText().trim().matches(emailRegex)) {
            mostrarError(getLanguageString("msg.email.format"), txfEmail);
            return false;
        }

        
        if (txfAddress.getText() == null || txfAddress.getText().trim().isEmpty()) {
            mostrarError(getLanguageString("msg.address.required"), txfAddress);
            return false;
        }

        
        Double ivaValue = spinnerIVA != null && spinnerIVA.getValue() != null ? spinnerIVA.getValue() : 13.0;
        if (ivaValue < 0 || ivaValue > 100) {
            mostrarError(getLanguageString("msg.iva"), null);
            return false;
        }

        Double serviceTaxValue = spinnerServiceTax != null && spinnerServiceTax.getValue() != null ? spinnerServiceTax.getValue() : 10.0;
        if (serviceTaxValue < 0 || serviceTaxValue > 100) {
            mostrarError(getLanguageString("msg.service.tax"), null);
            return false;
        }

        Double cashierDiscountValue = spinnerCashierDiscount != null && spinnerCashierDiscount.getValue() != null ? spinnerCashierDiscount.getValue() : 5.0;
        if (cashierDiscountValue < 0 || cashierDiscountValue > 100) {
            mostrarError(getLanguageString("msg.max.discount"), null);
            return false;
        }

        return true;
    }

    private void mostrarError(String mensaje, MFXTextField campo) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(getLanguageString("msg.validation"));
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();

        if (campo != null) {
            campo.requestFocus();
        }
    }

    private String obtenerCodigoIdioma() {
        String idiomaSeleccionado = cmbLanguage.getSelectedItem();
        if (idiomaSeleccionado != null &&
                (idiomaSeleccionado.equals("Español") || idiomaSeleccionado.equals("Spanish"))) {
            return "es";
        }
        return "en";
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        
        aplicarParametrosAUI();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(getLanguageString("msg.setting"));
        alert.setHeaderText(null);
        alert.setContentText(getLanguageString("msg.cancel.changes"));
        alert.showAndWait();
    }

    @FXML
    private void onActionCmbLanguage(ActionEvent event) {
        if (cmbLanguage.getSelectedItem().equals("Español") || cmbLanguage.getSelectedItem().equals("Spanish")) {
            checkLanguage("es");
        } else {
            checkLanguage("en");
        }
    }

    @FXML
    private void onActionCmbCurrency(ActionEvent event) {

    }

    private void loadLanguageOptions() {
        cmbLanguage.getItems().clear();
        cmbLanguage.getItems().add("Español");
        cmbLanguage.getItems().add("English");

        
        if (cmbLanguage.getSelectedItem() == null || cmbLanguage.getSelectedItem().isEmpty()) {
            cmbLanguage.selectItem("Español");
        }
    }

    private void loadCurrencyOptions() {
        cmbCurrency.getItems().clear();
        cmbCurrency.getItems().add("CRC - Colón");
        cmbCurrency.getItems().add("USD - Dólar");
        cmbCurrency.getItems().add("EUR - Euro");

        
        if (cmbCurrency.getSelectedItem() == null || cmbCurrency.getSelectedItem().isEmpty()) {
            cmbCurrency.selectItem("CRC - Colón");
        }
    }

    private void checkLanguage(String key) {
        if (FlowController.getInstance().getLanguage().toString().equals(key)) {
            return;
        }
        Locale locale = Locale.of(key);
        ResourceBundle bundle = ResourceBundle.getBundle("cr.ac.una.restuna.i18n.text", locale);
        FlowController.getInstance().setLanguage(bundle);
    }

    
    private void aplicarCambioDeIdioma(String codigoIdioma) {
        try {
            Locale locale;

            if ("es".equalsIgnoreCase(codigoIdioma)) {
                locale = Locale.of("es");
            } else if ("en".equalsIgnoreCase(codigoIdioma)) {
                locale = Locale.of("en");
            } else {
                
                locale = Locale.of("es");
            }

            ResourceBundle bundle = ResourceBundle.getBundle("cr.ac.una.restuna.i18n.text", locale);
            FlowController.getInstance().setLanguage(bundle);

            System.out.println("Idioma del sistema cambiado a: " + locale.getLanguage());
        } catch (Exception e) {
            System.err.println("Error cambiando idioma del sistema: " + e.getMessage());
        }
    }

    
    private void guardarValoresOriginales() {
        valoresOriginales.clear();
        valoresOriginales.put("IDIOMA", obtenerCodigoIdioma());
        valoresOriginales.put("MONEDA", cmbCurrency.getSelectedItem() != null ? cmbCurrency.getSelectedItem() : "");
        valoresOriginales.put("NOMBRE_RESTAURANTE", txfRestaurantName.getText() != null ? txfRestaurantName.getText().trim() : "");
        valoresOriginales.put("IMPUESTO_VENTA", spinnerIVA != null && spinnerIVA.getValue() != null ? String.valueOf(spinnerIVA.getValue()) : "13.0");
        valoresOriginales.put("IMPUESTO_SERVICIO", spinnerServiceTax != null && spinnerServiceTax.getValue() != null ? String.valueOf(spinnerServiceTax.getValue()) : "10.0");
        valoresOriginales.put("DESCUENTO_MAXIMO_CAJERO", spinnerCashierDiscount != null && spinnerCashierDiscount.getValue() != null ? String.valueOf(spinnerCashierDiscount.getValue()) : "5.0");
        valoresOriginales.put("TELEFONO", txfPhone.getText() != null ? txfPhone.getText().trim() : "");
        valoresOriginales.put("TELEFONO_SECUNDARIO", txfSecondaryPhone.getText() != null ? txfSecondaryPhone.getText().trim() : "");
        valoresOriginales.put("EMAIL", txfEmail.getText() != null ? txfEmail.getText().trim() : "");
        valoresOriginales.put("DIRECCION", txfAddress.getText() != null ? txfAddress.getText().trim() : "");
    }

    
    private boolean hayCambiosNoGuardados() {
        if (valoresOriginales.isEmpty()) {
            return false; 
        }

        
        String idiomaActual = obtenerCodigoIdioma();
        if (!idiomaActual.equals(valoresOriginales.get("IDIOMA"))) return true;

        String monedaActual = cmbCurrency.getSelectedItem() != null ? cmbCurrency.getSelectedItem() : "";
        if (!monedaActual.equals(valoresOriginales.get("MONEDA"))) return true;

        String nombreActual = txfRestaurantName.getText() != null ? txfRestaurantName.getText().trim() : "";
        if (!nombreActual.equals(valoresOriginales.get("NOMBRE_RESTAURANTE"))) return true;

        String ivaActual = spinnerIVA != null && spinnerIVA.getValue() != null ? String.valueOf(spinnerIVA.getValue()) : "13.0";
        if (!ivaActual.equals(valoresOriginales.get("IMPUESTO_VENTA"))) return true;

        String servicioActual = spinnerServiceTax != null && spinnerServiceTax.getValue() != null ? String.valueOf(spinnerServiceTax.getValue()) : "10.0";
        if (!servicioActual.equals(valoresOriginales.get("IMPUESTO_SERVICIO"))) return true;

        String descuentoActual = spinnerCashierDiscount != null && spinnerCashierDiscount.getValue() != null ? String.valueOf(spinnerCashierDiscount.getValue()) : "5.0";
        if (!descuentoActual.equals(valoresOriginales.get("DESCUENTO_MAXIMO_CAJERO"))) return true;

        String telefonoActual = txfPhone.getText() != null ? txfPhone.getText().trim() : "";
        if (!telefonoActual.equals(valoresOriginales.get("TELEFONO"))) return true;

        String telefono2Actual = txfSecondaryPhone.getText() != null ? txfSecondaryPhone.getText().trim() : "";
        if (!telefono2Actual.equals(valoresOriginales.get("TELEFONO_SECUNDARIO"))) return true;

        String emailActual = txfEmail.getText() != null ? txfEmail.getText().trim() : "";
        if (!emailActual.equals(valoresOriginales.get("EMAIL"))) return true;

        String direccionActual = txfAddress.getText() != null ? txfAddress.getText().trim() : "";
        if (!direccionActual.equals(valoresOriginales.get("DIRECCION"))) return true;

        return false; 
    }

    
    @FXML
    private void onActionBtnExit(ActionEvent event) {
        if (hayCambiosNoGuardados()) {
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cambios no guardados");
            alert.setHeaderText(null);
            alert.setContentText(getLanguageString("msg.unsaved.changes") + "\n" + getLanguageString("msg.changes.notsaved"));

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    
                    FlowController.getInstance().goHome();
                }
                
            });
        } else {
            
            FlowController.getInstance().goHome();
        }
    }

    private String getLanguageString(String key) {
        return FlowController.getInstance().getLanguage().getString(key);
    }

}
