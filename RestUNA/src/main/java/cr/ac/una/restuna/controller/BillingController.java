package cr.ac.una.restuna.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.controller.Controller;
import cr.ac.una.restuna.controller.NumberKeypadController;
import cr.ac.una.restuna.model.DetalleFacturaDto;
import cr.ac.una.restuna.model.FacturaDto;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.util.Date;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private TreeTableColumn<DetalleFacturaDto, String> tbcKey; //efectivo, tarjeta, sinpe.
    @FXML
    private TreeTableColumn<DetalleFacturaDto, String> tbcTotalAmount; //lo registrado en cada método de pago.
    @FXML
    private JFXTreeTableView<DetalleFacturaDto> tbvPaymentBreakdown;
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

    private ObservableList<DetalleFacturaDto> detailBill = FXCollections.observableArrayList();
    private FacturaDto currentBill = new FacturaDto();

    private double totalToPay = 0.0;
    private double totalPaid = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configTable();
        loadNumberKeypad();
        clear();

    }

    @Override
    public void initialize() {
    }

    private void configTable() {

        tbcKey.setCellValueFactory(new TreeItemPropertyValueFactory<>("idProducto"));
        tbcTotalAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("subtotal"));

        TreeItem<DetalleFacturaDto> root = new TreeItem<>(new DetalleFacturaDto());
        tbvPaymentBreakdown.setRoot(root);
        tbvPaymentBreakdown.setShowRoot(false);

    }

    //métodos de pago
    @FXML
    void onActionBtnCash(ActionEvent event) {

        addPay("Cash");
    }

    @FXML
    void onActionBtnCard(ActionEvent event) {
        addPay("Card");
    }

    @FXML
    void onActionBtnSinpe(ActionEvent event) {
        addPay("Sinpe");
    }

    @FXML
    void onActionBtnTip(ActionEvent event) {
        addPay("Tip");
    }

    //facturar
    @FXML
    void onActionBtnOk(ActionEvent event) {

        if (txfClient.getText().trim().isEmpty()) {
            showMessage("Ingrese el nombre del cliente.");
            return;
        }
        currentBill = new FacturaDto();
        currentBill.setIdFactura(System.currentTimeMillis());
        currentBill.setTotal((long) totalToPay);
        currentBill.setEfectivoRecibido((long) totalPaid);
        currentBill.setVuelto((long) (totalPaid - totalToPay));
        currentBill.setFechaFactura(new Date());
        showMessage("Factura generada correctamente.");
        closeWindow();
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void onActionBtnRegisterAmount(ActionEvent event) {

        try {
            double amount = Double.parseDouble(txfAmount.getText().trim());
            totalToPay += amount;
            txfTotalDue.setText(String.format("%.2f", totalToPay));
            updateTotal();
        } catch (NumberFormatException e) {
            showMessage("Ingrese un monto válido.");
        }
    }

    private void addPay(String method) {

        try {
            double amount = Double.parseDouble(txfAmount.getText().trim());
            DetalleFacturaDto detail = new DetalleFacturaDto();

            detail.setIdDetalleFactura(System.currentTimeMillis());
            detail.setIdProducto((long) (detailBill.size() + 1));
            detail.setPrecioUnitario((long) amount);
            detail.setCantidad(1L);
            detail.setSubtotal((long) amount);
            detailBill.add(detail);

            TreeItem<DetalleFacturaDto> item = new TreeItem<>(detail);
            tbvPaymentBreakdown.getRoot().getChildren().add(item);

            totalPaid += amount;
            txfAmountTendered.setText(String.format("%.2f", totalPaid));
            updateTotal();

        } catch (NumberFormatException e) {

        }
    }

    private void updateTotal() {
        double change = totalPaid - totalToPay;
        if (change >= 0) {
            txfChange.setText(String.format("%.2f", change));
            txfAmountDue.setText("0.00");
        } else {
            txfAmountDue.setText(String.format("%.2f", -change));
            txfChange.setText("0.00");
        }
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

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clear() {
        txfAmount.setText("");
        txfTotalDue.setText("0.00");
        txfAmountDue.setText("0.00");
        txfAmountTendered.setText("0.00");
        txfChange.setText("0.00");
        txfTotalTip.setText("0.00");
        totalPaid = 0.0;
        totalToPay = 0.0;
        detailBill.clear();
    }
}