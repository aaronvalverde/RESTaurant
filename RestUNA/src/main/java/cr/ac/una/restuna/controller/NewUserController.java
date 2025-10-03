/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.util.ResourceBundle;

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
    private MFXTextField txfName;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbRole.getItems().addAll("Cajero", "Administrador", "Salonero");
        cmbStatus.getItems().addAll("Activo", "Inactivo");
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
         String name = txfName.getText();
         String username = txfUsername.getText();
         String password = pwfPassword.getText();
         String role = cmbRole.getValue();
         String status = cmbStatus.getValue();
         
        if(name.isEmpty() ||username.isEmpty() || password.isEmpty() || role == null || status == null ){
            showMessage("Campos obligatorios");
            return;
        }
        
        UsersMgmtController newUser = (UsersMgmtController) FlowController.getInstance().getController(AppKeys.USERS_MGMT);
        
        newUser.addUser(name, username, name.toLowerCase() + "@.com", role, status);
        getStage().close();
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
