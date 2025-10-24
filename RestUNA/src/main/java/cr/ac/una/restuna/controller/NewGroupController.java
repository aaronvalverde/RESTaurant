package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class NewGroupController extends Controller implements Initializable {

    @FXML
    private MFXTextField txfName;
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
    private String groupId;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initButtons();
        validations();
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnSaveChanges(ActionEvent event) {
        Respuesta respuesta = format();
        if (respuesta.getEstado()) {
            saveGroup();
        } else {
            showMessage(respuesta);
        }
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        Respuesta respuesta = format();
        if (respuesta.getEstado()) {
            addGroup();
        } else {
            showMessage(respuesta);
        }
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        closeWindow();
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
        initButtons();
    }

    private void initButtons() {
        if (editMode) {
            btnAdd.setVisible(false);
            btnAdd.setManaged(false);
            btnSaveChanges.setVisible(true);
            btnSaveChanges.setManaged(true);
        } else {
            btnSaveChanges.setVisible(false);
            btnSaveChanges.setManaged(false);
            btnAdd.setVisible(true);
            btnAdd.setManaged(true);
        }
    }

    private void validations() {

        txfName.textProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null && newValue.trim().isEmpty()) {

                txfName.setPromptText("Nombre vacio");
            }
        });
    }

    public Respuesta format() {

        String errors = "";

        if (txfName.getText() == null || txfName.getText().trim().isEmpty()) {

            errors += "Nombre vacio";

        }

        if (txaDescription.getText() == null || txaDescription.getText().trim().isEmpty()) {

            errors += "Descripcion vacia";

        }

        if (!errors.isEmpty()) {

            return new Respuesta(false, "Error en la validacion", "Complete los campos", "errores", errors);
        }

        return new Respuesta(true, "Validacion correcta", "Realizada con exito");
    }

    private void addGroup() {

        try {
           
            String name = txfName.getText().trim();
            String description = txaDescription.getText().trim();
            String shortcut = cbShortcut.isSelected() ? "S" : "N";
            String status = cbStatus.isSelected() ? "A" : "I";

            GroupsMgmtController mainController = (GroupsMgmtController) FlowController.getInstance().getController(AppKeys.MENU_GROUPS_MGMT);
            mainController.addGroup(name, description, shortcut, status);

            Respuesta respuesta = new Respuesta(true, "Éxito", "Grupo guardado correctamente");
            showMessage(respuesta);

            closeWindow();

        } catch (Exception e) {

            Respuesta error = new Respuesta(false, "No se agrego el grupo", "Excepcion" + e.getMessage());
            showMessage(error);
        }
    }

    private void saveGroup() {

        try {
            String name = txfName.getText().trim();
            String description = txaDescription.getText().trim();
            String shortcut = cbShortcut.isSelected() ? "S" : "N";
            String status = cbStatus.isSelected() ? "A" : "I";

            //falta que se guarden en la db 
            Respuesta respuesta = new Respuesta(true, "Éxito", "Grupo guardado correctamente");
            showMessage(respuesta);
            closeWindow();
        } catch (Exception e) {
            Respuesta error = new Respuesta(false, "No se guardo el grupo", "Excepcion" + e.getMessage());
            showMessage(error);
        }
    }

    private void closeWindow() {

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void edit(boolean editMode) {

        this.editMode = editMode;
        initButtons();
    }

    public void loadGroup(String id, String name, String description, String shorcut, String status) {

        this.groupId = id;
        this.editMode = true;
        txfName.setText(name);
        txaDescription.setText(description);
        cbShortcut.setSelected("S".equals(shorcut));
        cbStatus.setSelected("A".equals(status));
        initButtons();
    }

    private void showMessage(Respuesta respuesta) {
        Alert.AlertType alertType = respuesta.getEstado() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(alertType);
        alert.setTitle(respuesta.getEstado() ? "Éxito" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(respuesta.getMensaje());
        alert.showAndWait();
    }

    public void clear() {
        txfName.clear();
        txaDescription.clear();
        cbShortcut.setSelected(false);
        cbStatus.setSelected(true);
        txfName.setStyle("");
        txaDescription.setStyle("");
    }
}
