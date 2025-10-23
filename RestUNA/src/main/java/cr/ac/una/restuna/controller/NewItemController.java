package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.dto.ProductoDto;
import cr.ac.una.restuna.dto.SeccionDto;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbGroup.getItems().setAll("Bebidas Calientes", "Bebidas Frias", "Platos Fuertes", "Entradas", "Postres");
        initButtons();
        validations();
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnSaveChanges(ActionEvent event) {
        Respuesta respuesta = fields();
        if (respuesta.getEstado()) {
            saveProduct();
        } else {
            showMessage(respuesta);
        }
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        Respuesta respuesta = fields();
        if (respuesta.getEstado()) {
            addProduct();
        } else {
            showMessage(respuesta);
        }
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        closeWindow();
    }

    public void loadSection(SeccionDto section) {
        if (section != null) {

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
        } else {
            editMode = false;
            this.productEdit = null;
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

    private void validations() {

        txfName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.trim().isEmpty()) {
                txfName.setPromptText("Nombre vacío");
            }
        });
    }

    private void addProduct() {
        try {
            String name = txfName.getText().trim();
            String shortName = txfShortName.getText().trim();
            double price = Double.parseDouble(txfPrice.getText().trim());
            String description = txaDescription.getText().trim();
            String shortcut = cbShortcut.isSelected() ? "S" : "N";
            String status = cbStatus.isSelected() ? "A" : "I";

            // Obtener el controlador principal y agregar el producto
            ItemsMgmtController mainController = (ItemsMgmtController) FlowController.getInstance().getController(AppKeys.MENU_ITEMS_MGMT);
            mainController.addProduct(name, shortName, price, description, shortcut, status);

            Respuesta respuesta = new Respuesta(true, "Éxito", "Producto agregado correctamente");
            showMessage(respuesta);

            closeWindow();

        } catch (Exception e) {
            Respuesta error = new Respuesta(false, "No se agregó el producto", "Excepción: " + e.getMessage());
            showMessage(error);
            e.printStackTrace();
        }
    }

    private void saveProduct() {
        try {
            productEdit.setNombre(txfName.getText().trim());
            productEdit.setNombreCorto(txfShortName.getText().trim());
            productEdit.setPrecio(Double.parseDouble(txfPrice.getText().trim()));
            productEdit.setDescripcion(txaDescription.getText().trim());
            productEdit.setAccesoRapido(cbShortcut.isSelected() ? "S" : "N");
            productEdit.setEstado(cbStatus.isSelected() ? "A" : "I");

            // falta la logica para guardar en db 
            Respuesta respuesta = new Respuesta(true, "Éxito", "Producto guardado correctamente");
            showMessage(respuesta);

            closeWindow();
        } catch (Exception e) {
            Respuesta error = new Respuesta(false, "No se guardó el producto", "Excepción: " + e.getMessage());
            showMessage(error);
            e.printStackTrace();
        }
    }

    private Respuesta fields() {

        String errors = "";

        if (txfName.getText() == null || txfName.getText().trim().isEmpty()) {
            errors += "• Nombre vacío\n";
        }

        if (txfShortName.getText() == null || txfShortName.getText().trim().isEmpty()) {
            errors += "• Nombre corto vacío\n";
        }

        if (txfPrice.getText() == null || txfPrice.getText().trim().isEmpty()) {
            errors += "• Precio vacío\n";
        } else {
            try {
                Double.parseDouble(txfPrice.getText().trim());
            } catch (NumberFormatException e) {
                errors += "• Precio debe ser un número válido\n";
            }
        }

        if (!errors.isEmpty()) {
            return new Respuesta(false, "Error en la validación", "Complete los campos correctamente:", "errores", errors);
        }

        return new Respuesta(true, "Validación correcta", "Campos validados con éxito");
    }

    private void closeWindow() {

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
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
        txfShortName.clear();
        txfPrice.clear();
        txaDescription.clear();
        cmbGroup.getSelectionModel().clearSelection();
        cbShortcut.setSelected(false);
        cbStatus.setSelected(true);
    }

}
