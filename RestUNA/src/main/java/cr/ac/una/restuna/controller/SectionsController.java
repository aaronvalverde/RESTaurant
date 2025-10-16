/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class SectionsController extends Controller implements Initializable {

    @FXML
    private MFXComboBox<?> cmbSectionChooser;
    @FXML
    private MFXButton btnEdit;
    @FXML
    private MFXButton btnDelete;
    @FXML
    private AnchorPane tablesRoot;
    @FXML
    private MFXButton btnAddTable;
    @FXML
    private MFXButton btnDeleteTable;
    @FXML
    private MFXButton btnExitEditMode;
    @FXML
    private HBox adminBox;
    @FXML
    private HBox editBox;

    private Boolean editMode = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setBoxes(editMode);
    }
    
    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnEdit(ActionEvent event) {
        editMode = true;
        setBoxes(editMode);
    }

    @FXML
    private void onActionBtnDelete(ActionEvent event) {
    }

    @FXML
    private void onActionBtnAddTable(ActionEvent event) {
    }

    @FXML
    private void onActionBtnDeleteTable(ActionEvent event) {
    }

    @FXML
    private void onActionBtnExitEditMode(ActionEvent event) {
        editMode = false;
        setBoxes(editMode);
    }

    private void setBoxes(Boolean editMode) {
        if (editMode) {
            adminBox.setVisible(false);
            adminBox.setManaged(false);
            editBox.setVisible(true);
            editBox.setManaged(true);
            return;
        }
        adminBox.setVisible(true);
        adminBox.setManaged(true);
        editBox.setVisible(false);
        editBox.setManaged(false);
    }
}
