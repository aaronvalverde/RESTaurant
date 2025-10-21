package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import cr.ac.una.restuna.dto.ProductoDto;
import cr.ac.una.restuna.dto.SeccionDto;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
    private MFXFilterComboBox<String> cmbGroups;
    @FXML
    private MFXFilterComboBox<String> cmbStatus;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<ProductoDto, Long> tbcActions;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcID;
    @FXML
    private TreeTableColumn<ProductoDto, Double> tbcName;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcPrice;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcShortcut;
    @FXML
    private TreeTableColumn<ProductoDto, Void> tbcStatus;
    @FXML
    private JFXTreeTableView<ProductoDto> tbvMenuItems;
    @FXML
    private MFXTextField txfSearch;

    private final ObservableList<ProductoDto> product = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvMenuItems.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvMenuItems.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbGroups.getItems().setAll("Bebidas Calientes", "Bebidas Frias", "Platos Fuertes", "Entradas", "Postres");
        cmbStatus.getItems().setAll("A", "I");

        tbcID.setCellValueFactory(new TreeItemPropertyValueFactory<>("idProducto"));
        tbcName.setCellValueFactory(new TreeItemPropertyValueFactory<>("nombre"));
        tbcPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("precio"));
        tbcShortcut.setCellValueFactory(new TreeItemPropertyValueFactory<>("nombreCorto"));
        tbcStatus.setCellValueFactory(new TreeItemPropertyValueFactory<>("estado"));

        reloadTable();
        confEvent();
    }

    private void confEvent() {

        tbvMenuItems.setOnMouseClicked((MouseEvent event) -> {

            if (event.getClickCount() == 2) {
                ProductoDto select = tbvMenuItems.getSelectionModel().getSelectedItem().getValue();
                if (select != null) {
                    onEditSection(select);
                }
            }
        });
    }

    private void reloadTable() {

        TreeItem<ProductoDto> root = new TreeItem<>();
        for (ProductoDto pro : product) {
            root.getChildren().add(new TreeItem<>(pro));
        }

        tbvMenuItems.setRoot(root);
        tbvMenuItems.setShowRoot(false);
    }

    @Override
    public void initialize() {
    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        NewItemController item = new NewItemController();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_ITEM, new Stage(), false);
        item.loadSection(null);
        
        
        ProductoDto nuevoProducto = item.getProductoCreado();
        if (nuevoProducto != null) {
            nuevoProducto.setIdProducto((long) (product.size() + 1));
            product.add(nuevoProducto);
            reloadTable();
        }
    }

    @FXML
    private void onEditSection(ProductoDto produ) {
        SeccionDto seccion = new SeccionDto();
        NewItemController controller = new NewItemController();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_ITEM, new Stage(), false);
        controller.loadSection(seccion);
        controller.loadProduct(produ);
    }

}
