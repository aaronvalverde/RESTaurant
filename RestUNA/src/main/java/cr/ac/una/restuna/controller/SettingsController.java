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

/**
 * FXML Controller class
 *
 * @author aaron
 */
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
        
        // Agregar validaciones
        TextFieldValidator.addPhoneValidation(txfPhone);
        TextFieldValidator.addPhoneValidation(txfSecondaryPhone);
        TextFieldValidator.addEmailValidation(txfEmail);

        settingsContainer.prefHeightProperty().bind(settingsRoot.heightProperty());
        settingsContainer.prefWidthProperty().bind(settingsRoot.widthProperty());
        
        // Cargar parámetros del usuario actual
        cargarParametros();
    }

    @Override
    public void initialize() {
    }

    private void configurarSpinners() {
        // Los MFXSpinner necesitan tener su SpinnerModel configurado antes de usarlos
        // Creamos modelos con rangos de 0-100 con incremento de 0.5
        
        if (spinnerIVA != null) {
            DoubleSpinnerModel modelIVA = new DoubleSpinnerModel();
            modelIVA.setMin(0.0);
            modelIVA.setMax(100.0);
            modelIVA.setIncrement(0.5);
            modelIVA.setValue(13.0); // Valor inicial por defecto (IVA Costa Rica)
            spinnerIVA.setSpinnerModel(modelIVA);
        }
        
        if (spinnerServiceTax != null) {
            DoubleSpinnerModel modelService = new DoubleSpinnerModel();
            modelService.setMin(0.0);
            modelService.setMax(100.0);
            modelService.setIncrement(0.5);
            modelService.setValue(10.0); // Valor inicial por defecto (Servicio Costa Rica)
            spinnerServiceTax.setSpinnerModel(modelService);
        }
        
        if (spinnerCashierDiscount != null) {
            DoubleSpinnerModel modelDiscount = new DoubleSpinnerModel();
            modelDiscount.setMin(0.0);
            modelDiscount.setMax(100.0);
            modelDiscount.setIncrement(0.5);
            modelDiscount.setValue(5.0); // Valor inicial por defecto
            spinnerCashierDiscount.setSpinnerModel(modelDiscount);
        }
    }

    private void cargarParametros() {
        if (!UserSession.getInstance().isAuthenticated()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sesión");
            alert.setHeaderText(null);
            alert.setContentText("No hay un usuario activo en la sesión");
            alert.showAndWait();
            return;
        }

        Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
        if (idUsuario == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Usuario");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo obtener el ID del usuario");
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
                        // No hay parámetros guardados aún, aplicar valores por defecto visuales
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
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error al cargar la configuración: " + getException().getMessage());
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

        // Extraer cada objeto del array JSON
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
        // Aplicar idioma
        ParametroDto idioma = parametrosMap.get("IDIOMA");
        if (idioma != null && idioma.getValor() != null) {
            if ("es".equalsIgnoreCase(idioma.getValor())) {
                cmbLanguage.selectItem("Español");
            } else {
                cmbLanguage.selectItem("English");
            }
        }

        // Aplicar moneda
        ParametroDto moneda = parametrosMap.get("MONEDA");
        if (moneda != null && moneda.getValor() != null) {
            cmbCurrency.selectItem(moneda.getValor());
        }

        // Aplicar nombre del restaurante
        ParametroDto nombreRestaurante = parametrosMap.get("NOMBRE_RESTAURANTE");
        if (nombreRestaurante != null && nombreRestaurante.getValor() != null) {
            txfRestaurantName.setText(nombreRestaurante.getValor());
        }

        // Aplicar impuesto de venta (IVA)
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

        // Aplicar impuesto de servicio
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

        // Aplicar descuento máximo cajero
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

        // Aplicar teléfono
        ParametroDto telefono = parametrosMap.get("TELEFONO");
        if (telefono != null && telefono.getValor() != null) {
            txfPhone.setText(telefono.getValor());
        }

        // Aplicar teléfono secundario
        ParametroDto telefonoSecundario = parametrosMap.get("TELEFONO_SECUNDARIO");
        if (telefonoSecundario != null && telefonoSecundario.getValor() != null) {
            txfSecondaryPhone.setText(telefonoSecundario.getValor());
        }

        // Aplicar email
        ParametroDto email = parametrosMap.get("EMAIL");
        if (email != null && email.getValor() != null) {
            txfEmail.setText(email.getValor());
        }

        // Aplicar dirección
        ParametroDto direccion = parametrosMap.get("DIRECCION");
        if (direccion != null && direccion.getValor() != null) {
            txfAddress.setText(direccion.getValor());
        }
        
        // Guardar valores originales para detectar cambios
        guardarValoresOriginales();
    }

    /**
     * Aplica valores por defecto cuando no hay parámetros guardados en la BD
     */
    private void aplicarValoresPorDefecto() {
        // Idioma por defecto: español
        cmbLanguage.selectItem("Español");
        
        // Moneda por defecto: Colón
        cmbCurrency.selectItem("CRC - Colón");
        
        // Nombre restaurante vacío (el usuario debe ingresarlo)
        txfRestaurantName.setText("");
        
        // Spinners con valores por defecto de Costa Rica
        if (spinnerIVA != null) {
            try {
                if (spinnerIVA.getSpinnerModel() != null) {
                    spinnerIVA.setValue(13.0); // IVA estándar en Costa Rica
                }
            } catch (Exception e) {
                System.err.println("Error al establecer valor por defecto IVA: " + e.getMessage());
            }
        }
        
        if (spinnerServiceTax != null) {
            try {
                if (spinnerServiceTax.getSpinnerModel() != null) {
                    spinnerServiceTax.setValue(10.0); // Servicio estándar en Costa Rica
                }
            } catch (Exception e) {
                System.err.println("Error al establecer valor por defecto servicio: " + e.getMessage());
            }
        }
        
        if (spinnerCashierDiscount != null) {
            try {
                if (spinnerCashierDiscount.getSpinnerModel() != null) {
                    spinnerCashierDiscount.setValue(5.0); // Descuento razonable por defecto
                }
            } catch (Exception e) {
                System.err.println("Error al establecer valor por defecto descuento: " + e.getMessage());
            }
        }
        
        // Campos de contacto vacíos
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

        // Crear lista de parámetros a guardar
        parametrosAGuardar.add(crearParametro("IDIOMA", obtenerCodigoIdioma(), 
                "Idioma de la interfaz", "STRING", idUsuario));
        
        parametrosAGuardar.add(crearParametro("MONEDA", cmbCurrency.getSelectedItem(), 
                "Moneda utilizada en el restaurante", "STRING", idUsuario));
        
        parametrosAGuardar.add(crearParametro("NOMBRE_RESTAURANTE", txfRestaurantName.getText().trim(), 
                "Nombre del restaurante", "STRING", idUsuario));
        
        // Obtener valores de spinners con manejo seguro
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
        
        // Solo agregar teléfono secundario si no está vacío (campo opcional)
        if (txfSecondaryPhone.getText() != null && !txfSecondaryPhone.getText().trim().isEmpty()) {
            parametrosAGuardar.add(crearParametro("TELEFONO_SECUNDARIO", txfSecondaryPhone.getText().trim(), 
                    "Teléfono secundario del restaurante", "STRING", idUsuario));
        }
        
        parametrosAGuardar.add(crearParametro("EMAIL", txfEmail.getText().trim(), 
                "Correo electrónico del restaurante", "STRING", idUsuario));
        
        parametrosAGuardar.add(crearParametro("DIRECCION", txfAddress.getText().trim(), 
                "Dirección física del restaurante", "STRING", idUsuario));

        // Guardar en el servidor
        guardarParametrosEnServidor(parametrosAGuardar);
    }

    private ParametroDto crearParametro(String clave, String valor, String descripcion, 
                                        String tipoDato, Long idUsuario) {
        ParametroDto parametro = parametrosMap.get(clave);
        if (parametro == null) {
            parametro = new ParametroDto();
            parametro.setClave(clave);
        }
        
        // Asegurar que el valor nunca sea null o vacío para campos obligatorios
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
        // Detectar si cambió el idioma
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
                            // Mostrar mensaje de que se requiere logout
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Cambio de idioma");
                            alert.setHeaderText(null);
                            alert.setContentText("La configuración se guardó correctamente.\n\n" +
                                    "El cambio de idioma requiere cerrar sesión.\n" +
                                    "Por favor, inicie sesión nuevamente.");
                            alert.showAndWait();
                            
                            // Cerrar sesión y volver al login
                            UserSession.getInstance().clearSession();
                            FlowController.getInstance().goMain(AppKeys.LOGIN);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Configuración");
                            alert.setHeaderText(null);
                            alert.setContentText("La configuración se guardó correctamente");
                            alert.showAndWait();
                            
                            // Recargar parámetros
                            String jsonArray = (String) respuesta.getResultado("Parametros");
                            procesarParametros(jsonArray);
                            
                            // Actualizar valores originales después de guardar
                            guardarValoresOriginales();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Error al guardar la configuración: " + respuesta.getMensaje());
                        alert.showAndWait();
                    }
                });
                
                return null;
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error al guardar la configuración: " + getException().getMessage());
                    alert.showAndWait();
                });
            }
        };

        new Thread(task).start();
    }

    private boolean validarCampos() {
        // Validar idioma (combobox)
        if (cmbLanguage.getSelectedItem() == null || cmbLanguage.getSelectedItem().trim().isEmpty()) {
            mostrarError("El idioma del sistema es obligatorio", null);
            return false;
        }
        
        // Validar moneda (combobox)
        if (cmbCurrency.getSelectedItem() == null || cmbCurrency.getSelectedItem().trim().isEmpty()) {
            mostrarError("La moneda es obligatoria", null);
            return false;
        }
        
        // Validar nombre del restaurante (obligatorio)
        if (txfRestaurantName.getText() == null || txfRestaurantName.getText().trim().isEmpty()) {
            mostrarError("El nombre del restaurante es obligatorio", txfRestaurantName);
            return false;
        }
        
        // Validar teléfono principal (obligatorio)
        if (txfPhone.getText() == null || txfPhone.getText().trim().isEmpty()) {
            mostrarError("El teléfono principal es obligatorio", txfPhone);
            return false;
        }
        
        // Validar formato de teléfono principal
        String phoneRegex = "^[+]?[0-9\\s()-]{7,20}$";
        if (!txfPhone.getText().trim().matches(phoneRegex)) {
            mostrarError("El formato del teléfono principal no es válido", txfPhone);
            return false;
        }
        
        // Validar teléfono secundario (OPCIONAL - solo validar formato si tiene valor)
        if (txfSecondaryPhone.getText() != null && !txfSecondaryPhone.getText().trim().isEmpty()) {
            if (!txfSecondaryPhone.getText().trim().matches(phoneRegex)) {
                mostrarError("El formato del teléfono secundario no es válido", txfSecondaryPhone);
                return false;
            }
        }
        
        // Validar email (obligatorio)
        if (txfEmail.getText() == null || txfEmail.getText().trim().isEmpty()) {
            mostrarError("El correo electrónico es obligatorio", txfEmail);
            return false;
        }
        
        // Validar formato de email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!txfEmail.getText().trim().matches(emailRegex)) {
            mostrarError("El formato del correo electrónico no es válido", txfEmail);
            return false;
        }
        
        // Validar dirección (obligatoria)
        if (txfAddress.getText() == null || txfAddress.getText().trim().isEmpty()) {
            mostrarError("La dirección es obligatoria", txfAddress);
            return false;
        }

        // Validar que los spinners tengan valores válidos
        Double ivaValue = spinnerIVA != null && spinnerIVA.getValue() != null ? spinnerIVA.getValue() : 13.0;
        if (ivaValue < 0 || ivaValue > 100) {
            mostrarError("El impuesto de venta debe estar entre 0 y 100", null);
            return false;
        }

        Double serviceTaxValue = spinnerServiceTax != null && spinnerServiceTax.getValue() != null ? spinnerServiceTax.getValue() : 10.0;
        if (serviceTaxValue < 0 || serviceTaxValue > 100) {
            mostrarError("El impuesto de servicio debe estar entre 0 y 100", null);
            return false;
        }

        Double cashierDiscountValue = spinnerCashierDiscount != null && spinnerCashierDiscount.getValue() != null ? spinnerCashierDiscount.getValue() : 5.0;
        if (cashierDiscountValue < 0 || cashierDiscountValue > 100) {
            mostrarError("El descuento máximo debe estar entre 0 y 100", null);
            return false;
        }

        return true;
    }
    
    private void mostrarError(String mensaje, MFXTextField campo) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validación");
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
        // Recargar parámetros originales
        aplicarParametrosAUI();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configuración");
        alert.setHeaderText(null);
        alert.setContentText("Se cancelaron los cambios");
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
        
        // Seleccionar español por defecto si no hay nada seleccionado
        if (cmbLanguage.getSelectedItem() == null || cmbLanguage.getSelectedItem().isEmpty()) {
            cmbLanguage.selectItem("Español");
        }
    }

    private void loadCurrencyOptions() {
        cmbCurrency.getItems().clear();
        cmbCurrency.getItems().add("CRC - Colón");
        cmbCurrency.getItems().add("USD - Dólar");
        cmbCurrency.getItems().add("EUR - Euro");
        
        // Seleccionar CRC por defecto si no hay nada seleccionado
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
    
    /**
     * Aplica el cambio de idioma a todo el sistema
     */
    private void aplicarCambioDeIdioma(String codigoIdioma) {
        try {
            Locale locale;
            
            if ("es".equalsIgnoreCase(codigoIdioma)) {
                locale = Locale.of("es");
            } else if ("en".equalsIgnoreCase(codigoIdioma)) {
                locale = Locale.of("en");
            } else {
                // Por defecto usar español
                locale = Locale.of("es");
            }
            
            ResourceBundle bundle = ResourceBundle.getBundle("cr.ac.una.restuna.i18n.text", locale);
            FlowController.getInstance().setLanguage(bundle);
            
            System.out.println("Idioma del sistema cambiado a: " + locale.getLanguage());
        } catch (Exception e) {
            System.err.println("Error cambiando idioma del sistema: " + e.getMessage());
        }
    }
    
    /**
     * Guarda los valores actuales de los campos para detectar cambios posteriores
     */
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
    
    /**
     * Detecta si hay cambios no guardados en los campos
     */
    private boolean hayCambiosNoGuardados() {
        if (valoresOriginales.isEmpty()) {
            return false; // No hay valores originales, no hay cambios
        }
        
        // Comparar cada campo con su valor original
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
        
        return false; // No hay cambios
    }
    
    /**
     * Maneja el evento del botón Exit
     */
    @FXML
    private void onActionBtnExit(ActionEvent event) {
        if (hayCambiosNoGuardados()) {
            // Mostrar confirmación si hay cambios
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cambios no guardados");
            alert.setHeaderText(null);
            alert.setContentText("Hay cambios sin guardar. ¿Está seguro que desea salir?\nLos cambios no serán guardados.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    // Usuario confirmó salir sin guardar - restaurar contenido inicial
                    FlowController.getInstance().goHome();
                }
                // Si cancela, no hace nada y permanece en Settings
            });
        } else {
            // No hay cambios, salir directamente - restaurar contenido inicial
            FlowController.getInstance().goHome();
        }
    }
}
