package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.UsuarioDto;
import cr.ac.una.restuna.service.UsuarioService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Respuesta;
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

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class NewUserController extends Controller implements Initializable {

    @FXML
    private MFXTextField txfUsername;
    @FXML
    private MFXTextField txfName;
    @FXML
    private MFXPasswordField pwfPassword;
    @FXML
    private MFXComboBox<String> cmbRole;
    @FXML
    private MFXComboBox<String> cmbStatus;
    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXButton btnCancel;

    private UsuarioService usuarioService;
    private boolean editMode = false;
    private UsersMgmtController parentController;
    private UsuarioDto usuarioDto;
    @FXML
    private MFXButton btnSaveChanges;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar servicio
        usuarioService = new UsuarioService();

        // Configurar ComboBoxes con valores correctos de BD
        cmbRole.getItems().addAll("ADMINISTRADOR", "CAJERO", "SALONERO");
        cmbStatus.getItems().addAll("A", "I"); // A = Activo, I = Inactivo
        initButtons();
    }

    @Override
    public void initialize() {
        // Limpiar campos cada vez que se muestra la vista
        clearFields();
    }

    private void onActionBtnClose(ActionEvent event) {
        getStage().close();
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        String username = txfUsername.getText().trim();
        String name = txfName.getText().trim();
        String password = pwfPassword.getText().trim();
        String role = cmbRole.getValue();
        String status = cmbStatus.getValue();

        // Validaciones más completas
        if (username.isEmpty()) {
            showMessage("El nombre de usuario es obligatorio");
            txfUsername.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            showMessage("El nombre completo es obligatorio");
            txfName.requestFocus();
            return;
        }

        // Validar contraseña solo en modo creación
        if (password.isEmpty()) {
            showMessage("La contraseña es obligatoria");
            pwfPassword.requestFocus();
            return;
        } else if (password.length() < 4) {
            showMessage("La contraseña debe tener al menos 4 caracteres");
            pwfPassword.requestFocus();
            return;
        }

        if (role == null) {
            showMessage("Debe seleccionar un rol");
            cmbRole.requestFocus();
            return;
        }

        if (status == null) {
            showMessage("Debe seleccionar un estado");
            cmbStatus.requestFocus();
            return;
        }

        // Deshabilitar controles para evitar doble envío
        btnAdd.setDisable(true);
        btnCancel.setDisable(true);
        btnAdd.setText("Guardando...");

        // Crear usuario en el servidor usando servicio REST
        Task<Respuesta> task = new Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                UsuarioDto usuario = new UsuarioDto();
                usuario.setUsuario(username);
                usuario.setNombre(name);  // Agregar el nombre
                usuario.setNuevaContrasena(password);
                usuario.setRol(role);
                usuario.setEstado(status);

                System.out.println("Enviando petición para crear usuario: " + username);
                return usuarioService.guardarUsuario(usuario);
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                Respuesta respuesta = task.getValue();

                if (!respuesta.getEstado()) {
                    // Obtener un mensaje de error amigable para el usuario
                    String errorMsg = respuesta.getMensaje();

                    // Si el mensaje contiene HTML o es muy técnico, mostrar un mensaje genérico
                    if (errorMsg == null || errorMsg.isEmpty() || errorMsg.contains("<html") || errorMsg.contains("<!DOCTYPE")) {
                        errorMsg = "No se pudo crear el usuario. Por favor, inténtelo nuevamente.";
                    }

                    showMessage("Error al guardar usuario: " + errorMsg);

                    // Registrar el error técnico completo en la consola para debugging
                    System.err.println("Error técnico completo: " + respuesta.getMensajeInterno());

                    // Rehabilitar controles
                    btnAdd.setDisable(false);
                    btnCancel.setDisable(false);
                    btnAdd.setText("Añadir");
                } else {
                    showMessage("Usuario guardado correctamente");

                    // Notificar al controlador padre que se agregó un usuario
                    if (parentController != null) {
                        // Extraer el idUsuario de la respuesta
                        Long idUsuario = null;
                        try {
                            String usuarioJson = (String) respuesta.getResultado("Usuario");
                            if (usuarioJson != null) {
                                // Extraer idUsuario usando JsonParser
                                idUsuario = cr.ac.una.restuna.util.JsonParser.extraerValorLong(usuarioJson, "idUsuario");
                                System.out.println("ID de usuario extraído: " + idUsuario);
                            }
                        } catch (Exception ex) {
                            System.err.println("Error extrayendo ID de usuario: " + ex.getMessage());
                        }
                        
                        // Si no pudimos extraer el ID, usar un valor temporal
                        if (idUsuario == null) {
                            idUsuario = -1L; // ID temporal que indica que no se pudo obtener
                            System.err.println("ADVERTENCIA: No se pudo extraer el ID del usuario. Usando ID temporal.");
                        }
                        
                        // Asegurarse de pasar el nombre correctamente
                        System.out.println("Notificando a UsersMgmtController sobre nuevo usuario: " + username);
                        parentController.addUser(idUsuario, username, name, role, status);
                    } else {
                        System.err.println("ADVERTENCIA: parentController es null, no se puede actualizar la tabla");
                    }

                    // Cerrar ventana
                    getStage().close();
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                Throwable exception = task.getException();

                // Registrar el error técnico en la consola para debugging
                if (exception != null) {
                    exception.printStackTrace();
                    System.err.println("Error técnico completo: " + exception.getMessage());
                }

                // Mostrar un mensaje de error amigable al usuario
                showMessage("No se pudo conectar con el servidor. Por favor, verifique su conexión e inténtelo nuevamente.");

                // Rehabilitar controles
                btnAdd.setDisable(false);
                btnCancel.setDisable(false);
                btnAdd.setText("Añadir");
            });
        });

        // Ejecutar la tarea en un hilo separado
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        getStage().close();
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void setParentController(UsersMgmtController parent) {
        this.parentController = parent;
    }
    
    /**
     * Limpia todos los campos del formulario y resetea el estado
     */
    public void clearFields() {
        editMode = false;
        usuarioDto = null;
        
        txfUsername.clear();
        txfName.clear();
        pwfPassword.clear();
        pwfPassword.setPromptText("");
        cmbRole.clearSelection();
        cmbStatus.clearSelection();
        
        // Resetear botones
        btnAdd.setDisable(false);
        btnAdd.setText("Áñadir");
        btnSaveChanges.setDisable(false);
        btnSaveChanges.setText("Save changes");
        btnCancel.setDisable(false);
        
        initButtons();
    }

    public void loadUser(UsuarioDto usuarioDto) {
        // Primero limpiar campos
        clearFields();
        
        // Ahora configurar modo edición
        editMode = true;
        this.usuarioDto = usuarioDto;

        txfName.setText(usuarioDto.getNombre());
        txfUsername.setText(usuarioDto.getUsuario());
        // No cargar contraseña por seguridad - dejar vacío
        pwfPassword.clear();
        pwfPassword.setPromptText("Dejar vacío para mantener la actual");
        cmbRole.getSelectionModel().selectItem(usuarioDto.getRol());
        cmbStatus.getSelectionModel().selectItem(usuarioDto.getEstado());

        initButtons();
    }

    private void initButtons() {
        btnAdd.setVisible(!editMode);
        btnAdd.setManaged(!editMode);
        btnSaveChanges.setVisible(editMode);
        btnSaveChanges.setManaged(editMode);
    }

    @FXML
    private void onActionBtnSaveChanges(ActionEvent event) {
        // Validaciones
        String name = txfName.getText();
        String username = txfUsername.getText();
        String password = pwfPassword.getText();
        String role = cmbRole.getValue();
        String status = cmbStatus.getValue();

        if (name == null || name.trim().isEmpty()) {
            showMessage("Debe ingresar el nombre");
            txfName.requestFocus();
            return;
        }

        if (username == null || username.trim().isEmpty()) {
            showMessage("Debe ingresar el nombre de usuario");
            txfUsername.requestFocus();
            return;
        }

        if (role == null) {
            showMessage("Debe seleccionar un rol");
            cmbRole.requestFocus();
            return;
        }

        if (status == null) {
            showMessage("Debe seleccionar un estado");
            cmbStatus.requestFocus();
            return;
        }

        // Deshabilitar controles para evitar doble envío
        btnSaveChanges.setDisable(true);
        btnCancel.setDisable(true);
        btnSaveChanges.setText("Actualizando...");

        // Actualizar usuario en el servidor
        Task<Respuesta> task = new Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                // Actualizar datos del DTO existente
                usuarioDto.setNombre(name);
                usuarioDto.setUsuario(username);
                usuarioDto.setRol(role);
                usuarioDto.setEstado(status);
                
                // Solo actualizar contraseña si se ingresó una nueva
                if (password != null && !password.trim().isEmpty()) {
                    usuarioDto.setNuevaContrasena(password);
                }

                System.out.println("Enviando petición para actualizar usuario: " + username + " (ID: " + usuarioDto.getIdUsuario() + ")");
                return usuarioService.guardarUsuario(usuarioDto);
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                Respuesta respuesta = task.getValue();

                if (!respuesta.getEstado()) {
                    String errorMsg = respuesta.getMensaje();
                    if (errorMsg == null || errorMsg.isEmpty() || errorMsg.contains("<html") || errorMsg.contains("<!DOCTYPE")) {
                        errorMsg = "No se pudo actualizar el usuario. Por favor, inténtelo nuevamente.";
                    }

                    showMessage("Error al actualizar usuario: " + errorMsg);
                    System.err.println("Error técnico completo: " + respuesta.getMensajeInterno());

                    // Rehabilitar controles
                    btnSaveChanges.setDisable(false);
                    btnCancel.setDisable(false);
                    btnSaveChanges.setText("Save changes");
                } else {
                    showMessage("Usuario actualizado correctamente");

                    // Recargar la lista completa del controlador padre
                    if (parentController != null) {
                        parentController.cargarUsuarios();
                    }

                    // Cerrar ventana
                    getStage().close();
                }
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                Throwable exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                    System.err.println("Error técnico completo: " + exception.getMessage());
                }

                showMessage("No se pudo conectar con el servidor. Por favor, verifique su conexión e inténtelo nuevamente.");

                // Rehabilitar controles
                btnSaveChanges.setDisable(false);
                btnCancel.setDisable(false);
                btnSaveChanges.setText("Save changes");
            });
        });

        // Ejecutar la tarea en un hilo separado
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
