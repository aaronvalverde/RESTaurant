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
public class ItemsMgmtController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXFilterComboBox<?> cmbGroups;
    @FXML
    private MFXFilterComboBox<?> cmbStatus;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<?, ?> tbcActions;
    @FXML
    private TreeTableColumn<?, ?> tbcID;
    @FXML
    private TreeTableColumn<?, ?> tbcName;
    @FXML
    private TreeTableColumn<?, ?> tbcPrice;
    @FXML
    private TreeTableColumn<?, ?> tbcShortcut;
    @FXML
    private TreeTableColumn<?, ?> tbcStatus;
    @FXML
    private JFXTreeTableView<?> tbvMenuItems;
    @FXML
    private MFXTextField txfSearch;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvMenuItems.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvMenuItems.prefWidthProperty().bind(tableRoot.widthProperty());
    }

    @Override
    public void initialize() {
    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_ITEM, new Stage(), false);
    }
    
        @FXML
    private void onEditSection(/*DTO de la seccion*/) {
        NewItemController controller = new NewItemController();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_ITEM, new Stage(), false);
        controller.loadSection(/*DTO de la seccion*/);
    }
}
