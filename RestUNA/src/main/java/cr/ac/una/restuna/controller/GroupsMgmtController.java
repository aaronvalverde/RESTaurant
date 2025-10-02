package cr.ac.una.restuna.controller;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXTreeTableView;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableColumn;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class GroupsMgmtController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXFilterComboBox<?> cmbShortcut;
    @FXML
    private MFXFilterComboBox<?> cmbStatus;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<?, ?> tbcActions;
    @FXML
    private TreeTableColumn<?, ?> tbcDescription;
    @FXML
    private TreeTableColumn<?, ?> tbcID;
    @FXML
    private TreeTableColumn<?, ?> tbcName;
    @FXML
    private TreeTableColumn<?, ?> tbcShortcut;
    @FXML
    private TreeTableColumn<?, ?> tbcStatus;
    @FXML
    private JFXTreeTableView<?> tbvMenuGroups;
    @FXML
    private MFXTextField txfSearch;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvMenuGroups.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvMenuGroups.prefWidthProperty().bind(tableRoot.widthProperty());
    }

    @Override
    public void initialize() {
    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_GROUP, new Stage(), false);
    }

    @FXML
    private void onEditSection(/*DTO de la seccion*/) {
        NewGroupController controller = new NewGroupController();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_GROUP, new Stage(), false);
        controller.loadSection(/*DTO de la seccion*/);
    }

}
