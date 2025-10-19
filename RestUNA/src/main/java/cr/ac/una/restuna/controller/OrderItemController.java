package cr.ac.una.restuna.controller;

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
        quantity--;
        if (quantity == 0) {
            ((Pane) root.getParent()).getChildren().remove(root);
            return;
        }
        lbQuantity.setText(quantity.toString());
        Double total = price * quantity;
        lbTotal.setText(total.toString());
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        quantity++;
        lbQuantity.setText(quantity.toString());
        Double total = price * quantity;
        lbTotal.setText(total.toString());
    }

    @FXML
    private void onActionBtnRemove(ActionEvent event) {
        ((Pane) root.getParent()).getChildren().remove(root);
    }

    public void OrderItemController(String itemName, Double itemPrice) {
        price = itemPrice;
        lbItemName.setText(itemName);
        lbItemPrice.setText(itemPrice.toString());
    }
}
