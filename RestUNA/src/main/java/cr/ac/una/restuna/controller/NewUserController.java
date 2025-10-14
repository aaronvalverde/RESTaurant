/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.dto.UsuarioDto;
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
    private MFXButton btnCLose;
    @FXML
    private MFXTextField txfUsername;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar servicio
        usuarioService = new UsuarioService();
        
        // Configurar ComboBoxes con valores correctos de BD
        cmbRole.getItems().addAll("ADMINISTRADOR", "CAJERO", "SALONERO");
        cmbStatus.getItems().addAll("A", "I"); // A = Activo, I = Inactivo
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnClose(ActionEvent event) {
        getStage().close();
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
         String username = txfUsername.getText();
         String password = pwfPassword.getText();
         String role = cmbRole.getValue();
         String status = cmbStatus.getValue();
         
        if(username.isEmpty() || password.isEmpty() || role == null || status == null ){
            showMessage("Campos obligatorios");
            return;
        }
        
        // Crear usuario en el servidor usando servicio REST
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                UsuarioDto usuario = new UsuarioDto();
                usuario.setUsuario(username);
                usuario.setNuevaContrasena(password);
                usuario.setRol(role);
                usuario.setEstado(status);
                
                Respuesta respuesta = usuarioService.guardarUsuario(usuario);
                
                if (!respuesta.getEstado()) {
                    Platform.runLater(() -> {
                        showMessage("Error: " + respuesta.getMensaje());
                    });
                } else {
                    Platform.runLater(() -> {
                        // Notificar al controlador padre que se agregó un usuario
                        UsersMgmtController newUser = (UsersMgmtController) FlowController.getInstance().getController(AppKeys.USERS_MGMT);
                        newUser.addUser(username, role, status);
                        getStage().close();
                    });
                }
                return null;
            }
        };
        
        new Thread(task).start();
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
}
