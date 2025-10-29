package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class SettingsController extends Controller implements Initializable {

    @FXML
    private MFXScrollPane settingsRoot;
    @FXML
    private VBox settingsContainer;
    @FXML
    private MFXComboBox<String> cmbLanguage;
    @FXML
    private MFXComboBox<String> cmbCurrency;
    @FXML
    private MFXTextField txfRestaurantName;
    @FXML
    private MFXSpinner<?> spinnerIVA;
    @FXML
    private MFXSpinner<?> spinnerServiceTax;
    @FXML
    private MFXSpinner<?> spinnerCashierDiscount;
    @FXML
    private MFXTextField txfPhone;
    @FXML
    private MFXTextField txfSecondaryPhone;
    @FXML
    private MFXTextField txfEmail;
    @FXML
    private MFXTextField txfAddress;
    @FXML
    private MFXButton btnSave;
    @FXML
    private MFXButton btnCancel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadLanguageOptions();
        loadCurrencyOptions();

        if (cmbLanguage.getSelectedItem().equals("Español")|| cmbLanguage.getSelectedItem().equals("Spanish")) {
            checkLanguage("es");
        } else {
            checkLanguage("en");
        }
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnSave(ActionEvent event) {
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
    }

    private void loadLanguageOptions() {
        cmbLanguage.getItems().add(getLanguageString("lb.spanish"));
        cmbLanguage.getItems().add(getLanguageString("lb.english"));
    }

    private void loadCurrencyOptions() {
        cmbCurrency.getItems().add("CRC - Colón");
        cmbCurrency.getItems().add(getLanguageString("lb.dollar"));
        cmbCurrency.getItems().add("EUR - Euro");

    }

    private String getLanguageString(String key) {
        return FlowController.getInstance().getLanguage().getString(key);
    }

    private void checkLanguage(String key) {
        if (FlowController.getInstance().getLanguage().toString().equals(key)) {
            return;
        }
        Locale locale = new Locale(key);
        ResourceBundle bundle = ResourceBundle.getBundle("cr.ac.una.restuna.i18n.text", locale);
        FlowController.getInstance().setLanguage(bundle);
    }
}
