package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.model.SeccionDto;
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
    private TreeTableColumn<SeccionDto, String> tbcName;
    @FXML
    private TreeTableColumn<SeccionDto, String> tbcTableGraphic;
    @FXML
    private TreeTableColumn<SeccionDto, String> tbcTax;
    @FXML
    private TreeTableColumn<SeccionDto, String> tbcType;
    @FXML
    private JFXTreeTableView<SeccionDto> tbvSections;
    @FXML
    private MFXTextField txfSearch;

    private final ObservableList<SeccionDto> listSections = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tbvSections.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvSections.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbType.getItems().addAll("Salon", "VentaDirecta");

        tbcName.setCellValueFactory(x -> x.getValue().getValue().nombreProperty());
        tbcTax.setCellValueFactory(x -> new ReadOnlyStringWrapper("S".equals(x.getValue().getValue().cobraImpuestoProperty()) ? "si" : "no"));
        tbcType.setCellValueFactory(x -> x.getValue().getValue().tipoProperty());
        tbcTableGraphic.setCellValueFactory(x -> new ReadOnlyStringWrapper(
                x.getValue().getValue().getIdArchivoImagen() != null
                ? x.getValue().getValue().getIdArchivoImagen().toString() : "Sin imagen"));
        tbvSections.setOnMouseClicked(x -> {
            if (x.getClickCount() == 2 && tbvSections.getSelectionModel().getSelectedItem() != null) {
                SeccionDto selected = tbvSections.getSelectionModel().getSelectedItem().getValue();
                onEditSection(selected);
            }
        });

        TreeItem<SeccionDto> root = new RecursiveTreeItem<>(listSections, RecursiveTreeObject::getChildren);
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
    private void onEditSection(SeccionDto sect) {
        NewSectionController controller = (NewSectionController) FlowController.getInstance().getController(AppKeys.NEW_SECTION);

        controller.loadSection(sect);
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_SECTION, new Stage(), false);

    }

    public void addSection(String name, String type, String tax, Long image) {

        SeccionDto newSection = new SeccionDto();
        newSection.setIdSeccion(System.currentTimeMillis());
        newSection.setNombre(name);
        newSection.setTipo(type);
        newSection.setCobraImpuesto(tax);
        newSection.setEstado("A");
        newSection.setIdArchivoImagen(image);

        listSections.add(newSection);
        filters();
    }

    private void filters() {

        String search = txfSearch.getText() == null ? "" : txfSearch.getText().toLowerCase();
        String type = cmbType.getValue();

        ObservableList<SeccionDto> filter = listSections.filtered(x
                -> x.getNombre().toLowerCase().contains(search)
                || x.getTipo().toLowerCase().contains(search))
                .filtered(t -> type == null || type.isEmpty() || t.getTipo().equals(type));

        TreeItem<SeccionDto> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
        tbvSections.setRoot(root);
        tbvSections.setShowRoot(false);

    }
}
