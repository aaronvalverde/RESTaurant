/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.model.UsuarioDto;
import cr.ac.una.restuna.service.UsuarioService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.UserRow;
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
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private MFXButton btnBack;
    @FXML
    private MFXButton btnClearFilters;
    @FXML
    private TreeTableColumn<UserRow, String> tbcUser;
    @FXML
    private TreeTableColumn<UserRow, String> tbcName;
    @FXML
    private TreeTableColumn<UserRow, String> tbcRole;
    @FXML
    private TreeTableColumn<UserRow, String> tbcStatus;
    @FXML
    private JFXTreeTableView<UserRow> tbvUsers;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<UserRow, Void> tbcActions;

    private final ObservableList<UserRow> userList = FXCollections.observableArrayList();
    private UsuarioService usuarioService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioService = new UsuarioService();

        tbvUsers.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvUsers.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbRole.getItems().addAll("Administrador", "Cajero", "Salonero");
        cmbStatus.getItems().addAll("Activo", "Inactivo");

        tbcUser.setCellValueFactory(x -> x.getValue().getValue().getUsername());
        tbcName.setCellValueFactory(x -> x.getValue().getValue().getName());
        tbcRole.setCellValueFactory(x -> x.getValue().getValue().getRoleDisplay()); 
        tbcStatus.setCellValueFactory(x -> x.getValue().getValue().getStatusDisplay());

        TreeItem<UserRow> root = new RecursiveTreeItem<>(userList, RecursiveTreeObject::getChildren);
        tbvUsers.setRoot(root);
        tbvUsers.setShowRoot(false);

        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> filters());
        cmbRole.valueProperty().addListener((obs, oldVal, newVal) -> filters());
        cmbStatus.valueProperty().addListener((obs, oldVal, newVal) -> filters());

        cargarUsuarios();
        setActionsColumn();
    }

    public void cargarUsuarios() {
        btnAdd.setDisable(true);
        btnAdd.setText("Cargando...");

        Task<Respuesta> loadTask = new Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                return usuarioService.obtenerTodosLosUsuarios(); // Obtener todos los usuarios
            }
        };

        loadTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                Respuesta respuesta = loadTask.getValue();

                if (respuesta != null && respuesta.getEstado()) {
                    try {
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

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    @Override
    public void initialize() {

    }

    @FXML
    void onActionBtnBack(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MANAGEMENT);
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        Task<NewUserController> loadTask = new Task<NewUserController>() {
            @Override
            protected NewUserController call() throws Exception {
                return (NewUserController) FlowController.getInstance()
                        .getController(AppKeys.NEW_USER);
            }
        };

        loadTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                NewUserController controller = loadTask.getValue();
                if (controller != null) {
                    controller.setParentController(this);
                    controller.clearFields(); // Limpiar campos para modo creación
                    
                    FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_USER, this.getStage(), false);
                } else {
                    showMessage("Error: No se pudo cargar la vista de nuevo usuario");
                }
            });
        });

        loadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showMessage("Error cargando vista: " + loadTask.getException().getMessage());
            });
        });

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    public void addUser(Long idUsuario, String username, String name, String role, String status) {
        // Verificar si el usuario ya existe en la lista local
        boolean encontrado = false;
        for (UserRow user : userList) {
            if (user.getUsername().get().equalsIgnoreCase(username)) {
                System.out.println("El usuario " + username + " ya existe en la lista. Actualizando...");
                // Si existe, actualizar sus datos
                user.getName().set(name);
                user.getRole().set(role);
                user.getStatus().set(status);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            System.out.println("Añadiendo nuevo usuario: " + username);
            userList.add(new UserRow(idUsuario, username, name, role, status));
        }
        
        TreeItem<UserRow> root = new RecursiveTreeItem<>(userList, RecursiveTreeObject::getChildren);
        tbvUsers.setRoot(null); // Limpiar primero
        tbvUsers.setRoot(root);  // Establecer nuevo root
        tbvUsers.setShowRoot(false);
        
        System.out.println("Usuario " + username + " procesado. Total usuarios: " + userList.size());
    }

    private void filters() {
        String search = txfSearch.getText() == null ? "" : txfSearch.getText().toLowerCase();
        String filterRol = cmbRole.getValue();
        String filterStatus = cmbStatus.getValue();

        ObservableList<UserRow> filter = userList.filtered(x
                -> x.getUsername().get().toLowerCase().contains(search)
        ).filtered(f -> {
            if (filterRol == null || filterRol.isEmpty()) {
                return true;
            }
            String rolCode = filterRol.toUpperCase();
            return f.getRole().get().equals(rolCode);
        }).filtered(s -> {
            if (filterStatus == null || filterStatus.isEmpty()) {
                return true;
            }
            String statusCode = filterStatus.equals("Activo") ? "A" : "I";
            return s.getStatus().get().equals(statusCode);
        });

        TreeItem<UserRow> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
        tbvUsers.setRoot(root);
        tbvUsers.setShowRoot(false);
    }

    @FXML
    private void onActionBtnClearFilters(ActionEvent event) {
        txfSearch.clear();

        cmbRole.clearSelection();
        cmbStatus.clearSelection();

        filters();
    }

    private void procesarUsuariosDesdeJson(String usuariosJson) {
        try {
            userList.clear();
            System.out.println("Procesando JSON de usuarios...");
            System.out.println("JSON recibido: " + usuariosJson);

            // Verificar si el JSON es válido
            if (usuariosJson == null || usuariosJson.trim().isEmpty()) {
                System.err.println("JSON vacío");
                showMessage(getLanguageString("msg.nouserdata"));
                return;
            }

            String jsonTrimmed = usuariosJson.trim();
            if (jsonTrimmed.startsWith("[")) {
                procesarArrayDeUsuarios(usuariosJson);
            } else {
                int inicioArray = usuariosJson.indexOf("\"Usuarios\":");
                if (inicioArray != -1) {
                    inicioArray = usuariosJson.indexOf("[", inicioArray);
                    if (inicioArray != -1) {
                        int finArray = encontrarCierreCorchete(usuariosJson, inicioArray);
                        if (finArray != -1) {
                            String arrayUsuarios = usuariosJson.substring(inicioArray, finArray + 1);

                            procesarArrayDeUsuarios(arrayUsuarios);
                        } else {
                            showMessage("Error analizando la respuesta del servidor");
                        }
                    } else {
                        showMessage("Error analizando la respuesta del servidor");
                    }
                } else {
                    showMessage("Formato de respuesta inesperado");
                }
            }

            filters();


        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error procesando datos: " + e.getMessage());
        }
    }

    /**
     * Encuentra la posición de cierre del corchete correspondiente
     */
    private int encontrarCierreCorchete(String json, int posicionApertura) {
        int contador = 1;
        for (int i = posicionApertura + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') {
                contador++;
            } else if (c == ']') {
                contador--;
                if (contador == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Procesa un array de usuarios JSON
     */
    private void procesarArrayDeUsuarios(String arrayUsuarios) {
        try {
            String contenido = arrayUsuarios.substring(1, arrayUsuarios.length() - 1);

            int nivelLlaves = 0;
            StringBuilder objetoUsuario = new StringBuilder();

            for (int i = 0; i < contenido.length(); i++) {
                char c = contenido.charAt(i);

                if (c == '{') {
                    nivelLlaves++;
                    objetoUsuario.append(c);
                } else if (c == '}') {
                    nivelLlaves--;
                    objetoUsuario.append(c);

                    if (nivelLlaves == 0) {
                        procesarObjetoUsuario(objetoUsuario.toString());
                        objetoUsuario = new StringBuilder();

                        if (i + 1 < contenido.length() && contenido.charAt(i + 1) == ',') {
                            i++;
                        }
                    }
                } else if (nivelLlaves > 0) {
                    objetoUsuario.append(c);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando array de usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Procesa un objeto de usuario individual para la tabla
     */
    private void procesarObjetoUsuario(String objetoUsuario) {
        try {
            Long idUsuario = JsonParser.extraerValorLong(objetoUsuario, "idUsuario");
            String usuario = JsonParser.extraerValor(objetoUsuario, "usuario");
            String nombre = JsonParser.extraerValor(objetoUsuario, "nombre");
            if (nombre == null || nombre.trim().isEmpty()) {
                nombre = usuario; // Fallback al usuario si no hay nombre
            }
            String rol = JsonParser.extraerValor(objetoUsuario, "rol");
            String estado = JsonParser.extraerValor(objetoUsuario, "estado");

            if (usuario != null && rol != null && estado != null && idUsuario != null) {
                System.out.println("Usuario encontrado: " + usuario + ", Nombre: " + nombre + ", Rol: " + rol + ", Estado: " + estado);
                userList.add(new UserRow(idUsuario, usuario, nombre, rol, estado));
            } else {
                System.err.println("Datos incompletos en objeto de usuario: " + objetoUsuario);
            }
        } catch (Exception e) {
            System.err.println("Error procesando objeto de usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private UsuarioDto parsearUsuarioDto(String json) {
        try {
            UsuarioDto dto = new UsuarioDto();
            dto.setIdUsuario(JsonParser.extraerValorLong(json, "idUsuario"));
            dto.setUsuario(JsonParser.extraerValor(json, "usuario"));
            dto.setNombre(JsonParser.extraerValor(json, "nombre"));
            dto.setRol(JsonParser.extraerValor(json, "rol"));
            dto.setEstado(JsonParser.extraerValor(json, "estado"));
            
            // Campo booleano para estado activo
            Boolean activo = JsonParser.extraerValorBooleano(json, "activo");
            if (activo != null) {
                dto.setActivo(activo);
            }
            
            return dto;
        } catch (Exception e) {
            System.err.println("Error parseando UsuarioDto: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



    private void onEditUser(UserRow userRow) {
        // Cargar el usuario completo desde el servidor usando el ID
        Task<Respuesta> loadTask = new Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                return usuarioService.getUsuario(userRow.getIdUsuario());
            }
        };

        loadTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                Respuesta respuesta = loadTask.getValue();
                if (respuesta != null && respuesta.getEstado()) {
                    // La respuesta viene como String JSON, necesitamos parsearlo
                    Object resultado = respuesta.getResultado("Usuario");
                    UsuarioDto usuarioDto = null;
                    
                    if (resultado instanceof String) {
                        // Parsear el JSON manualmente
                        usuarioDto = parsearUsuarioDto((String) resultado);
                    } else if (resultado instanceof UsuarioDto) {
                        usuarioDto = (UsuarioDto) resultado;
                    }
                    
                    if (usuarioDto != null) {
                        NewUserController controller = (NewUserController) FlowController.getInstance()
                                .getController(AppKeys.NEW_USER);
                        controller.setParentController(this);
                        controller.loadUser(usuarioDto);
                        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_USER, this.getStage(), false);
                    } else {
                        showMessage(getLanguageString("msg.user.error"));
                    }
                } else {
                    showMessage(getLanguageString("msg.error.user") + (respuesta != null ? respuesta.getMensaje() : "Error desconocido"));
                }
            });
        });

        loadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showMessage(getLanguageString("msg.connection.error") + loadTask.getException().getMessage());
            });
        });

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private void onDeleteUser(UserRow userRow) {
        // Mostrar diálogo de confirmación
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle(getLanguageString("confirm.delete"));
        confirmAlert.setHeaderText(getLanguageString("delete.user"));
        confirmAlert.setContentText(getLanguageString("sure.delete.user") + userRow.getUsername().get() + "'?\n\n" +
                getLanguageString("action.not.reversible"));
        
        // Esperar respuesta del usuario
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                eliminarUsuarioDelServidor(userRow);
            }
        });
    }
    
    private void eliminarUsuarioDelServidor(UserRow userRow) {
        // Deshabilitar botón mientras se elimina
        btnAdd.setDisable(true);
        btnAdd.setText(getLanguageString("btn.deleting"));
        
        Task<Respuesta> deleteTask = new Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                return usuarioService.eliminarUsuario(userRow.getIdUsuario());
            }
        };
        
        deleteTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                Respuesta respuesta = deleteTask.getValue();
                
                if (respuesta != null && respuesta.getEstado()) {
                    // Eliminación exitosa
                    showMessage(getLanguageString("user")+ userRow.getUsername().get() + getLanguageString("deletion.success"));
                    
                    // Eliminar de la lista local
                    userList.remove(userRow);
                    
                    // Actualizar la vista
                    filters();
                } else {
                    // Error al eliminar
                    String errorMsg = respuesta != null ? respuesta.getMensaje() : "Error desconocido";
                    showMessage(getLanguageString("msg.error.user.delete") + errorMsg);
                }
                
                // Restaurar botón
                btnAdd.setDisable(false);
                btnAdd.setText(getLanguageString("btn.add"));
            });
        });
        
        deleteTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                Throwable exception = deleteTask.getException();
                showMessage("Error de conexión: " + (exception != null ? exception.getMessage() : "Error desconocido"));
                
                // Restaurar botón
                btnAdd.setDisable(false);
                btnAdd.setText(getLanguageString("btn.add"));
            });
        });
        
        Thread deleteThread = new Thread(deleteTask);
        deleteThread.setDaemon(true);
        deleteThread.start();
    }

    private void setActionsColumn() {
        tbcActions.setCellFactory(col -> new TreeTableCell<UserRow, Void>() {
            private final MFXButton btnEdit = new MFXButton();
            private final MFXButton btnDelete = new MFXButton();
            private final javafx.scene.layout.HBox buttonsBox = new javafx.scene.layout.HBox(5);

            {
                // Configurar estilo de los botones para que solo muestren el gráfico
                btnEdit.setText("");  // Sin texto
                btnDelete.setText(""); // Sin texto
                
                // Configurar imágenes para los botones
                try {
                    String editIconPath = "/cr/ac/una/restuna/resources/icons/icons8-edit-50.png";
                    String deleteIconPath = "/cr/ac/una/restuna/resources/icons/icons8-delete-50.png";
                    
                    java.io.InputStream editStream = getClass().getResourceAsStream(editIconPath);
                    java.io.InputStream deleteStream = getClass().getResourceAsStream(deleteIconPath);
                    
                    if (editStream != null && deleteStream != null) {
                        ImageView editIcon = new ImageView(new Image(editStream));
                        editIcon.setFitWidth(20);
                        editIcon.setFitHeight(20);
                        btnEdit.setGraphic(editIcon);
                        
                        ImageView deleteIcon = new ImageView(new Image(deleteStream));
                        deleteIcon.setFitWidth(20);
                        deleteIcon.setFitHeight(20);
                        btnDelete.setGraphic(deleteIcon);
                    } else {
                        // Si no se cargan las imágenes, crear labels con símbolos
                        javafx.scene.control.Label editLabel = new javafx.scene.control.Label("✏️");
                        editLabel.setStyle("-fx-font-size: 18px;");
                        btnEdit.setGraphic(editLabel);
                        
                        javafx.scene.control.Label deleteLabel = new javafx.scene.control.Label("🗑️");
                        deleteLabel.setStyle("-fx-font-size: 18px;");
                        btnDelete.setGraphic(deleteLabel);
                    }
                } catch (Exception e) {
                    System.err.println("Error cargando iconos: " + e.getMessage());
                    // Crear labels con símbolos en caso de error
                    javafx.scene.control.Label editLabel = new javafx.scene.control.Label("✏️");
                    editLabel.setStyle("-fx-font-size: 18px;");
                    btnEdit.setGraphic(editLabel);
                    
                    javafx.scene.control.Label deleteLabel = new javafx.scene.control.Label("🗑️");
                    deleteLabel.setStyle("-fx-font-size: 18px;");
                    btnDelete.setGraphic(deleteLabel);
                }

                // Configurar acciones
                btnEdit.setOnAction(e -> {
                    UserRow userRow = getTableRow().getItem();
                    if (userRow != null) {
                        onEditUser(userRow);
                    }
                });
                
                btnDelete.setOnAction(e -> {
                    UserRow userRow = getTableRow().getItem();
                    if (userRow != null) {
                        onDeleteUser(userRow);
                    }
                });
                
                buttonsBox.getChildren().addAll(btnEdit, btnDelete);
                buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(getLanguageString("msg.info"));
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String getLanguageString(String key) {
        return FlowController.getInstance().getLanguage().getString(key);
    }
}
