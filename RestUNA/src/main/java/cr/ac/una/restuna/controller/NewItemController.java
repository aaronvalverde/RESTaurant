package cr.ac.una.restuna.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class NewItemController extends Controller implements Initializable {

    @FXML
    private MFXTextField txfName;
    @FXML
    private MFXTextField txfShortName;
    @FXML
    private MFXComboBox<?> cmbGroup;
    @FXML
    private MFXTextField txfPrice;
    @FXML
    private TextArea txaDescription;
    @FXML
    private MFXCheckbox cbShortcut;
    @FXML
    private MFXCheckbox cbStatus;
    @FXML
    private MFXButton btnSaveChanges;
    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXButton btnCancel;

    private boolean editMode = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initButtons();
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnSaveChanges(ActionEvent event) {
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
    }

    public void loadSection(/*SectionDto section*/) {
        editMode = true;
        //cargar todas los items correspondientes.
        //txfName.setText(section.getName());
        //cmbType.getSelectionModel().selectItem(section.getType());
        /*if (section.isTaxed()) {
            rdbYes.setSelected(true);
        } else {
            rdbNo.setSelected(true);
        }*/
        /*if (section.getImagePath() != null) {
            imvTableGraphic.setImage(new Image(new File(section.getImagePath()).toURI().toString()));
        }*/
    }

    private void initButtons() {
        if (editMode) {
            btnAdd.setVisible(false);
            btnAdd.setManaged(false);
        } else {
            btnSaveChanges.setVisible(false);
            btnSaveChanges.setManaged(false);
        }
    }

}
