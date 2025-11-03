package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.DetalleOrdenDto;
import cr.ac.una.restuna.model.ProductoDto;
import cr.ac.una.restuna.util.BillingCalculator;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class OrderItemController extends Controller implements Initializable {

    @FXML
    private Label lbItemName;
    @FXML
    private Label lbItemPrice;
    @FXML
    private MFXButton btnSubstract;
    @FXML
    private Label lbQuantity;
    @FXML
    private MFXButton btnAdd;
    @FXML
    private Label lbTotal;
    @FXML
    private MFXButton btnRemove;
    @FXML
    private AnchorPane root;

    private Integer quantity = 1;
    private Double price = 0.0;
    private ProductoDto product;
    private DetalleOrdenDto detail;
    private OrderController parentController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnSubstract(ActionEvent event) {
        if (quantity > 1) {
            quantity--;
            updateValues();
        } else {
            ((Pane) root.getParent()).getChildren().remove(root);
            parentController.deleteDetail(detail);
        }
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        quantity++;
        updateValues();
    }

    @FXML
    private void onActionBtnRemove(ActionEvent event) {
        ((Pane) root.getParent()).getChildren().remove(root);
    }

    private void updateValues() {

        detail.setCantidad(quantity);
        detail.setSubtotal(price * quantity);
        lbQuantity.setText(quantity.toString());
        
        // Usar el método formatearPrecio del controlador padre que ya hace la conversión
        if (parentController != null) {
            lbTotal.setText(parentController.formatearPrecio(detail.getSubtotal()));
        }
        
        parentController.updateTotals();
    }

    public void selectProduct(ProductoDto product) {
        this.product = product;
        this.price = product.getPrecio();

        this.detail = new DetalleOrdenDto();
        this.detail.setIdProducto(product.getIdProducto());
        this.detail.setPrecioUnitario(product.getPrecio());
        this.detail.setCantidad(1);
        this.detail.setSubtotal(product.getPrecio());

        lbItemName.setText(product.getNombre());
        lbQuantity.setText(quantity.toString());
        
        // Los precios se actualizarán cuando se llame setParentController -> updatePriceDisplay
    }
    
    /**
     * Cargar un detalle existente de la base de datos con su cantidad específica
     * Se usa al cargar órdenes existentes desde la BD
     */
    public void loadExistingDetail(ProductoDto product, DetalleOrdenDto existingDetail) {
        this.product = product;
        this.price = product.getPrecio();
        this.detail = existingDetail;
        
        // Establecer la cantidad desde el detalle existente
        this.quantity = existingDetail.getCantidad() != null ? existingDetail.getCantidad() : 1;
        
        lbItemName.setText(product.getNombre());
        lbQuantity.setText(quantity.toString());
        
        // Los precios se actualizarán cuando se llame setParentController -> updatePriceDisplay
    }

    public void setParentController(OrderController controller) {
        this.parentController = controller;
        // Actualizar precios con la moneda correcta
        updatePriceDisplay();
    }
    
    /**
     * Actualizar visualización de precios con la moneda actual
     */
    public void updatePriceDisplay() {
        if (parentController == null || price == null) {
            return;
        }
        
        // Usar el método formatearPrecio del controlador padre que ya hace la conversión
        lbItemPrice.setText(parentController.formatearPrecio(price));
        lbTotal.setText(parentController.formatearPrecio(price * quantity));
    }
    
    /**
     * Obtener el detalle de la orden
     */
    public DetalleOrdenDto getDetail() {
        return this.detail;
    }

    public void OrderItemController(String itemName, Double itemPrice) {
        price = itemPrice;
        lbItemName.setText(itemName);
        lbItemPrice.setText(itemPrice.toString());
    }
}
