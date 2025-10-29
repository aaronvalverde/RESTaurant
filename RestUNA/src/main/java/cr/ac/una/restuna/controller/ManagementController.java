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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class ManagementController extends Controller implements Initializable {

    @FXML
    private MFXButton btnUsers;
    @FXML
    private MFXButton btnSections;
    @FXML
    private MFXButton btnGroups;
    @FXML
    private MFXButton btnItems;
    @FXML
    private MFXButton btnExit;
    @FXML
    private BorderPane contentArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void initialize() {
        FlowController.getInstance().setContentArea(contentArea);
    }

    @FXML
    private void onActionBtnUsers(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.USERS_MGMT);
    }

    @FXML
    private void onActionBtnSections(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.SECTIONS_MGMT);
    }

    @FXML
    private void onActionBtnGroups(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MENU_GROUPS_MGMT);
    }

    @FXML
    private void onActionBtnItems(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MENU_ITEMS_MGMT);
    }

    @FXML
    private void onActionBtnExit(ActionEvent event) {
        FlowController.getInstance().setContentArea(null);
        FlowController.getInstance().goMain(AppKeys.MAIN);
    }
}
