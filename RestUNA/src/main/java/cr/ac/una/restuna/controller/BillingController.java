package cr.ac.una.restuna.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableColumn;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class BillingController extends Controller implements Initializable {

    /*métodos de pago*/
    @FXML
    private MFXButton btnCash;
    @FXML
    private MFXButton btnCard;
    @FXML
    private MFXButton btnSinpe;
    @FXML
    private MFXButton btnTip;
    /*digitación de monto*/
    @FXML
    private MFXButton btnNine;
    @FXML
    private MFXButton btnEight;
    @FXML
    private MFXButton btnSeven;
    @FXML
    private MFXButton btnSix;
    @FXML
    private MFXButton btnFive;
    @FXML
    private MFXButton btnFour;
    @FXML
    private MFXButton btnThree;
    @FXML
    private MFXButton btnTwo;
    @FXML
    private MFXButton btnOne;
    @FXML
    private MFXButton btnZero;
    @FXML
    private MFXButton btnDot;
    @FXML
    private MFXButton btnErase;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void initialize() {
    }

    /*métodos de pago*/
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

    /*digitar monto*/
    @FXML
    void onActionBtnNine(ActionEvent event) {

    }

    @FXML
    void onActionBtnEight(ActionEvent event) {

    }

    @FXML
    void onActionBtnSeven(ActionEvent event) {

    }

    @FXML
    void onActionBtnSix(ActionEvent event) {

    }

    @FXML
    void onActionBtnFive(ActionEvent event) {

    }

    @FXML
    void onActionBtnFour(ActionEvent event) {

    }

    @FXML
    void onActionBtnThree(ActionEvent event) {

    }

    @FXML
    void onActionBtnTwo(ActionEvent event) {

    }

    @FXML
    void onActionBtnOne(ActionEvent event) {

    }

    @FXML
    void onActionBtnZero(ActionEvent event) {

    }

    @FXML
    void onActionBtnDot(ActionEvent event) {

    }

    @FXML
    void onActionBtnErase(ActionEvent event) {

    }

    /*facturar*/
    @FXML
    void onActionBtnOk(ActionEvent event) {

    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {

    }
}
