package cr.ac.una.restuna.controller;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableColumn;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
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
    private MFXFilterComboBox<String> cmbShortcut;
    @FXML
    private MFXFilterComboBox<String> cmbStatus;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<Groups, String> tbcActions;
    @FXML
    private TreeTableColumn<Groups, String> tbcDescription;
    @FXML
    private TreeTableColumn<Groups, String> tbcID;
    @FXML
    private TreeTableColumn<Groups, String> tbcName;
    @FXML
    private TreeTableColumn<Groups, String> tbcShortcut;
    @FXML
    private TreeTableColumn<Groups, String> tbcStatus;
    @FXML
    private JFXTreeTableView<Groups> tbvMenuGroups;
    @FXML
    private MFXTextField txfSearch;

    private final ObservableList<Groups> group = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvMenuGroups.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvMenuGroups.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbStatus.getItems().addAll("A", "I");
        //cmbShortcut 

        tbcID.setCellValueFactory(x -> x.getValue().getValue().getIdGroup());
        tbcName.setCellValueFactory(x -> x.getValue().getValue().getNameGroup());
        tbcDescription.setCellValueFactory(x -> x.getValue().getValue().getDescription());
        tbcShortcut.setCellValueFactory(x -> x.getValue().getValue().getQuickAccess());
        tbcStatus.setCellValueFactory(x -> x.getValue().getValue().getStatus());

        TreeItem<Groups> root = new RecursiveTreeItem<>(group, RecursiveTreeObject::getChildren);
        tbvMenuGroups.setRoot(root);
        tbvMenuGroups.setShowRoot(false);

        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> groupFilter());
        cmbShortcut.valueProperty().addListener((obs, oldVal, newVal) -> groupFilter());
        cmbStatus.valueProperty().addListener((obs, oldVal, newVal) -> groupFilter());
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

    public void addGroup(String idGroup, String nameGroup, String description, String shorcut, String status) {

        for (Groups grp : group) {

            if (grp.getIdGroup().get().equalsIgnoreCase(idGroup)) {

                showMessage("El grupo ya existe: " + idGroup);
                return;
            }

        }

        group.add(new Groups(nameGroup, idGroup, description, shorcut, status));
        groupFilter();
    }

    private void groupFilter() {

        String search = txfSearch.getText() == null ? "" : txfSearch.getText().toLowerCase();
        String shorcut = cmbShortcut.getValue();
        String status = cmbStatus.getValue();

        ObservableList<Groups> filter = group.filtered(x
                -> x.getNameGroup().get().toLowerCase().contains(search) || x.getDescription().get().toLowerCase().contains(search))
                .filtered(x -> shorcut == null || shorcut.isEmpty() || x.getQuickAccess().get().equals(shorcut))
                .filtered(x -> status == null || status.isEmpty() || x.getStatus().get().equals(status));

        TreeItem<Groups> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
        tbvMenuGroups.setRoot(root);
        tbvMenuGroups.setShowRoot(false);
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
