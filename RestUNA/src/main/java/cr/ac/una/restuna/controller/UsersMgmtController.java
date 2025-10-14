/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.service.UsuarioService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class UsersMgmtController extends Controller implements Initializable {

    @FXML
    private MFXTextField txfSearch;
    @FXML
    private MFXFilterComboBox<String> cmbRole;
    @FXML
    private MFXFilterComboBox<String> cmbStatus;
    @FXML
    private MFXButton btnAdd;
    @FXML
    private TreeTableColumn<UserRow, String> tbcUser;
    @FXML
    private TreeTableColumn<UserRow, String> tbcRole;
    @FXML
    private TreeTableColumn<UserRow, String> tbcStatus;
    @FXML
    private JFXTreeTableView<UserRow> tbvUsers;
    @FXML
    private MFXScrollPane tableRoot;

    private final ObservableList<UserRow> userList = FXCollections.observableArrayList();
    private UsuarioService usuarioService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar servicio
        usuarioService = new UsuarioService();
        
        // Configurar tabla
        tbvUsers.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvUsers.prefWidthProperty().bind(tableRoot.widthProperty());

        // Configurar ComboBoxes de filtros
        cmbRole.getItems().addAll("ADMINISTRADOR", "CAJERO", "SALONERO");
        cmbStatus.getItems().addAll("A", "I"); // A = Activo, I = Inactivo

        // Configurar columnas de la tabla (estructura simplificada)
        tbcUser.setCellValueFactory(x -> x.getValue().getValue().getUsername());
        tbcRole.setCellValueFactory(x -> x.getValue().getValue().getRole());
        tbcStatus.setCellValueFactory(x -> x.getValue().getValue().getStatus());

        // Configurar raíz de la tabla
        TreeItem<UserRow> root = new RecursiveTreeItem<>(userList, RecursiveTreeObject::getChildren);
        tbvUsers.setRoot(root);
        tbvUsers.setShowRoot(false);

        // Configurar listeners para filtros
        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> filters());
        cmbRole.valueProperty().addListener((obs, oldVal, newVal) -> filters());
        cmbStatus.valueProperty().addListener((obs, oldVal, newVal) -> filters());

        // Cargar usuarios desde el servidor
        cargarUsuarios();
    }
    
    /**
     * Carga todos los usuarios desde el servidor
     */
    private void cargarUsuarios() {
        // Mostrar indicador de carga si es necesario
        btnAdd.setDisable(true);
        btnAdd.setText("Cargando...");
        
        Task<Respuesta> loadTask = new Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                // Usar el método simplificado para obtener todos los usuarios
                return usuarioService.obtenerTodosLosUsuarios();
            }
        };
        
        loadTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                Respuesta respuesta = loadTask.getValue();
                
                if (respuesta != null && respuesta.getEstado()) {
                    try {
                        // La respuesta contiene el JSON completo, necesitamos extraer el array de usuarios
                        String jsonCompleto = (String) respuesta.getResultado("Usuarios");
                        
                        if (jsonCompleto != null && !jsonCompleto.trim().isEmpty()) {
                            // Extraer solo el array de usuarios del JSON anidado
                            String usuariosArray = extraerArrayUsuarios(jsonCompleto);
                            if (usuariosArray != null) {
                                procesarUsuariosDesdeJson(usuariosArray);
                            } else {
                                System.err.println("No se pudo extraer el array de usuarios del JSON");
                                showMessage("Error procesando la respuesta del servidor");
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Error procesando usuarios: " + ex.getMessage());
                        showMessage("Error procesando la lista de usuarios");
                    }
                } else {
                    String mensaje = respuesta != null ? respuesta.getMensaje() : "Error desconocido";
                    
                    // Log técnico para desarrolladores (NO mostrar al usuario)
                    System.err.println("Error del servidor al cargar usuarios: " + mensaje);
                    
                    // Verificar si es un error HTML (404, 500, etc.)
                    if (mensaje.contains("<!DOCTYPE") || mensaje.contains("<html>")) {
                        System.err.println("Servidor devolvió página de error HTML - Endpoint no encontrado");
                        showMessage("El servicio no está disponible. Contacte al administrador.");
                    } else {
                        // Mensaje amigable para el usuario (SIN detalles técnicos)
                        showMessage("No se pudieron cargar los usuarios. Verifique la conexión.");
                    }
                }
                
                // Restaurar botón
                btnAdd.setDisable(false);
                btnAdd.setText("Añadir");
            });
        });
        
        loadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                Throwable exception = loadTask.getException();
                
                // Log técnico SOLO en consola para desarrolladores
                System.err.println("Error de conexión al cargar usuarios: " + exception.getMessage());
                if (exception.getCause() != null) {
                    System.err.println("Causa: " + exception.getCause().getMessage());
                }
                
                // Mensaje simple y amigable para el usuario
                showMessage("No se pudo conectar al servidor.");
                
                // Restaurar botón
                btnAdd.setDisable(false);
                btnAdd.setText("Añadir");
            });
        });
        
        // Ejecutar tarea en background
        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    @Override
    public void initialize() {

    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_USER, new Stage(), false);
    }

    public void addUser(String username, String role, String status) {
        for (UserRow user : userList) {
            if (user.getUsername().get().equalsIgnoreCase(username)) {
                showMessage("Usuario ya existente: " + username);
                return;
            }
        }

        userList.add(new UserRow(username, role, status));
        filters();
    }

    private void filters() {
        String search = txfSearch.getText() == null ? "" : txfSearch.getText().toLowerCase();
        String filterRol = cmbRole.getValue();
        String filterStatus = cmbStatus.getValue();
        
        ObservableList<UserRow> filter = userList.filtered(x -> 
            x.getUsername().get().toLowerCase().contains(search)
        ).filtered(f -> filterRol == null || filterRol.isEmpty() || f.getRole().get().equals(filterRol)
        ).filtered(s -> filterStatus == null || filterStatus.isEmpty() || s.getStatus().get().equals(filterStatus));
        
        TreeItem<UserRow> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
        tbvUsers.setRoot(root);
        tbvUsers.setShowRoot(false);
    }
    
    /**
     * Procesa la lista de usuarios desde JSON y los agrega a la tabla
     */
    private void procesarUsuariosDesdeJson(String usuariosJson) {
        try {
            userList.clear();
            
            // Usar regex más flexible que encuentra cada objeto JSON completo
            java.util.regex.Pattern objetoPattern = java.util.regex.Pattern.compile(
                "\\{[^{}]*\\}",
                java.util.regex.Pattern.CASE_INSENSITIVE);
            
            java.util.regex.Matcher objetoMatcher = objetoPattern.matcher(usuariosJson);
            
            // Patrones individuales para extraer cada campo por separado
            java.util.regex.Pattern usuarioPattern = java.util.regex.Pattern.compile("\"usuario\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Pattern rolPattern = java.util.regex.Pattern.compile("\"rol\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Pattern estadoPattern = java.util.regex.Pattern.compile("\"estado\"\\s*:\\s*\"([^\"]+)\"");
            
            while (objetoMatcher.find()) {
                String objetoJson = objetoMatcher.group();
                
                // Extraer cada campo por separado
                java.util.regex.Matcher usuarioMatcher = usuarioPattern.matcher(objetoJson);
                java.util.regex.Matcher rolMatcher = rolPattern.matcher(objetoJson);
                java.util.regex.Matcher estadoMatcher = estadoPattern.matcher(objetoJson);
                
                if (usuarioMatcher.find() && rolMatcher.find() && estadoMatcher.find()) {
                    String usuario = usuarioMatcher.group(1);
                    String rol = rolMatcher.group(1);
                    String estado = estadoMatcher.group(1);
                    
                    // Crear UserRow con la estructura simplificada
                    userList.add(new UserRow(usuario, rol, estado));
                }
            }
            
            // Actualizar la vista
            filters();
            
            System.out.println("Usuarios cargados: " + userList.size());
            
        } catch (Exception e) {
            System.err.println("Error procesando usuarios desde JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extraerArrayUsuarios(String jsonCompleto) {
        try {
            System.out.println("=== EXTRAYENDO ARRAY USUARIOS ===");
            System.out.println("JSON completo a procesar: " + jsonCompleto);
            
            // Buscar el array de usuarios dentro de "resultados":{"Usuarios":[...]}
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "\"resultados\"\\s*:\\s*\\{[^}]*\"Usuarios\"\\s*:\\s*(\\[[^\\]]*\\])",
                java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
            
            java.util.regex.Matcher matcher = pattern.matcher(jsonCompleto);
            
            if (matcher.find()) {
                String array = matcher.group(1);
                System.out.println("Array encontrado: " + array);
                return array; // Devuelve solo el array [...]
            } else {
                System.err.println("No se encontró el patrón 'resultados':{'Usuarios':[...]}");
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Error extrayendo array de usuarios: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
