package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.UsuarioDto;
import cr.ac.una.restuna.service.UsuarioService;
import cr.ac.una.restuna.service.ParametroService;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Format;
import cr.ac.una.restuna.util.UserSession;
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
        
        usuarioService = new UsuarioService();
        parametroService = new ParametroService();
        
        
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
            
            
            if (cmBoxRol != null) {
                cmBoxRol.getItems().addAll("Administrador", "Cajero", "Salonero");
                cmBoxRol.setValue("Salonero"); 
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
        
        
        
        
        btnSignIn.setDisable(true);
        btnSignIn.setText(FlowController.getInstance().getLanguage().getString("btn.authenticating"));
        
        
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
                    
                    String usuarioJson = (String) respuesta.getResultado("Usuario");
                    
                    if (usuarioJson != null && !usuarioJson.trim().isEmpty()) {
                        
                        String rolUsuario = extraerRolDelJson(usuarioJson);
                        String rolSeleccionado = cmBoxRol.getValue();
                        
                        
                        if (rolUsuario != null && rolSeleccionado != null && validarRol(rolUsuario, rolSeleccionado)) {
                            
                            UsuarioDto usuarioDto = crearUsuarioDesdeJson(usuarioJson);
                            UserSession.getInstance().setCurrentUser(usuarioDto);
                            
                            
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
                
                
                btnSignIn.setDisable(false);
                btnSignIn.setText(FlowController.getInstance().getLanguage().getString("btn.login"));
            });
        });
        
        loginTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                Throwable exception = loginTask.getException();
                mostrarAlerta("Error de Conexión", 
                    "No se pudo conectar con el servidor: " + exception.getMessage());
                
                
                btnSignIn.setDisable(false);
                btnSignIn.setText("Ingresar");
            });
        });
        
        
        Thread loginThread = new Thread(loginTask);
        loginThread.setDaemon(true);
        loginThread.start();
    }
    
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    
    private String extraerRolDelJson(String json) {
        try {
            
            
            String[] patronesRol = {
                "\"rol\"\\s*:\\s*\"([^\"]+)\"",           
                "\"ROL\"\\s*:\\s*\"([^\"]+)\"",           
                "\"role\"\\s*:\\s*\"([^\"]+)\"",          
                "rol[^:]*:\\s*[\"']([^\"']+)[\"']",       
                "ROL[^:]*:\\s*[\"']([^\"']+)[\"']"        
            };
            
            for (String patron : patronesRol) {
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patron, java.util.regex.Pattern.CASE_INSENSITIVE);
                java.util.regex.Matcher matcher = pattern.matcher(json);
                
                if (matcher.find()) {
                    String rol = matcher.group(1);
                    return rol;
                }
            }
            
            
            
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
    
    
    private boolean validarRol(String rolUsuario, String rolSeleccionado) {
        if (rolUsuario == null || rolSeleccionado == null) {
            return false;
        }
        
        
        String rolBD = mapearRolInterfazABD(rolSeleccionado);
        
        
        return rolUsuario.toUpperCase().equals(rolBD.toUpperCase());
    }
    
    
    private String mapearRolInterfazABD(String rolInterfaz) {
        switch (rolInterfaz) {
            case "Administrador":
                return "ADMINISTRADOR";
            case "Cajero":
                return "CAJERO";
            case "Salonero":
                return "SALONERO";
            default:
                return rolInterfaz; 
        }
    }
    
    
    private UsuarioDto crearUsuarioDesdeJson(String json) {
        try {
            UsuarioDto usuario = new UsuarioDto();
            
            
            String idPattern = "\"idUsuario\"\\s*:\\s*(\\d+)";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(idPattern);
            java.util.regex.Matcher matcher = pattern.matcher(json);
            if (matcher.find()) {
                usuario.setIdUsuario(Long.parseLong(matcher.group(1)));
            }
            
            
            String usuarioPattern = "\"usuario\"\\s*:\\s*\"([^\"]+)\"";
            pattern = java.util.regex.Pattern.compile(usuarioPattern);
            matcher = pattern.matcher(json);
            if (matcher.find()) {
                usuario.setUsuario(matcher.group(1));
            }
            
            
            String rol = extraerRolDelJson(json);
            usuario.setRol(rol);
            
            
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
    
    
    private void cargarIdiomaUsuario(Long idUsuario) {
        if (idUsuario == null) {
            return;
        }
        
        try {
            
            Respuesta respuesta = parametroService.getParametrosPorUsuario(idUsuario);
            
            if (respuesta != null && respuesta.getEstado()) {
                String jsonArray = (String) respuesta.getResultado("Parametros");
                
                if (jsonArray != null && !jsonArray.trim().isEmpty() && !jsonArray.equals("[]")) {
                    
                    String idiomaUsuario = extraerIdiomaDelJson(jsonArray);
                    
                    if (idiomaUsuario != null && !idiomaUsuario.trim().isEmpty()) {
                        
                        cambiarIdiomaSistema(idiomaUsuario);
                        System.out.println("Idioma cargado desde parámetros: " + idiomaUsuario);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando idioma del usuario: " + e.getMessage());
            
        }
    }
    
    
    private String extraerIdiomaDelJson(String jsonArray) {
        try {
            
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
    
    
    private void cambiarIdiomaSistema(String codigoIdioma) {
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

}
