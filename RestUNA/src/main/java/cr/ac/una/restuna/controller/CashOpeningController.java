package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.CierreCajaDto;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class CashOpeningController extends Controller implements Initializable {

    @FXML
    private MFXButton btnKeypad;
    @FXML
    private MFXButton btnOk;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private MFXTextField txfInitialFund;
    @FXML
    private VBox keypadRoot;
    @FXML
    private VBox root;

    private Boolean onKeypadMode = false;
    public static CierreCajaDto activeOpening;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadKeypad();
        onKeypadMode = false;
        keypadRoot.setVisible(false);
        keypadRoot.setManaged(false);

        Platform.runLater(() -> {
            if (root.getScene() != null && root.getScene().getWindow() != null) {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.sizeToScene();
            }
        });
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnKeypad(ActionEvent event) {
        if (!onKeypadMode) {
            onKeypadMode = true;
            setKeypadVisibility(onKeypadMode);
            return;
        }
        onKeypadMode = false;
        setKeypadVisibility(onKeypadMode);
    }

    @FXML
    private void onActionBtnOk(ActionEvent event) {
        try {

            double initAmount = Double.parseDouble(txfInitialFund.getText().trim());

            if (initAmount <= 0) {

                showMessage("el monto debe mayor a 0");
                return;
            }

            activeOpening = new CierreCajaDto();
            activeOpening.setIdCierreCaja(System.currentTimeMillis());
            activeOpening.setIdUsuarioCajero(1L);
            activeOpening.setFechaApertura(new Date());
            activeOpening.setEfectivoInicial((long) (initAmount * 100));
            activeOpening.setEfectivoSistema((long) (initAmount * 100));
            activeOpening.setTarjetaSistema(0L);
            activeOpening.setEstado("A");

            showMessage("Caja abierta con ₡" + initAmount);
            closeWindow();

        } catch (NumberFormatException e) {

            showMessage("Ingrese un monto valido");
        }

    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        closeWindow();
    }

    private void loadKeypad() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/NumberKeypad.fxml"));
            AnchorPane keypadView = loader.load();
            NumberKeypadController numberKeypad = loader.getController();

            numberKeypad.setInputRoot(txfInitialFund);
            keypadRoot.getChildren().add(keypadView);

            keypadView.prefHeightProperty().bind(keypadRoot.heightProperty());
            keypadView.prefWidthProperty().bind(keypadRoot.widthProperty());

        } catch (IOException ex) {
            System.getLogger(BillingController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void setKeypadVisibility(Boolean isVisible) {
        keypadRoot.setVisible(isVisible);
        keypadRoot.setManaged(isVisible);

        if (root.getScene() != null && root.getScene().getWindow() != null) {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.sizeToScene();
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
