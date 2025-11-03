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

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class ManagementController extends Controller implements Initializable {

    @FXML
    private MFXButton btnUsers;
    @FXML
    private MFXButton btnSectionsMgmt;
    @FXML
    private MFXButton btnGroups;
    @FXML
    private MFXButton btnItems;
    @FXML
    private MFXButton btnExit;
    @FXML
    private BorderPane contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnUsers(ActionEvent event) {
        FlowController.getInstance().goViewInContent(AppKeys.USERS_MGMT, contentArea);
    }

    @FXML
    private void onActionBtnSections(ActionEvent event) {
        FlowController.getInstance().goViewInContent(AppKeys.SECTIONS_MGMT, contentArea);
    }

    @FXML
    private void onActionBtnGroups(ActionEvent event) {
        FlowController.getInstance().goViewInContent(AppKeys.MENU_GROUPS_MGMT, contentArea);
    }

    @FXML
    private void onActionBtnItems(ActionEvent event) {
        FlowController.getInstance().goViewInContent(AppKeys.MENU_ITEMS_MGMT, contentArea);
    }

    @FXML
    private void onActionBtnExit(ActionEvent event) {
        FlowController.getInstance().goHome();
    }
}
