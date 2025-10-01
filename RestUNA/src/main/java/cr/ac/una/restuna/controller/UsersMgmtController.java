/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private MFXFilterComboBox<?> cmbRole;
    @FXML
    private MFXFilterComboBox<?> cmbStatus;
    @FXML
    private MFXButton btnAdd;
    @FXML
    private TreeTableColumn<?, ?> tbcName;
    @FXML
    private TreeTableColumn<?, ?> tbcUser;
    @FXML
    private TreeTableColumn<?, ?> tbcEmail;
    @FXML
    private TreeTableColumn<?, ?> tbcRole;
    @FXML
    private TreeTableColumn<?, ?> tbcStatus;
    @FXML
    private JFXTreeTableView<?> tbvUsers;
    @FXML
    private MFXScrollPane tableRoot;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvUsers.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvUsers.prefWidthProperty().bind(tableRoot.widthProperty());
    }

    @Override
    public void initialize() {

    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_USER, new Stage(), false);
    }
}
