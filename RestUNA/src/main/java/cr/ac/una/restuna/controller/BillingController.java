package cr.ac.una.restuna.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class BillingController extends Controller implements Initializable {

    //métodos de pago
    @FXML
    private MFXButton btnCash;
    @FXML
    private MFXButton btnCard;
    @FXML
    private MFXButton btnSinpe;
    @FXML
    private MFXButton btnTip;

    @FXML
    private MFXButton btnOk;
    @FXML
    private MFXButton btnCancel;

    @FXML
    private MFXTextField txfAmount; //se actualiza en cada interacción con los botones de digitación.
    @FXML
    private TreeTableColumn<?, ?> tbcKey; //efectivo, tarjeta, sinpe.
    @FXML
    private TreeTableColumn<?, ?> tbcTotalAmount; //lo registrado en cada método de pago.
    @FXML
    private JFXTreeTableView<?> tbvPaymentBreakdown;
    @FXML
    private MFXTextField txfAmountDue; //monto pendiente (se actualiza en base a la resta del monto fijo - monto pagado, si es negativo se debe actualizar el cambio y colocar este en ceros).
    @FXML
    private MFXTextField txfAmountTendered; //monto pagado (en base a lo registrado en los distintos métodos de pago).
    @FXML
    private MFXTextField txfChange; //cambio (se actualiza si se excede del monto).
    @FXML
    private MFXTextField txfClient; //nombre del cliente (obligatorio).
    @FXML
    private MFXTextField txfClientEmail; //correo para enviar factura (opcional).
    @FXML
    private MFXTextField txfTotalDue; //monto a pagar (es fijo).
    @FXML
    private MFXTextField txfTotalTip; //propina. 
    @FXML
    private VBox numberKeypadRoot;
    @FXML
    private MFXButton btnRegisterAmount;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadNumberKeypad();
    }

    @Override
    public void initialize() {
    }

    //métodos de pago
    @FXML
    void onActionBtnCash(ActionEvent event) {

    }

    @FXML
    void onActionBtnCard(ActionEvent event) {

    }

    @FXML
    void onActionBtnSinpe(ActionEvent event) {

    }

    @FXML
    void onActionBtnTip(ActionEvent event) {

    }

    //facturar
    @FXML
    void onActionBtnOk(ActionEvent event) {

    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {

    }

    @FXML
    private void onActionBtnRegisterAmount(ActionEvent event) {
    }

    private void loadNumberKeypad() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/NumberKeypad.fxml"));
            AnchorPane keypadView = loader.load();
            NumberKeypadController numberKeypad = loader.getController();

            numberKeypad.setInputRoot(txfAmount);
            numberKeypadRoot.getChildren().add(keypadView);
            
            keypadView.prefHeightProperty().bind(numberKeypadRoot.heightProperty());
            keypadView.prefWidthProperty().bind(numberKeypadRoot.widthProperty());
            
        } catch (IOException ex) {
            System.getLogger(BillingController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
