package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class NewSectionController extends Controller implements Initializable {

    @FXML
    private MFXTextField txfName;
    @FXML
    private MFXComboBox<String> cmbType;
    private MFXRadioButton rdbYes;
    private MFXRadioButton rdbNo;
    @FXML
    private ImageView imvTableGraphic;
    @FXML
    private MFXButton btnChooseImage;
    @FXML
    private MFXButton btnChangeImage;
    @FXML
    private MFXButton btnDeleteImage;
    @FXML
    private MFXButton btnSaveChanges;
    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private HBox imageRoot;
    @FXML
    private MFXCheckbox cbSalesTax;

    private boolean editMode = false;
    private Sections section;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbType.getItems().addAll("Salon", "VentaDirecta");
        initButtons();
    }

    @Override
    public void initialize() {

    }

    @FXML
    private void onActionBtnChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imvTableGraphic.setImage(image);
            imvTableGraphic.setUserData(file.getAbsolutePath());
        }
    }

    @FXML
    private void onActionBtnChangeImage(ActionEvent event) {
        onActionBtnChooseImage(event);
    }

    @FXML
    private void onActionBtnDeleteImage(ActionEvent event) {

        imvTableGraphic.setImage(null);
        imvTableGraphic.setUserData(null);
    }

    @FXML
    private void onActionBtnSaveChanges(ActionEvent event) {

        if (section != null) {

            section.getName().set(txfName.getText());
            section.getType().set(cmbType.getValue());
            section.getTax().set(cbSalesTax.isSelected());
            section.getImage().set(imvTableGraphic.getUserData() != null ? imvTableGraphic.getUserData().toString() : "");

        }

        getStage().close();
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {

        String name = txfName.getText();
        String type = cmbType.getValue();
        boolean tax = cbSalesTax.isSelected();
        String image = imvTableGraphic.getUserData() != null ? imvTableGraphic.getUserData().toString() : "";

        if (name.isEmpty() || type == null) {

            showMessage("Campos obligatorios");
            return;
        }

        SectionsMgmtController newSection = (SectionsMgmtController) FlowController.getInstance().getController(AppKeys.SECTIONS_MGMT);
        newSection.addSection(name, type, tax, image);
        getStage().close();
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        getStage().close();
    }

    private void clearFields() {
        txfName.clear();
        cmbType.getSelectionModel().clearSelection();
        rdbYes.setSelected(false);
        rdbNo.setSelected(false);
    }

    @FXML
    private void onDragOverToSetImage(DragEvent event) {
        imageRoot.setStyle("-fx-border-color: blue; -fx-border-style: dotted; -fx-border-width: 2px; -fx-border-radius: 10px;");
        if (event.getDragboard().hasFiles()) {
            for (File file : event.getDragboard().getFiles()) {
                if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
                    Image image = new Image(file.toURI().toString());
                    imvTableGraphic.setImage(image);
                    event.acceptTransferModes(TransferMode.COPY);
                    break;
                }
            }
        }
        event.consume();
    }

    @FXML
    private void onDragDroppedToSetImage(DragEvent event) {
        boolean success = false;
        if (event.getDragboard().hasFiles()) {
            for (File file : event.getDragboard().getFiles()) {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                    Image image = new Image(file.toURI().toString());
                    imvTableGraphic.setImage(image);
                    success = true;
                    break;
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    public void loadSection(Sections sectionLoad) {
        editMode = true;
        section = sectionLoad;
        txfName.setText(sectionLoad.getName().get());
        cmbType.getSelectionModel().selectItem(sectionLoad.getType().get());
        cbSalesTax.setSelected(sectionLoad.getTax().get());
        
        if (sectionLoad.getImage().get() != null && !sectionLoad.getImage().get().isEmpty()) {
            imvTableGraphic.setImage(new Image(new File(sectionLoad.getImage().get()).toURI().toString()));
            imvTableGraphic.setUserData(sectionLoad.getImage().get());
        }

        initButtons();
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

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
