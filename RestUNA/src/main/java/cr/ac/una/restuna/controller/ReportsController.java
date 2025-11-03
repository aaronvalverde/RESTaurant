package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class ReportsController extends Controller implements Initializable {

    @FXML
    private MFXButton btnBillingReport;
    @FXML
    private MFXButton btnCashClosingReport;
    @FXML
    private MFXButton btnSalesReport;
    @FXML
    private Label lbReportType;
    @FXML
    private VBox vboxCashClosing;
    @FXML
    private MFXComboBox<?> cmbCashier;
    @FXML
    private MFXDatePicker dpDate;
    @FXML
    private VBox vboxNonCashClosing;
    @FXML
    private MFXDatePicker dpStartDate;
    @FXML
    private MFXDatePicker dpEndDate;
    @FXML
    private MFXButton btnGenerate;
    @FXML
    private MFXButton btnPrint;
    @FXML
    private MFXButton btnDownload;
    @FXML
    private MFXButton btnClose;

    private MFXButton activeButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initView();
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnBillingReport(ActionEvent event) {
        setActiveButton(btnBillingReport);
        lbReportType.setText(getLanguageString("lb.billing.report"));
        setBoxes(false);
    }

    @FXML
    private void onActionBtnCashClosingReport(ActionEvent event) {
        setActiveButton(btnCashClosingReport);
        lbReportType.setText(getLanguageString("lb.cashClosing.report"));
        loadCashierOptions();
        setBoxes(true);
    }

    @FXML
    private void onActionBtnSalesReport(ActionEvent event) {
        setActiveButton(btnSalesReport);
        lbReportType.setText(getLanguageString("lb.sales.report"));
        setBoxes(false);
    }

    @FXML
    private void onActionBtnGenerate(ActionEvent event) {
    }

    @FXML
    private void onActionBtnPrint(ActionEvent event) {
        //validar si hay contenido para "imprimir", de lo contrario mostrar mensaje de error.
        //esto es un mensaje genérico.
        showMessage(getLanguageString("lb.print.success"));
    }

    @FXML
    private void onActionBtnDownload(ActionEvent event) {
    }

    @FXML
    private void onActionBtnClose(ActionEvent event) {
        FlowController.getInstance().goHome();
    }

    private String getLanguageString(String key) {
        return FlowController.getInstance().getLanguage().getString(key);
    }

    private void setBoxes(Boolean isVisible) {
        vboxCashClosing.setVisible(isVisible);
        vboxCashClosing.setManaged(isVisible);
        vboxNonCashClosing.setVisible(!isVisible);
        vboxNonCashClosing.setManaged(!isVisible);
    }

    private void initView() {
        lbReportType.setText(getLanguageString("lb.billing.report"));
        setBoxes(false);
    }

    private void loadCashierOptions() {
        //cmbCashier
        //cargar todos los cajeros registrados en el sistema.
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(getLanguageString("lb.information"));
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void setActiveButton(MFXButton button) {
        if (activeButton != null) {
            activeButton.setStyle("");
        }
        button.setStyle("-fx-background-color: #475569;\n-fx-border-color: #475569");
        activeButton = button;
    }
}
