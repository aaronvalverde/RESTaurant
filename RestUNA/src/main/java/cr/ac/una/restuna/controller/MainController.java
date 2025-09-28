/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class MainController extends Controller implements Initializable {

    @FXML
    private HBox topbar;
    @FXML
    private MFXButton btnMenu;
    @FXML
    private VBox sidebar;
    @FXML
    private VBox main;
    @FXML
    private MFXButton btnLogout;
    @FXML
    private MFXButton btnSections;
    @FXML
    private MFXButton btnOrders;
    @FXML
    private MFXButton btnBilling;
    @FXML
    private MFXButton btnCashClosing;
    @FXML
    private MFXButton btnUsers;
    @FXML
    private MFXButton btnMenuGroups;
    @FXML
    private MFXButton btnMenuItems;
    @FXML
    private MFXButton btnReports;
    @FXML
    private MFXButton btnSettings;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize() {}

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onActionBtnMenu(ActionEvent event) {
        if (!sidebar.isVisible()) {
            sidebar.setVisible(true);
            sidebar.setManaged(true);
            return;
        }
        sidebar.setVisible(false);
        sidebar.setManaged(false);
    }

    @FXML
    private void onActionBtnSignOut(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.LOGIN);
    }

    @FXML
    private void onActionBtnSections(ActionEvent event) {
    }

    @FXML
    private void onActionBtnOrders(ActionEvent event) {
    }

    @FXML
    private void onActionBtnBilling(ActionEvent event) {
    }

    @FXML
    private void onActionBtnCashClosing(ActionEvent event) {
    }

    @FXML
    private void onActionBtnUsers(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.USERS_MANAGEMENT);
    }

    @FXML
    private void onActionBtnMenuGroups(ActionEvent event) {
    }

    @FXML
    private void onActionBtnMenuItems(ActionEvent event) {
    }

    @FXML
    private void onActionBtnReports(ActionEvent event) {
    }

    @FXML
    private void onActionBtnSettings(ActionEvent event) {
    }
}
