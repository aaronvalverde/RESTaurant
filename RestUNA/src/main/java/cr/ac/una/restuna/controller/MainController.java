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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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
    private MFXButton btnSectionsMgmt;
    @FXML
    private MFXButton btnMenuGroups;
    @FXML
    private MFXButton btnMenuItems;
    @FXML
    private MFXButton btnReports;
    @FXML
    private MFXButton btnSettings;
    @FXML
    private BorderPane contentArea;
    @FXML
    private WebView wvLogo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FlowController.getInstance().setContentArea((BorderPane) contentArea.getParent());

        WebEngine webEngine = wvLogo.getEngine();
        String svgPath = getClass()
                .getResource("/cr/ac/una/restuna/resources/Logo_beany.svg")
                .toExternalForm();

        String html = "<html><body style='background: #5e3d26; display:flex; justify-content:center; align-items:center; height:100%; margin:0;'>"
                + "<img src='" + svgPath + "'/>"
                + "</body></html>";
        webEngine.loadContent(html);

        sidebar.setVisible(false);
        sidebar.setManaged(false);
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
        FlowController.getInstance().goMain(AppKeys.LOGIN);
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
        FlowController.getInstance().goView(AppKeys.USERS_MGMT);
    }

    @FXML
    private void onActionBtnSectionsMgmt(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.SECTIONS_MGMT);
    }

    @FXML
    private void onActionBtnMenuGroups(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MENU_GROUPS_MGMT);
    }

    @FXML
    private void onActionBtnMenuItems(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MENU_ITEMS_MGMT);
    }

    @FXML
    private void onActionBtnReports(ActionEvent event) {
    }

    @FXML
    private void onActionBtnSettings(ActionEvent event) {
    }
}
