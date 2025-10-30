package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.util.AppKeys;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 * 
 * Vista para gestionar MESAS dentro de una sección seleccionada
 *
 * @author aaron
 */
public class SectionsController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAddTable;
    @FXML
    private MFXButton btnDeleteTable; //habilitar este botón unicamente cuando se detecte un click en alguna de las mesas (estando en modo edición).
    @FXML
    private MFXButton btnExitEditMode;
    @FXML
    private MFXButton btnEditMode;
    @FXML
    private MFXScrollPane sectionsRoot;
    @FXML
    private VBox sectionsContainer; //agregar todas las secciones en este contenedor como botones para seleccionar
    @FXML
    private MFXButton btnExit;
    @FXML
    private AnchorPane sectionPane;
    @FXML
    private VBox editModeBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setNombreVista(AppKeys.SECTIONS);
        sectionsContainer.prefHeightProperty().bind(sectionsRoot.heightProperty());
        sectionsContainer.prefWidthProperty().bind(sectionsRoot.widthProperty());
        onEditMode(false);
    }

    @Override
    public void initialize() {
        // TODO: Cargar lista de secciones y permitir seleccionar una
        // TODO: Al seleccionar una sección, cargar sus mesas en sectionPane
    }

    @FXML
    private void onActionBtnAddTable(ActionEvent event) {
        MFXButton table = new MFXButton();
        //String path = " "; //cargar el path del diseño de la mesa para dicho salón.
        //table.setGraphic(new ImageView(new Image(path)));
        table.setText("1"); //se podría colocar el número de mesa, pero es algo opcional.
        table.setPrefHeight(80);
        table.setPrefWidth(80);

        table.setOnDragDetected(e -> {
            Dragboard db = table.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("table");
            db.setContent(content);
            e.consume();
        });

        sectionPane.setOnDragOver(e -> {
            if (e.getGestureSource() != sectionPane) { //esta línea verifica que no se este haciendo un drag de sí mismo.
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        sectionPane.setOnDragDropped(e -> {
            double x = e.getX();
            double y = e.getY();
            table.relocate(x, y); //settea la mesa en la nueva posición.
            e.setDropCompleted(true);
            e.consume();
        });

        sectionPane.getChildren().add(table);
    }

    @FXML
    private void onActionBtnDeleteTable(ActionEvent event) {
    }

    @FXML
    private void onActionBtnExitEditMode(ActionEvent event) {
        onEditMode(false);
    }

    @FXML
    private void onActionBtnEditMode(ActionEvent event) {
        onEditMode(true);
        //confirmación de guardar cambios (si los hay).
    }

    @FXML
    private void onActionBtnExit(ActionEvent event) {
    }

    private void onEditMode(Boolean editMode) {
        editModeBox.setVisible(editMode);
        editModeBox.setVisible(editMode);
        btnExitEditMode.setVisible(editMode);
        btnExitEditMode.setManaged(editMode);
        btnEditMode.setVisible(!editMode);
        btnEditMode.setManaged(!editMode);
    }

    private void loadSections() {
        //cargar todos los salones(secciones) en forma de mfxbuttons, setteando button.setText(...) con el nombre de dicha salón.
        //agregarlos a sectionsContainer.
    }
}
