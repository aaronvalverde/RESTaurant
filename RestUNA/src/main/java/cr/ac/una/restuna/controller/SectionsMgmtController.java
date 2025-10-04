package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class SectionsMgmtController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXFilterComboBox<String> cmbType;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<Sections, String> tbcName;
    @FXML
    private TreeTableColumn<Sections, String> tbcTableGraphic;
    @FXML
    private TreeTableColumn<Sections, String> tbcTax;
    @FXML
    private TreeTableColumn<Sections, String> tbcType;
    @FXML
    private JFXTreeTableView<Sections> tbvSections;
    @FXML
    private MFXTextField txfSearch;

    private final ObservableList<Sections> listSections = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tbvSections.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvSections.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbType.getItems().addAll("Salon", "VentaDirecta");

        tbcName.setCellValueFactory(x -> x.getValue().getValue().getName());
        tbcTax.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().getValue().getTax().get() ? "si" : "no"));
        tbcType.setCellValueFactory(x -> x.getValue().getValue().getType());
        tbcTableGraphic.setCellValueFactory(x -> x.getValue().getValue().getImage());
        tbvSections.setOnMouseClicked(x -> {
            if (x.getClickCount() == 2 && tbvSections.getSelectionModel().getSelectedItem() != null) {
                Sections selected = tbvSections.getSelectionModel().getSelectedItem().getValue();
                onEditSection(selected);
            }
        });

        TreeItem<Sections> root = new RecursiveTreeItem<>(listSections, RecursiveTreeObject::getChildren);
        tbvSections.setRoot(root);
        tbvSections.setShowRoot(false);

        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> filters());
        cmbType.valueProperty().addListener((obs, oldVal, newVal) -> filters());

    }

    @Override
    public void initialize() {

    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_SECTION, new Stage(), false);
    }

    @FXML
    private void onEditSection(Sections sect) {
        NewSectionController controller = (NewSectionController) FlowController.getInstance().getController(AppKeys.NEW_SECTION);

        controller.loadSection(sect);
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_SECTION, new Stage(), false);

    }

    public void addSection(String name, String type, boolean tax, String image) {

        listSections.add(new Sections(name, type, tax, image));
        filters();
    }

    private void filters() {

        String search = txfSearch.getText() == null ? "" : txfSearch.getText().toLowerCase();
        String type = cmbType.getValue();

        ObservableList<Sections> filter = listSections.filtered(x
                -> x.getName().get().toLowerCase().contains(search) || x.getType().get().toLowerCase().contains(search))
                .filtered(t -> type == null || type.isEmpty() || t.getType().get().equals(type));

        TreeItem<Sections> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
        tbvSections.setRoot(root);
        tbvSections.setShowRoot(false);

    }
}
