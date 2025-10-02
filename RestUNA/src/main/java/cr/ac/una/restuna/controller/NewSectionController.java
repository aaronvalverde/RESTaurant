package cr.ac.una.restuna.controller;

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
    private MFXComboBox<?> cmbType;
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
    

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        }
    }

    @FXML
    private void onActionBtnChangeImage(ActionEvent event) {
    }

    @FXML
    private void onActionBtnDeleteImage(ActionEvent event) {
        if (imvTableGraphic != null) {
            imvTableGraphic.setImage(null);
        }
    }

    @FXML
    private void onActionBtnSaveChanges(ActionEvent event) {
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
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
