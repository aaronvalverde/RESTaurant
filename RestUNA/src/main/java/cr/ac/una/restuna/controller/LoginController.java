package cr.ac.una.restuna.controller;


import cr.ac.una.restuna.service.UsuarioService;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Format;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
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

    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar cliente REST primero
        usuarioService = new UsuarioService();
        
        // Usar Platform.runLater para asegurar que todos los componentes estén inicializados
        Platform.runLater(() -> {
            initializeControls();
            initializeWebView();
        });
    }
    
    private void initializeControls() {
        try {
            System.out.println("Initializing controls...");
            System.out.println("txfUser: " + (txfUser != null ? "OK" : "NULL"));
            System.out.println("pswPassword: " + (pswPassword != null ? "OK" : "NULL"));
            System.out.println("cmBoxRol: " + (cmBoxRol != null ? "OK" : "NULL"));
            System.out.println("webRoot: " + (webRoot != null ? "OK" : "NULL"));
            
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
                System.out.println("ComboBox configured successfully");
            } else {
                System.err.println("WARNING: cmBoxRol is null! Check FXML injection.");
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
        btnSignIn.setText("Autenticando...");
        
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
                        // Login exitoso
                        System.out.println("Login exitoso - Respuesta: " + usuarioJson);
                        
                        // TODO: Validar rol si es necesario
                        // TODO: Guardar usuario en sesión si necesario
                        // UserSession.setCurrentUser(usuarioAutenticado);
                        
                        FlowController.getInstance().goMain(AppKeys.MAIN);
                    } else {
                        mostrarAlerta("Error", "No se pudo obtener la información del usuario");
                    }
                } else {
                    String mensaje = respuesta != null ? respuesta.getMensaje() : "Error desconocido";
                    mostrarAlerta("Error de Autenticación", mensaje);
                }
                
                // Rehabilitar botón
                btnSignIn.setDisable(false);
                btnSignIn.setText("Ingresar");
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

}
