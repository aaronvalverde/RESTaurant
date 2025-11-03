package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.service.ReporteService;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

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
    private MFXComboBox<Long> cmbCashier;
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
    private byte[] pdf;
    private String reportType;
    private ReporteService reporteService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reporteService = new ReporteService();
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
        reportType = "FACTURAS";

    }

    @FXML
    private void onActionBtnCashClosingReport(ActionEvent event) {
        setActiveButton(btnCashClosingReport);
        lbReportType.setText(getLanguageString("lb.cashClosing.report"));
        loadCashierOptions();
        setBoxes(true);
        reportType = "CIERRE_CAJA";
    }

    @FXML
    private void onActionBtnSalesReport(ActionEvent event) {
        setActiveButton(btnSalesReport);
        lbReportType.setText(getLanguageString("lb.sales.report"));
        setBoxes(false);
        reportType = "PROUDCTO_VENDIDO";
    }

    @FXML
    private void onActionBtnGenerate(ActionEvent event) {

        try {
            Respuesta respuesta;

            switch (reportType) {
                case "FACTURAS":
                    respuesta = generateBillingReport();
                    break;
                case "CIERRE_CAJA":
                    respuesta = generateCashClosingReport();
                    break;
                case "PROUDCTO_VENDIDO":
                    respuesta = generateSalesReport();
                    break;
                default:
                    showError(getLanguageString("lb.select.report.type"));
                    return;
            }

            if (respuesta.getEstado()) {
                pdf = (byte[]) respuesta.getResultado("Reporte");
                showMessage(getLanguageString("lb.report.generated.success"));
                btnPrint.setDisable(false);
                btnDownload.setDisable(false);
            } else {
                showError(respuesta.getMensaje());
            }

        } catch (Exception e) {
            showError(getLanguageString("lb.error.generating.report") + ": " + e.getMessage());
        }
    }

    @FXML
    private void onActionBtnPrint(ActionEvent event) {
        if (pdf == null || pdf.length == 0) {
            showError(getLanguageString("lb.no.report.to.print"));
            return;
        }

        try {
            File tempFile = File.createTempFile("report_", ".pdf");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(pdf);
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile);
                showMessage(getLanguageString("lb.print.success"));
            } else {
                showError(getLanguageString("lb.print.not.supported"));
            }

        } catch (IOException e) {
            showError(getLanguageString("lb.error.printing") + ": " + e.getMessage());
        }
    }

    @FXML
    private void onActionBtnDownload(ActionEvent event) {
        if (pdf == null || pdf.length == 0) {
            showError(getLanguageString("lb.no.report.to.download"));
            return;
        }

        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(getLanguageString("lb.select.download.folder"));
            File selectedDirectory = directoryChooser.showDialog(btnDownload.getScene().getWindow());

            if (selectedDirectory != null) {
                String fileName = generateFileName();
                File outputFile = new File(selectedDirectory, fileName);

                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(pdf);
                }

                showMessage(getLanguageString("lb.download.success") + ": " + outputFile.getAbsolutePath());
            }

        } catch (IOException e) {
            showError(getLanguageString("lb.error.downloading") + ": " + e.getMessage());
        }
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
        reportType = "FACTURAS";
        setActiveButton(btnBillingReport);

        btnPrint.setDisable(true);
        btnDownload.setDisable(true);

        dpStartDate.setValue(LocalDate.now().minusDays(7));
        dpEndDate.setValue(LocalDate.now());
        dpDate.setValue(LocalDate.now());
    }

    private void loadCashierOptions() {
        // TODO: Implementar carga de cajeros desde el servicio
        cmbCashier.getItems().clear();
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(getLanguageString("lb.information"));
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(getLanguageString("lb.error"));
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

    private Respuesta generateSalesReport() {
        if (dpStartDate.getValue() == null || dpEndDate.getValue() == null) {
            return new Respuesta(false, getLanguageString("lb.select.dates"), "Fechas inválidas");
        }

        Date fechaInicio = Date.from(dpStartDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaFin = Date.from(dpEndDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        return reporteService.reporteProductoVendido(fechaInicio, fechaFin);
    }

    private Respuesta generateCashClosingReport() {
        if (cmbCashier.getValue() == null) {
            return new Respuesta(false, getLanguageString("lb.select.cashier"), "Cajero no seleccionado");
        }

        return reporteService.reporteCierreCaja(cmbCashier.getValue());
    }

    private Respuesta generateBillingReport() {
        if (dpStartDate.getValue() == null || dpEndDate.getValue() == null) {
            return new Respuesta(false, getLanguageString("lb.select.dates"), "Fechas inválidas");
        }

        Date fechaInicio = Date.from(dpStartDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaFin = Date.from(dpEndDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        return reporteService.reporteFactura(fechaInicio, fechaFin);
    }

    private String generateFileName() {
        String baseName;
        switch (reportType) {
            case "PRODUCTOS_VENDIDOS":
                baseName = "reporte_ventas";
                break;
            case "CIERRE_CAJA":
                baseName = "reporte_cierre_caja";
                break;
            case "FACTURAS":
                baseName = "reporte_facturas";
                break;
            default:
                baseName = "reporte";
        }
        return baseName + "_" + System.currentTimeMillis() + ".pdf";
    }

}
