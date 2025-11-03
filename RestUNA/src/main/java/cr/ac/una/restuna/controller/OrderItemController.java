package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.DetalleOrdenDto;
import cr.ac.una.restuna.model.ProductoDto;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;


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
        
        
    }
    
    
    public void loadExistingDetail(ProductoDto product, DetalleOrdenDto existingDetail) {
        this.product = product;
        this.price = product.getPrecio();
        this.detail = existingDetail;
        
        
        this.quantity = existingDetail.getCantidad() != null ? existingDetail.getCantidad() : 1;
        
        lbItemName.setText(product.getNombre());
        lbQuantity.setText(quantity.toString());
        
        
    }

    public void setParentController(OrderController controller) {
        this.parentController = controller;
        
        updatePriceDisplay();
    }
    
    
    public void updatePriceDisplay() {
        if (parentController == null || price == null) {
            return;
        }
        
        
        lbItemPrice.setText(parentController.formatearPrecio(price));
        lbTotal.setText(parentController.formatearPrecio(price * quantity));
    }
    
    
    public DetalleOrdenDto getDetail() {
        return this.detail;
    }

    public void OrderItemController(String itemName, Double itemPrice) {
        price = itemPrice;
        lbItemName.setText(itemName);
        lbItemPrice.setText(itemPrice.toString());
    }
}
