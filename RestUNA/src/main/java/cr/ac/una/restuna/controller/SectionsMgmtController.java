package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SectionsMgmtController extends Controller implements Initializable {
    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXFilterComboBox<?> cmbType;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<?, ?> tbcName;
    @FXML
    private TreeTableColumn<?, ?> tbcTableGraphic;
    @FXML
    private TreeTableColumn<?, ?> tbcTax;
    @FXML
    private TreeTableColumn<?, ?> tbcType;
    @FXML
    private JFXTreeTableView<?> tbvSections;
    @FXML
    private MFXTextField txfSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tbvSections.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvSections.prefWidthProperty().bind(tableRoot.widthProperty());
    }

    @Override
    public void initialize() {

    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_SECTION, new Stage(), false);
    }

    @FXML
    private void onEditSection(/*DTO de la seccion*/) {
        NewSectionController controller = new NewSectionController();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_SECTION, new Stage(), false);
        controller.loadSection(/*DTO de la seccion*/);
    }
}
