package cr.ac.una.restuna.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.model.CierreCajaDto;
import cr.ac.una.restuna.model.DetalleFacturaDto;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
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
public class CashClosingController extends Controller implements Initializable {

    @FXML
    private MFXButton btnCancel;
    @FXML
    private MFXButton btnCard;
    @FXML
    private MFXButton btnCash;
    @FXML
    private MFXButton btnEnter;
    @FXML
    private MFXButton btnOk;
    @FXML
    private MFXButton btnSinpe;
    @FXML
    private MFXButton btnTip;
    @FXML
    private VBox keypadRoot;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<CierreCajaDto, String> tbcKey;
    @FXML
    private TreeTableColumn<CierreCajaDto, String> tbcTotalAmount;
    @FXML
    private JFXTreeTableView<CierreCajaDto> tbvPaymentBreakdown;
    @FXML
    private MFXTextField txfInput;

    private ObservableList<CierreCajaDto> closingBox = FXCollections.observableArrayList();
    private CierreCajaDto currentClosing = new CierreCajaDto();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvPaymentBreakdown.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvPaymentBreakdown.prefWidthProperty().bind(tableRoot.widthProperty());
        loadKeypad();
        initTableColumns();

    }

    @Override
    public void initialize() {
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void onActionBtnCard(ActionEvent event) {

    }

    @FXML
    void onActionBtnCash(ActionEvent event) {

    }

    @FXML
    void onActionBtnEnter(ActionEvent event) {

    }

    @FXML
    void onActionBtnOk(ActionEvent event) {
        if (CashOpeningController.activeOpening == null) {
            showMessage("No hay apertura activa para cerrar.");
            return;
        }

        currentClosing = CashOpeningController.activeOpening;
        currentClosing.setFechaCierre(new Date());
        currentClosing.setEstado("CERRADA");
        showMessage("Cierre de caja completado correctamente.");
        CashOpeningController.activeOpening = null;
        closeWindow();
    }

    @FXML
    void onActionBtnSinpe(ActionEvent event) {

    }

    @FXML
    void onActionBtnTip(ActionEvent event) {

    }

    @FXML
    void onActionTxfInput(ActionEvent event) {

    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void loadKeypad() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/NumberKeypad.fxml"));
            AnchorPane keypadView = loader.load();
            NumberKeypadController numberKeypad = loader.getController();

            numberKeypad.setInputRoot(txfInput);
            keypadRoot.getChildren().add(keypadView);

            keypadView.prefHeightProperty().bind(keypadRoot.heightProperty());
            keypadView.prefWidthProperty().bind(keypadRoot.widthProperty());

        } catch (IOException ex) {
            System.getLogger(BillingController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void initTableColumns() {
        tbcKey.setCellValueFactory(new TreeItemPropertyValueFactory<>("estado"));
        tbcTotalAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("observaciones"));
        
        List<String> payMethod = Arrays.asList("Cash","Card","Sinpe","Tip");
        TreeItem<CierreCajaDto> root = new TreeItem<>(new CierreCajaDto());
        
        for(String method : payMethod){
            CierreCajaDto payM = new CierreCajaDto();
            payM.setEstado(method);
            payM.setObservaciones("$0.00");
            root.getChildren().add(new TreeItem<>(payM));
            
        }
        
        tbvPaymentBreakdown.setRoot(root);
        tbvPaymentBreakdown.setShowRoot(false);
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
