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
import javafx.scene.control.TreeTableColumn;
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
        currentClosing.setEfectivoDeclarado(10000L); // simulado
        currentClosing.setTarjetaDeclarado(5000L);   // simulado
        currentClosing.setEfectivoSistema(10000L);
        currentClosing.setTarjetaSistema(5000L);
        currentClosing.setDiferenciaEfectivo(0L);
        currentClosing.setDiferenciaTarjeta(0L);

        showMessage("Cierre de caja completado correctamente.");
        CashOpeningController.activeOpening = null; // se cierra la sesión de caja
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
        //tbcKey : agregar todos los métodos de pago y tip.
        //tbcTotalAmount : agregar valores en ceros $0.00.
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
