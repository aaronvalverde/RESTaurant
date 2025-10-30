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
        // Inicializar servicio
        usuarioService = new UsuarioService();

        // Configurar tabla
        tbvUsers.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvUsers.prefWidthProperty().bind(tableRoot.widthProperty());

        // Configurar ComboBoxes de filtros
        cmbRole.getItems().addAll("Administrador", "Cajero", "Salonero");
        cmbStatus.getItems().addAll("Activo", "Inactivo");

        // Configurar columnas de la tabla (estructura simplificada)
        tbcUser.setCellValueFactory(x -> x.getValue().getValue().getUsername());
        tbcName.setCellValueFactory(x -> x.getValue().getValue().getName());
        tbcRole.setCellValueFactory(x -> x.getValue().getValue().getRoleDisplay()); // Mostrar rol capitalizado
        tbcStatus.setCellValueFactory(x -> x.getValue().getValue().getStatusDisplay()); // Mostrar "Activo"/"Inactivo"

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
        setActionsColumn();
    }

    /**
     * Carga todos los usuarios desde el servidor Método público para permitir
     * recargar desde otros controladores
     */
    public void cargarUsuarios() {
        // Mostrar indicador de carga si es necesario
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
                
                // Restaurar botón después de cargar (exitoso o no)
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
        // Abrir ventana modal para agregar nuevo usuario
        Stage stage = new Stage();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_USER, stage, false);

        // Si la ventana se cierra, podríamos recargar los usuarios
        // Sin embargo, esto se maneja desde NewUserController a través de addUser
    }

    /**
     * Añade un usuario al modelo local y actualiza la vista Este método es
     * llamado desde NewUserController después de guardar un usuario
     *
     * @param idUsuario ID del usuario
     * @param username Nombre de usuario
     * @param name Nombre completo
     * @param role Rol del usuario
     * @param status Estado del usuario (A=Activo, I=Inactivo)
     */
    public void addUser(Long idUsuario, String username, String name, String role, String status) {
        // Verificar si el usuario ya existe en la lista local
        for (UserRow user : userList) {
            if (user.getUsername().get().equalsIgnoreCase(username)) {
                System.out.println("El usuario " + username + " ya existe en la lista. Actualizando...");
                // Si existe, podemos actualizar sus datos
                user.getName().set(name);
                user.getRole().set(role);
                user.getStatus().set(status);
                filters(); // Actualizar vista
                return;
            }
        }

        // Si no existe, añadirlo
        System.out.println("Añadiendo nuevo usuario: " + username);
        userList.add(new UserRow(idUsuario, username, name, role, status));
        filters(); // Actualizar vista

        // Opcional: Mostrar mensaje de confirmación
        // showMessage("Usuario " + username + " añadido correctamente");
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
            // Convertir rol capitalizado a mayúsculas para comparar
            String rolCode = filterRol.toUpperCase();
            return f.getRole().get().equals(rolCode);
        }).filtered(s -> {
            if (filterStatus == null || filterStatus.isEmpty()) {
                return true;
            }
            // Convertir "Activo" a "A" e "Inactivo" a "I" para comparar
            String statusCode = filterStatus.equals("Activo") ? "A" : "I";
            return s.getStatus().get().equals(statusCode);
        });

        TreeItem<UserRow> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
        tbvUsers.setRoot(root);
        tbvUsers.setShowRoot(false);
    }

    @FXML
    private void onActionBtnClearFilters(ActionEvent event) {
        // Limpiar campo de búsqueda
        txfSearch.clear();

        // Limpiar ComboBoxes
        cmbRole.clearSelection();
        cmbStatus.clearSelection();

        // Aplicar filtros (mostrará todos los usuarios)
        filters();
    }

    /**
     * Procesa la lista de usuarios desde JSON y los agrega a la tabla
     */
    private void procesarUsuariosDesdeJson(String usuariosJson) {
        try {
            userList.clear();
            System.out.println("Procesando JSON de usuarios...");
            System.out.println("JSON recibido: " + usuariosJson);

            // Verificar si el JSON es válido
            if (usuariosJson == null || usuariosJson.trim().isEmpty()) {
                System.err.println("JSON vacío");
                showMessage("No se recibieron datos de usuarios");
                return;
            }

            // El servidor retorna directamente un array JSON siguiendo el patrón UNA Planilla
            // Verificar si comienza con corchete (array directo)
            String jsonTrimmed = usuariosJson.trim();
            if (jsonTrimmed.startsWith("[")) {
                // Es un array directo, procesarlo directamente
                System.out.println("Procesando array directo de usuarios");
                procesarArrayDeUsuarios(usuariosJson);
            } else {
                // Formato antiguo: objeto con clave "Usuarios"
                int inicioArray = usuariosJson.indexOf("\"Usuarios\":");
                if (inicioArray != -1) {
                    // Encontrar el inicio del array
                    inicioArray = usuariosJson.indexOf("[", inicioArray);
                    if (inicioArray != -1) {
                        // Encontrar el fin del array
                        int finArray = encontrarCierreCorchete(usuariosJson, inicioArray);
                        if (finArray != -1) {
                            // Extraer el array de usuarios
                            String arrayUsuarios = usuariosJson.substring(inicioArray, finArray + 1);
                            System.out.println("Array de usuarios extraído: " + arrayUsuarios);

                            // Ahora procesamos cada objeto de usuario dentro del array
                            procesarArrayDeUsuarios(arrayUsuarios);
                        } else {
                            System.err.println("No se pudo encontrar el cierre del array de usuarios");
                            showMessage("Error analizando la respuesta del servidor");
                        }
                    } else {
                        System.err.println("No se pudo encontrar el inicio del array de usuarios");
                        showMessage("Error analizando la respuesta del servidor");
                    }
                } else {
                    System.err.println("No se encontró la clave 'Usuarios' en el JSON");
                    showMessage("Formato de respuesta inesperado");
                }
            }

            // Actualizar la vista
            filters();

            System.out.println("Usuarios cargados: " + userList.size());

        } catch (Exception e) {
            System.err.println("Error procesando usuarios desde JSON: " + e.getMessage());
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
            // Eliminar los corchetes del array
            String contenido = arrayUsuarios.substring(1, arrayUsuarios.length() - 1);

            // Dividir por objetos de usuario (este enfoque simple asume que no hay objetos anidados)
            // Para una solución más robusta, necesitaríamos una biblioteca JSON adecuada
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

                    // Si llegamos al cierre del objeto de usuario
                    if (nivelLlaves == 0) {
                        procesarObjetoUsuario(objetoUsuario.toString());
                        objetoUsuario = new StringBuilder();

                        // Saltar la coma que separa objetos
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
     * Procesa un objeto de usuario individual
     */
    private void procesarObjetoUsuario(String objetoUsuario) {
        try {
            String idUsuarioStr = extraerValor(objetoUsuario, "idUsuario");
            String usuario = extraerValor(objetoUsuario, "usuario");
            // Intentar extraer el nombre del JSON, si no existe usamos el usuario como nombre
            String nombre = extraerValor(objetoUsuario, "nombre");
            if (nombre == null || nombre.trim().isEmpty()) {
                nombre = usuario; // Fallback al usuario si no hay nombre
            }
            String rol = extraerValor(objetoUsuario, "rol");
            String estado = extraerValor(objetoUsuario, "estado");

            if (usuario != null && rol != null && estado != null && idUsuarioStr != null) {
                try {
                    Long idUsuario = Long.parseLong(idUsuarioStr);
                    System.out.println("Usuario encontrado: " + usuario + ", Nombre: " + nombre + ", Rol: " + rol + ", Estado: " + estado);
                    userList.add(new UserRow(idUsuario, usuario, nombre, rol, estado));
                } catch (NumberFormatException e) {
                    System.err.println("Error parseando ID de usuario: " + idUsuarioStr);
                }
            } else {
                System.err.println("Datos incompletos en objeto de usuario: " + objetoUsuario);
            }
        } catch (Exception e) {
            System.err.println("Error procesando objeto de usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extrae un valor de un campo en el JSON
     */
    private String extraerValor(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patron);
        java.util.regex.Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group(1);
        }

        // Intentar con formato numérico (sin comillas)
        patron = "\"" + campo + "\"\\s*:\\s*([^,\\}]+)";
        pattern = java.util.regex.Pattern.compile(patron);
        matcher = pattern.matcher(json);

        if (matcher.find()) {
            String valor = matcher.group(1);
            // Eliminar espacios y verificar si es booleano
            valor = valor.trim();
            if (valor.equals("true") || valor.equals("false")) {
                return valor;
            }
            try {
                // Verificar si es numérico
                Double.parseDouble(valor);
                return valor;
            } catch (NumberFormatException e) {
                // No es numérico
            }
        }

        return null;
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
                    UsuarioDto usuarioDto = (UsuarioDto) respuesta.getResultado("Usuario");
                    if (usuarioDto != null) {
                        NewUserController controller = (NewUserController) FlowController.getInstance()
                                .getController(AppKeys.NEW_USER);
                        controller.setParentController(this);
                        controller.loadUser(usuarioDto);
                        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_USER, this.getStage(), false);
                    } else {
                        showMessage("Error: No se pudo cargar el usuario");
                    }
                } else {
                    showMessage("Error cargando usuario: " + (respuesta != null ? respuesta.getMensaje() : "Error desconocido"));
                }
            });
        });

        loadTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showMessage("Error de conexión: " + loadTask.getException().getMessage());
            });
        });

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private void onDeleteUser(UserRow userRow) {
        // TODO: Implementar lógica de eliminación
        // Mostrar diálogo de confirmación
        // Llamar al servicio para eliminar
        // Actualizar la lista local y refrescar la tabla
        System.out.println("Eliminar usuario: " + userRow.getUsername().get());
        showMessage("Función de eliminación aún no implementada");
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
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
