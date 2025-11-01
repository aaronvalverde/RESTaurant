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
        lbTotal.setText(String.format("$ %.2f", detail.getSubtotal()));
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
        lbItemPrice.setText(String.format("₡ %.2f", product.getPrecio()));
        lbQuantity.setText(quantity.toString());
        lbTotal.setText(String.format("₡ %.2f", product.getPrecio()));
    }

    public void setParentController(OrderController controller) {
        this.parentController = controller;
    }

    public void OrderItemController(String itemName, Double itemPrice) {
        price = itemPrice;
        lbItemName.setText(itemName);
        lbItemPrice.setText(itemPrice.toString());
    }
}
