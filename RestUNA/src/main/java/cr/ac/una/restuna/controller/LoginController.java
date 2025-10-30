package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.controller.Controller;
import cr.ac.una.restuna.model.UsuarioDto;
import cr.ac.una.restuna.model.ParametroDto;
import cr.ac.una.restuna.service.UsuarioService;
import cr.ac.una.restuna.service.ParametroService;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Format;
import cr.ac.una.restuna.util.UserSession;
import cr.ac.una.restuna.util.JsonParser;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

public class LoginController extends Controller implements Initializable {

    @FXML
    private VBox loginRoot;
    @FXML
    private MFXTextField txfUser;
    @FXML
    private MFXPasswordField pswPassword;
    @FXML
    private MFXButton btnSignIn;
    
    @FXML
    private MFXComboBox<String> cmBoxRol;

    @FXML
    private WebView webRoot;

    private UsuarioService usuarioService;
    private ParametroService parametroService;

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar clientes REST primero
        usuarioService = new UsuarioService();
        parametroService = new ParametroService();
        
        // Usar Platform.runLater para asegurar que todos los componentes estén inicializados
        Platform.runLater(() -> {
            initializeControls();
            initializeWebView();
        });
    }
    
    private void initializeControls() {
        try {
            if (txfUser != null) {
                txfUser.delegateSetTextFormatter(Format.getInstance().lettersFormat(20));
            }
            
            if (pswPassword != null) {
                pswPassword.delegateSetTextFormatter(Format.getInstance().lettersFormat(20));
            }
            
            // Configurar ComboBox de roles si está disponible
            if (cmBoxRol != null) {
                cmBoxRol.getItems().addAll("Administrador", "Cajero", "Salonero");
                cmBoxRol.setValue("Salonero"); // Valor por defecto
            }
        } catch (Exception e) {
            System.err.println("Error initializing controls: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeWebView() {
        if (webRoot != null) {
            try {
                WebEngine webEngine = webRoot.getEngine();
                String svgPath = getClass().getResource("/cr/ac/una/restuna/resources/beany.svg").toExternalForm();
                String html = "<html><body style='display:flex; justify-content:center; align-items:center; height:100%; margin:0;'>"
                        + "<img src='" + svgPath + "'/>"
                        + "</body></html>";
                webEngine.loadContent(html);
            } catch (Exception e) {
                System.err.println("Error loading WebView content: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onKeyPressedPswPassword(KeyEvent event) {
    }

    @FXML
    private void onActionBtnSignIn(ActionEvent event) {
        // Validar campos
        String usuario = txfUser.getText();
        String password = pswPassword.getText();
        
        if (usuario == null || usuario.trim().isEmpty()) {
            mostrarAlerta("Error", "El nombre de usuario es obligatorio");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            mostrarAlerta("Error", "La contraseña es obligatoria");
            return;
        }
        
        // El rol es opcional para el login, se validará con el servidor
        
        // Deshabilitar botón durante la autenticación
        btnSignIn.setDisable(true);
        btnSignIn.setText(FlowController.getInstance().getLanguage().getString("btn.authenticating"));
        
        // Ejecutar autenticación en background thread
        Task<Respuesta> loginTask = new Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                return usuarioService.getUsuario(usuario, password);
            }
        };
        
        loginTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                Respuesta respuesta = loginTask.getValue();
                
                if (respuesta != null && respuesta.getEstado()) {
                    // La respuesta contiene el JSON del usuario
                    String usuarioJson = (String) respuesta.getResultado("Usuario");
                    
                    if (usuarioJson != null && !usuarioJson.trim().isEmpty()) {
                        // Extraer el rol del usuario del JSON
                        String rolUsuario = extraerRolDelJson(usuarioJson);
                        String rolSeleccionado = cmBoxRol.getValue();
                        
                        // Validar que el rol del usuario coincida con el seleccionado
                        if (rolUsuario != null && rolSeleccionado != null && validarRol(rolUsuario, rolSeleccionado)) {
                            // Login exitoso - crear DTO del usuario para la sesión
                            UsuarioDto usuarioDto = crearUsuarioDesdeJson(usuarioJson);
                            UserSession.getInstance().setCurrentUser(usuarioDto);
                            
                            // Cargar idioma preferido del usuario antes de ir al Main
                            cargarIdiomaUsuario(usuarioDto.getIdUsuario());
                            
                            FlowController.getInstance().goMain(AppKeys.MAIN);
                        } else {
                            mostrarAlerta("Error de Autorización", 
                                "El usuario '" + usuario + "' no tiene permisos para el rol '" + rolSeleccionado + "'.\n" +
                                "Rol del usuario: " + (rolUsuario != null ? rolUsuario : "Desconocido"));
                        }
                    } else {
                        mostrarAlerta("Error", "El servidor no retornó los datos del usuario. \n" +
                                     "Verificar que el servidor WsRestUNA esté ejecutándose.");
                    }
                } else {
                    String mensaje = respuesta != null ? respuesta.getMensaje() : "Error desconocido";
                    mostrarAlerta("Error de Autenticación", mensaje);
                }
                
                // Rehabilitar botón
                btnSignIn.setDisable(false);
                btnSignIn.setText(FlowController.getInstance().getLanguage().getString("btn.login"));
            });
        });
        
        loginTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                Throwable exception = loginTask.getException();
                mostrarAlerta("Error de Conexión", 
                    "No se pudo conectar con el servidor: " + exception.getMessage());
                
                // Rehabilitar botón
                btnSignIn.setDisable(false);
                btnSignIn.setText("Ingresar");
            });
        });
        
        // Ejecutar tarea en background
        Thread loginThread = new Thread(loginTask);
        loginThread.setDaemon(true);
        loginThread.start();
    }
    
    /**
     * Muestra una alerta de error
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Extrae el rol del usuario del JSON de respuesta usando regex simple
     */
    private String extraerRolDelJson(String json) {
        try {
            // El JSON puede venir como respuesta completa del servidor o como datos del usuario
            // Intentar diferentes patrones para extraer el rol
            String[] patronesRol = {
                "\"rol\"\\s*:\\s*\"([^\"]+)\"",           // "rol":"ADMINISTRADOR"
                "\"ROL\"\\s*:\\s*\"([^\"]+)\"",           // "ROL":"ADMINISTRADOR"  
                "\"role\"\\s*:\\s*\"([^\"]+)\"",          // "role":"ADMINISTRADOR"
                "rol[^:]*:\\s*[\"']([^\"']+)[\"']",       // rol: "ADMINISTRADOR" con variantes
                "ROL[^:]*:\\s*[\"']([^\"']+)[\"']"        // ROL: "ADMINISTRADOR" con variantes
            };
            
            for (String patron : patronesRol) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patron, java.util.regex.Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(json);
                
                if (matcher.find()) {
                    String rol = matcher.group(1);
                    return rol;
                }
            }
            
            // Si no encuentra rol directamente, buscar en la estructura del resultado
            // Buscar: "Usuario": { ... "rol": "ADMINISTRADOR" ... }
            java.util.regex.Pattern usuarioPattern = java.util.regex.Pattern.compile(
                "\"Usuario\"\\s*:\\s*\\{[^}]*\"rol\"\\s*:\\s*\"([^\"]+)\"[^}]*\\}", 
                java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher usuarioMatcher = usuarioPattern.matcher(json);
            
            if (usuarioMatcher.find()) {
                String rol = usuarioMatcher.group(1);
                return rol;
            }
            
        } catch (Exception e) {
            System.err.println("Error extrayendo rol del JSON: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Valida que el rol del usuario coincida con el rol seleccionado
     */
    private boolean validarRol(String rolUsuario, String rolSeleccionado) {
        if (rolUsuario == null || rolSeleccionado == null) {
            return false;
        }
        
        // Mapeo de roles de la interfaz a roles de la base de datos
        String rolBD = mapearRolInterfazABD(rolSeleccionado);
        
        // Comparar ignorando case
        return rolUsuario.toUpperCase().equals(rolBD.toUpperCase());
    }
    
    /**
     * Mapea los roles de la interfaz a los roles de la base de datos
     */
    private String mapearRolInterfazABD(String rolInterfaz) {
        switch (rolInterfaz) {
            case "Administrador":
                return "ADMINISTRADOR";
            case "Cajero":
                return "CAJERO";
            case "Salonero":
                return "SALONERO";
            default:
                return rolInterfaz; // En caso de que ya venga en el formato correcto
        }
    }
    
    /**
     * Crea un UsuarioDto básico desde el JSON de respuesta
     * Extrae los campos principales para la sesión
     */
    private UsuarioDto crearUsuarioDesdeJson(String json) {
        try {
            UsuarioDto usuario = new UsuarioDto();
            
            // Extraer ID
            String idPattern = "\"idUsuario\"\\s*:\\s*(\\d+)";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(idPattern);
            java.util.regex.Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                usuario.setIdUsuario(Long.parseLong(matcher.group(1)));
            }
            
            // Extraer usuario
            String usuarioPattern = "\"usuario\"\\s*:\\s*\"([^\"]+)\"";
            pattern = java.util.regex.Pattern.compile(usuarioPattern);
            matcher = pattern.matcher(json);
            if (matcher.find()) {
                usuario.setUsuario(matcher.group(1));
            }
            
            // Extraer rol (ya lo tenemos del método anterior)
            String rol = extraerRolDelJson(json);
            usuario.setRol(rol);
            
            // Extraer estado
            String estadoPattern = "\"estado\"\\s*:\\s*\"([^\"]+)\"";
            pattern = java.util.regex.Pattern.compile(estadoPattern);
            matcher = pattern.matcher(json);
            if (matcher.find()) {
                usuario.setEstado(matcher.group(1));
            }
            
            return usuario;
        } catch (Exception e) {
            System.err.println("Error creando UsuarioDto desde JSON: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Carga el idioma preferido del usuario desde sus parámetros guardados
     */
    private void cargarIdiomaUsuario(Long idUsuario) {
        if (idUsuario == null) {
            return;
        }
        
        try {
            // Obtener parámetros del usuario
            Respuesta respuesta = parametroService.getParametrosPorUsuario(idUsuario);
            
            if (respuesta != null && respuesta.getEstado()) {
                String jsonArray = (String) respuesta.getResultado("Parametros");
                
                if (jsonArray != null && !jsonArray.trim().isEmpty() && !jsonArray.equals("[]")) {
                    // Buscar el parámetro IDIOMA en el JSON
                    String idiomaUsuario = extraerIdiomaDelJson(jsonArray);
                    
                    if (idiomaUsuario != null && !idiomaUsuario.trim().isEmpty()) {
                        // Cambiar el idioma del sistema
                        cambiarIdiomaSistema(idiomaUsuario);
                        System.out.println("Idioma cargado desde parámetros: " + idiomaUsuario);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando idioma del usuario: " + e.getMessage());
            // No bloqueamos el login si falla la carga del idioma
        }
    }
    
    /**
     * Extrae el valor del parámetro IDIOMA del JSON de parámetros
     */
    private String extraerIdiomaDelJson(String jsonArray) {
        try {
            // Buscar el objeto que contiene "clave":"IDIOMA"
            Pattern pattern = Pattern.compile("\\{[^}]*\"clave\"\\s*:\\s*\"IDIOMA\"[^}]*\"valor\"\\s*:\\s*\"([^\"]+)\"[^}]*\\}");
            Matcher matcher = pattern.matcher(jsonArray);
            
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo idioma del JSON: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Cambia el idioma de todo el sistema
     */
    private void cambiarIdiomaSistema(String codigoIdioma) {
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

}
