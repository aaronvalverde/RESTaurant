package cr.ac.una.restuna.controller;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.model.GrupoProductoDto;
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
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private TreeTableColumn<GrupoProductoDto, Void> tbcActions;
    @FXML
    private TreeTableColumn<GrupoProductoDto, String> tbcDescription;
    @FXML
    private TreeTableColumn<GrupoProductoDto, Long> tbcID;
    @FXML
    private TreeTableColumn<GrupoProductoDto, String> tbcName;
    @FXML
    private TreeTableColumn<GrupoProductoDto, String> tbcShortcut;
    @FXML
    private TreeTableColumn<GrupoProductoDto, String> tbcStatus;
    @FXML
    private JFXTreeTableView<GrupoProductoDto> tbvMenuGroups;
    @FXML
    private MFXTextField txfSearch;

    private final ObservableList<GrupoProductoDto> group = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvMenuGroups.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvMenuGroups.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbStatus.getItems().addAll("A", "I");
        cmbShortcut.getItems().addAll("S", "N");

        tbcID.setCellValueFactory(x -> x.getValue().getValue().idGrupoProductoProperty().asObject());
        tbcName.setCellValueFactory(x -> x.getValue().getValue().nombreProperty());
        tbcDescription.setCellValueFactory(x -> x.getValue().getValue().descripcionProperty());
        tbcShortcut.setCellValueFactory(x -> x.getValue().getValue().accesoRapidoProperty());
        tbcStatus.setCellValueFactory(x -> x.getValue().getValue().estadoProperty());

        TreeItem<GrupoProductoDto> root = new RecursiveTreeItem<>(group, RecursiveTreeObject::getChildren);
        tbvMenuGroups.setRoot(root);
        tbvMenuGroups.setShowRoot(false);

        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> groupFilter());
        cmbShortcut.valueProperty().addListener((obs, oldVal, newVal) -> groupFilter());
        cmbStatus.valueProperty().addListener((obs, oldVal, newVal) -> groupFilter());
        
        setActionsColumn();
    }

    @Override
    public void initialize() {
    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        try {
            NewGroupController item = (NewGroupController) FlowController.getInstance().getController(AppKeys.NEW_MENU_GROUP);
            item.clear();
            FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_GROUP, new Stage(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditSection(/*DTO de la seccion*/) {
        NewGroupController controller = new NewGroupController();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_GROUP, new Stage(), false);
        controller.loadSection(/*DTO de la seccion*/);
    }

    public void addGroup(String nameGroup, String description, String shorcut, String status) {

        for (GrupoProductoDto grp : group) {

            if (grp.getNombre().equals(nameGroup)) {

                showMessage("El grupo ya existe: " + nameGroup);
                return;
            }

        }
        GrupoProductoDto addGroups = new GrupoProductoDto();
        addGroups.setIdGrupoProducto(System.currentTimeMillis());
        addGroups.setNombre(nameGroup);
        addGroups.setDescripcion(description);
        addGroups.setAccesoRapido(shorcut);
        addGroups.setEstado(status);

        group.add(addGroups);
        groupFilter();
    }

    private void groupFilter() {

        String search = txfSearch.getText() == null ? "" : txfSearch.getText().toLowerCase();
        String shorcut = cmbShortcut.getValue();
        String status = cmbStatus.getValue();

        ObservableList<GrupoProductoDto> filter = group.filtered(x
                -> x.getNombre().toLowerCase().contains(search) || x.getDescripcion().toLowerCase().contains(search))
                .filtered(x -> shorcut == null || shorcut.isEmpty() || x.getAccesoRapido().equals(shorcut))
                .filtered(x -> status == null || status.isEmpty() || x.getEstado().equals(status));

        TreeItem<GrupoProductoDto> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
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

    private void onEditGroup(GrupoProductoDto gpDto) {
        NewGroupController controller = (NewGroupController) FlowController.getInstance()
                .getController(AppKeys.NEW_MENU_GROUP);
        controller.setParentController(this);
        controller.loadSection(gpDto);
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_GROUP, new Stage(), false);
    }

    private void setActionsColumn() {
        tbcActions.setCellFactory(col -> new TreeTableCell<GrupoProductoDto, Void>() {
            MFXButton btnEdit = new MFXButton(" ");
            MFXButton btnDelete = new MFXButton();

            {
                btnEdit.setGraphic(new ImageView(new Image("../resources/icons/icons8-edit-50.png")));
                btnDelete.setGraphic(new ImageView(new Image("../resources/icons/icons8-delete-50.png")));

                btnEdit.setOnAction(e -> {
                    GrupoProductoDto gpDto = getTreeTableRow().getItem();
                    if (gpDto != null) {
                        onEditGroup(gpDto);
                    }
                });
                btnDelete.setOnAction(e -> {
                    //lógica para eliminar la columna de la tabla y DB.
                });
            }
        });

    }

    private String getLanguageString(String key) {
        return FlowController.getInstance().getLanguage().getString(key);
    }

}
