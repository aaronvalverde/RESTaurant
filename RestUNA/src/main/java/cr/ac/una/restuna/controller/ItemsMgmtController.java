package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.dto.ProductoDto;
import cr.ac.una.restuna.service.ProductoService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private MFXButton btnBack;
    @FXML
    private MFXFilterComboBox<String> cmbGroups;
    @FXML
    private MFXFilterComboBox<String> cmbStatus;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<ProductoDto, Void> tbcActions;
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
    private final ProductoService productoService = new ProductoService();

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

        TreeItem<ProductoDto> root = new RecursiveTreeItem<>(product, RecursiveTreeObject::getChildren);
        tbvMenuItems.setRoot(root);
        tbvMenuItems.setShowRoot(false);

        confEvent();
        setActionsColumn();
    }

    private void confEvent() {

        tbvMenuItems.setOnMouseClicked((MouseEvent event) -> {

            if (event.getClickCount() == 2) {
                ProductoDto select = tbvMenuItems.getSelectionModel().getSelectedItem().getValue();
                if (select != null) {
                    onEditItem(select);
                }
            }
        });
    }

    @Override
    public void initialize() {
    }

    @FXML
    void onActionBtnBack(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MANAGEMENT);
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
    private void onEditItem(ProductoDto productoDto) {
        NewItemController controller = (NewItemController) FlowController.getInstance()
                .getController(AppKeys.NEW_MENU_ITEM);
        controller.setParentController(this);
        controller.loadProduct(productoDto);
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_ITEM, new Stage(), false);
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
        newProduct.setPrecio(BigDecimal.valueOf(price));
        newProduct.setDescripcion(description);
        newProduct.setAccesoRapido(shortcut);
        newProduct.setEstado(status);

        product.add(newProduct);

    }

    private void setActionsColumn() {
        tbcActions.setCellFactory(col -> new TreeTableCell<ProductoDto, Void>() {
            MFXButton btnEdit = new MFXButton(" ");
            MFXButton btnDelete = new MFXButton();

            {
                btnEdit.setGraphic(new ImageView(new Image("../resources/icons/icons8-edit-50.png")));
                btnDelete.setGraphic(new ImageView(new Image("../resources/icons/icons8-delete-50.png")));

                btnEdit.setOnAction(e -> {
                    ProductoDto productoDto = getTreeTableRow().getItem();
                    if (productoDto != null) {
                        onEditItem(productoDto);
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

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    /**
     * Carga productos desde el servidor
     */
    public void loadProductsFromServer() {
        System.out.println("DEBUG: Cargando productos desde servidor");
        Respuesta respuesta = productoService.getProductos();
        
        if (!respuesta.getEstado()) {
            System.err.println("Error cargando productos: " + respuesta.getMensaje());
            showMessage("Error cargando productos: " + respuesta.getMensaje());
            return;
        }
        
        String contenido = (String) respuesta.getResultado("Productos");
        
        if (contenido == null || contenido.trim().isEmpty()) {
            System.out.println("No hay productos disponibles");
            product.clear();
            return;
        }
        
        product.clear();
        
        // Extraer objetos JSON
        List<String> objetosProductos = JsonParser.extraerObjetosDelArray(contenido);
        
        for (String objetoJson : objetosProductos) {
            ProductoDto producto = new ProductoDto(objetoJson);
            product.add(producto);
        }
        
        // Refrescar la tabla con el workaround de JFXTreeTableView
        TreeItem<ProductoDto> root = new RecursiveTreeItem<>(product, RecursiveTreeObject::getChildren);
        tbvMenuItems.setRoot(null);
        tbvMenuItems.setRoot(root);
        
        System.out.println("DEBUG: Productos cargados: " + product.size());
    }
}
