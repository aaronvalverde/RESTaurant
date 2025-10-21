package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.dto.ProductoDto;
import cr.ac.una.restuna.dto.SeccionDto;
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
import javafx.stage.Stage;

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
    private MFXComboBox<String> cmbGroup;
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
    private ProductoDto productEdit;
    private SeccionDto sectionCurrent;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbGroup.getItems().setAll("Bebidas Calientes", "Bebidas Frias", "Platos Fuertes", "Entradas", "Postres");
        initButtons();
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnSaveChanges(ActionEvent event) {

        if (!fields()) {
            return;
        }

        productEdit.setNombre(txfName.getText());
        productEdit.setNombreCorto(txfShortName.getText());
        productEdit.setPrecio(Double.parseDouble(txfPrice.getText()));
        productEdit.setDescripcion(txaDescription.getText());
        productEdit.setAccesoRapido(cbShortcut.isSelected() ? "S" : "N");
        productEdit.setEstado(cbStatus.isSelected() ? "A" : "I");

        closeWindow();
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        if (!fields()) {
            return;
        }

        ProductoDto newProduct = new ProductoDto();
        newProduct.setNombre(txfName.getText());
        newProduct.setNombreCorto(txfShortName.getText());
        newProduct.setPrecio(Double.parseDouble(txfPrice.getText()));
        newProduct.setDescripcion(txaDescription.getText());
        newProduct.setAccesoRapido(cbShortcut.isSelected() ? "S" : "N");
        newProduct.setEstado(cbStatus.isSelected() ? "A" : "I");

        closeWindow();
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        closeWindow();
    }

    public void loadSection(SeccionDto section) {
        if (section != null) {
            this.sectionCurrent = section;
            editMode = true;

            //cargar todas los items correspondientes.
            txfName.setText(section.getNombre());
            cmbGroup.getSelectionModel().selectItem(section.getTipo());

            if ("S".equals(section.getCobraImpuesto())) {
                cbShortcut.setSelected(true);
            } else {
                cbShortcut.setSelected(true);
            }

            cbStatus.setSelected("A".equals(section.getEstado()));
            /*if (section.getImagePath() != null) {
            imvTableGraphic.setImage(new Image(new File(section.getImagePath()).toURI().toString()));
        }*/

            initButtons();
        } else {
            editMode = false;
            this.sectionCurrent = null;
            initButtons();
        }
    }

    public void loadProduct(ProductoDto product) {

        if (product != null) {
            editMode = true;
            this.productEdit = product;

            txfName.setText(product.getNombre());
            txfShortName.setText(product.getNombreCorto());
            txfPrice.setText(product.getPrecio() != null ? product.getPrecio().toString() : "");
            txaDescription.setText(product.getDescripcion());
            cbShortcut.setSelected("S".equals(product.getAccesoRapido()));
            cbStatus.setSelected("A".equals(product.getEstado()));
            initButtons();
        }
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

    private boolean fields() {

        return !(txfName.getText().isEmpty() || txfShortName.getText().isEmpty() || txfPrice.getText().isEmpty());
    }

    private void closeWindow() {

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
    
}
