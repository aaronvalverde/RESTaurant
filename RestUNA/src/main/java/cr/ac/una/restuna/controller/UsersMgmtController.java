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
                return usuarioService.getUsuarios("", "", "", ""); // Obtener todos los usuarios
            }
        };
        
        loadTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                Respuesta respuesta = loadTask.getValue();
                
                if (respuesta != null && respuesta.getEstado()) {
                    try {
                        // La respuesta contiene el JSON con la lista de usuarios
                        String usuariosJson = (String) respuesta.getResultado("Usuarios");
                        if (usuariosJson != null && !usuariosJson.trim().isEmpty()) {
                            procesarUsuariosDesdeJson(usuariosJson);
                        }
                    } catch (Exception ex) {
                        System.err.println("Error procesando usuarios: " + ex.getMessage());
                        showMessage("Error procesando la lista de usuarios");
                    }
                } else {
                    String mensaje = respuesta != null ? respuesta.getMensaje() : "Error desconocido";
                    showMessage("Error cargando usuarios: " + mensaje);
                }
                
                // Restaurar botón
                btnAdd.setDisable(false);
                btnAdd.setText("Añadir");
            });
        });
        
        loadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                Throwable exception = loadTask.getException();
                showMessage("Error de conexión: " + exception.getMessage());
                
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
            
            // El JSON viene como un array de usuarios: [{"usuario":"admin","rol":"ADMINISTRADOR",...}, ...]
            // Usaremos regex para extraer cada usuario
            java.util.regex.Pattern usuarioPattern = java.util.regex.Pattern.compile(
                "\\{[^}]*\"usuario\"\\s*:\\s*\"([^\"]+)\"[^}]*\"rol\"\\s*:\\s*\"([^\"]+)\"[^}]*\"estado\"\\s*:\\s*\"([^\"]+)\"[^}]*\\}",
                java.util.regex.Pattern.CASE_INSENSITIVE);
            
            java.util.regex.Matcher matcher = usuarioPattern.matcher(usuariosJson);
            
            while (matcher.find()) {
                String usuario = matcher.group(1);
                String rol = matcher.group(2);
                String estado = matcher.group(3);
                
                // Crear UserRow con la estructura simplificada
                userList.add(new UserRow(usuario, rol, estado));
            }
            
            // Actualizar la vista
            filters();
            
            System.out.println("Usuarios cargados: " + userList.size());
            
        } catch (Exception e) {
            System.err.println("Error procesando usuarios desde JSON: " + e.getMessage());
            e.printStackTrace();
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
