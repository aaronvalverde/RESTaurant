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
    private double totalTip = 0.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadNumberKeypad();

    }

    @Override
    public void initialize() {
    }

    private void payTable() {

        tbcKey.setCellValueFactory(cellData -> {

            DetalleFacturaDto detailTable = cellData.getValue().getValue();
            String payMethod = "";
            if (detailTable.getIdProducto() != null) {
                switch (detailTable.getIdProducto().intValue()) {
                    case 1:
                        payMethod = "Cash";
                        break;
                    case 2:
                        payMethod = "Card";
                        break;
                    case 3:
                        payMethod = "Tip";
                        break;                      
               }
            }
            return new SimpleStringProperty(payMethod);
        });
        
        tbcTotalAmount.setCellValueFactory(cellData -> {
            
            DetalleFacturaDto detail = cellData.getValue().getValue();
            return new SimpleStringProperty(String.format("$%,.2f", detail.getSubtotal() / 100.0));
        });
        
        TreeItem<DetalleFacturaDto> root = new RecursiveTreeItem<>(detailBill, RecursiveTreeObject::getChildren);
        tbvPaymentBreakdown.setRoot(root);
        tbvPaymentBreakdown.setShowRoot(false);
    }

    //métodos de pago
    @FXML
    void onActionBtnCash(ActionEvent event) {

        DetalleFacturaDto detail = new DetalleFacturaDto();

        detail.setIdProducto(System.currentTimeMillis());
        detail.setCantidad(1L);
        detail.setPrecioUnitario(2000L);
        detail.setSubtotal(2000L);
        detailBill.add(detail);
        totalToPay += 2000;
        txfTotalDue.setText(String.format("%.2f", totalToPay));
        txfAmountDue.setText(String.format("%.2f", totalToPay - totalPaid));
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

        if (txfClient.getText().trim().isEmpty()) {
            showMessage("Ingrese el nombre del cliente");
            return;
        }

        currentBill.setTotal((long) totalToPay);
        currentBill.setEfectivoRecibido((long) totalPaid);
        currentBill.setVuelto((long) (totalPaid - totalToPay));
        currentBill.setIdCliente(1L);
        currentBill.setCorreoEnviado("N");

        showMessage("Factura generada correctamente.\nTotal: ₡" + currentBill.getTotal());
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
            addPay(amount);
        } catch (NumberFormatException e) {

        }
    }

    
    private void PayRegister(Long idMethod){
        
        if(txfAmount.getText().trim().isEmpty()){
            
            showMessage("Ingrese el monto correspondiente");
            return;
        }
        
        try{
            
            double amount = Double.parseDouble(txfAmount.getText().trim());
            if(amount <= 0){
                
                showMessage("El monto debe ser mayor a cero");
                return;
                
            }
            
            DetalleFacturaDto detail = new DetalleFacturaDto();
            detail.setIdDetalleFactura(System.currentTimeMillis());
            detail.setIdProducto(idMethod);
            detail.setCantidad(1L);
            detail.setPrecioUnitario((long) (amount *100));
            detail.setSubtotal((long) (amount * 100));
            detailBill.add(detail);
            
            totalPaid += amount;
            txfAmount.clear();
            
        }catch(NumberFormatException e){
            
            
        }
        
        
    }
    private void addPay(double amount) {

        totalPaid += amount;
        txfAmountTendered.setText(String.format("%.2f", totalPaid));

        double change = totalPaid - totalToPay;
        if (change >= 0) {
            txfChange.setText(String.format("%.2f", change));
            txfAmountDue.setText("0.00");
        } else {
            txfAmountDue.setText(String.format("%.2f", -change));
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
}
