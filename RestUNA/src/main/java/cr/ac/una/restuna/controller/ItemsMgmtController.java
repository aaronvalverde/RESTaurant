package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
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
import javafx.scene.control.Alert;
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
    private TreeTableColumn<ProductoDto, Long> tbcID;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcName;
    @FXML
    private TreeTableColumn<ProductoDto, Double> tbcPrice;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcShortcut;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcStatus;
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

    
        tbcID.setCellValueFactory(x -> x.getValue().getValue().idProductoProperty().asObject());
        tbcName.setCellValueFactory(x -> x.getValue().getValue().nombreProperty());
        tbcPrice.setCellValueFactory(x -> x.getValue().getValue().precioProperty().asObject());
        tbcShortcut.setCellValueFactory(x -> x.getValue().getValue().nombreCortoProperty());
        tbcStatus.setCellValueFactory(x -> x.getValue().getValue().estadoProperty());

        
        TreeItem<ProductoDto> root = new RecursiveTreeItem<>(product, RecursiveTreeObject::getChildren);
        tbvMenuItems.setRoot(root);
        tbvMenuItems.setShowRoot(false);

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

    @Override
    public void initialize() {
    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        try {
            NewItemController item = (NewItemController) FlowController.getInstance().getController(AppKeys.NEW_MENU_ITEM);
            item.clear(); 
            FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_ITEM, new Stage(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // este onAction no se esta usando por el momento 
    @FXML
    private void onEditSection(ProductoDto produ) {
        SeccionDto seccion = new SeccionDto();
        NewItemController controller = new NewItemController();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_ITEM, new Stage(), false);
        controller.loadSection(seccion);
        controller.loadProduct(produ);

    }

    public void addProduct(String name, String shortName, double price, String description, String shortcut, String status) {

        for (ProductoDto product : product) {
            if (product.getNombre().equalsIgnoreCase(name)) {
                showMessage("El producto ya existe: " + name);
                return;
            }
        }
        ProductoDto newProduct = new ProductoDto();
        newProduct.setIdProducto(System.currentTimeMillis());
        newProduct.setNombre(name);
        newProduct.setNombreCorto(shortName);
        newProduct.setPrecio(price);
        newProduct.setDescripcion(description);
        newProduct.setAccesoRapido(shortcut);
        newProduct.setEstado(status);

        product.add(newProduct);

    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
