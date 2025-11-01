package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.dto.ProductoDto;
import cr.ac.una.restuna.model.GrupoProductoDto;
import cr.ac.una.restuna.model.SeccionDto;
import cr.ac.una.restuna.service.GrupoProductoService;
import cr.ac.una.restuna.service.ProductoService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.JsonParser;
import java.math.BigDecimal;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private ItemsMgmtController parentController;
    private final GrupoProductoService grupoProductoService = new GrupoProductoService();
    private final ProductoService productoService = new ProductoService();
    private List<GrupoProductoDto> gruposDisponibles = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadGrupos(); // Cargar grupos desde el servidor
        initButtons();
        validations();
    }

    @Override
    public void initialize() {
    }
    
    /**
     * Carga los grupos activos desde el servidor
     */
    private void loadGrupos() {
        System.out.println("DEBUG: Cargando grupos para ComboBox");
        Respuesta respuesta = grupoProductoService.getGrupoProductosActivos();
        
        if (!respuesta.getEstado()) {
            System.err.println("Error cargando grupos: " + respuesta.getMensaje());
            showMessage(new Respuesta(false, "Error", "No se pudieron cargar los grupos: " + respuesta.getMensaje()));
            return;
        }
        
        String contenido = (String) respuesta.getResultado("GrupoProductos");
        
        if (contenido == null || contenido.trim().isEmpty()) {
            System.out.println("No hay grupos disponibles");
            return;
        }
        
        gruposDisponibles.clear();
        cmbGroup.getItems().clear();
        
        // Extraer objetos JSON
        List<String> objetosGrupos = JsonParser.extraerObjetosDelArray(contenido);
        
        for (String objetoJson : objetosGrupos) {
            GrupoProductoDto grupo = parsearGrupoProducto(objetoJson);
            if (grupo != null) {
                gruposDisponibles.add(grupo);
                cmbGroup.getItems().add(grupo.getNombre());
            }
        }
        
        System.out.println("DEBUG: Grupos cargados: " + gruposDisponibles.size());
    }
    
    /**
     * Parsea un objeto JSON string a GrupoProductoDto
     */
    private GrupoProductoDto parsearGrupoProducto(String objetoJson) {
        try {
            GrupoProductoDto grupo = new GrupoProductoDto();
            grupo.setIdGrupoProducto(JsonParser.extraerValorLong(objetoJson, "idGrupoProducto"));
            grupo.setNombre(JsonParser.extraerValor(objetoJson, "nombre"));
            grupo.setDescripcion(JsonParser.extraerValor(objetoJson, "descripcion"));
            grupo.setAccesoRapido(JsonParser.extraerValor(objetoJson, "accesoRapido"));
            grupo.setEstado(JsonParser.extraerValor(objetoJson, "estado"));
            
            Long ordenVis = JsonParser.extraerValorLong(objetoJson, "ordenVisualizacion");
            if (ordenVis != null) {
                grupo.setOrdenVisualizacion(ordenVis.intValue());
            }
            
            return grupo;
        } catch (Exception e) {
            System.err.println("Error parseando grupo: " + e.getMessage());
            return null;
        }
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
            // Validar que se seleccionó un grupo
            if (cmbGroup.getSelectionModel().getSelectedItem() == null) {
                showMessage(new Respuesta(false, "Error", "Debe seleccionar un grupo de producto"));
                return;
            }
            
            // Buscar el grupo seleccionado
            String nombreGrupoSeleccionado = cmbGroup.getSelectionModel().getSelectedItem();
            GrupoProductoDto grupoSeleccionado = null;
            for (GrupoProductoDto grupo : gruposDisponibles) {
                if (grupo.getNombre().equals(nombreGrupoSeleccionado)) {
                    grupoSeleccionado = grupo;
                    break;
                }
            }
            
            if (grupoSeleccionado == null) {
                showMessage(new Respuesta(false, "Error", "Grupo de producto no encontrado"));
                return;
            }
            
            // Crear DTO del producto
            ProductoDto nuevoProducto = new ProductoDto();
            nuevoProducto.setIdGrupoProducto(grupoSeleccionado.getIdGrupoProducto());
            nuevoProducto.setNombre(txfName.getText().trim());
            nuevoProducto.setNombreCorto(txfShortName.getText().trim());
            nuevoProducto.setPrecio(new BigDecimal(txfPrice.getText().trim()));
            nuevoProducto.setDescripcion(txaDescription.getText().trim());
            nuevoProducto.setAccesoRapido(cbShortcut.isSelected() ? "S" : "N");
            nuevoProducto.setEstado(cbStatus.isSelected() ? "A" : "I");
            
            // Guardar en el servidor
            Respuesta respuesta = productoService.guardarProducto(nuevoProducto);
            
            if (!respuesta.getEstado()) {
                showMessage(new Respuesta(false, "Error", "No se pudo guardar el producto: " + respuesta.getMensaje()));
                return;
            }
            
            showMessage(new Respuesta(true, "Éxito", "Producto agregado correctamente"));
            
            // Recargar tabla en el controlador padre si existe
            if (parentController != null) {
                parentController.loadProductsFromServer();
            }
            
            closeWindow();

        } catch (Exception e) {
            Respuesta error = new Respuesta(false, "No se agregó el producto", "Excepción: " + e.getMessage());
            showMessage(error);
            e.printStackTrace();
        }
    }

    private void saveProduct() {
        try {
            // Validar que se seleccionó un grupo
            if (cmbGroup.getSelectionModel().getSelectedItem() == null) {
                showMessage(new Respuesta(false, "Error", "Debe seleccionar un grupo de producto"));
                return;
            }
            
            // Buscar el grupo seleccionado
            String nombreGrupoSeleccionado = cmbGroup.getSelectionModel().getSelectedItem();
            GrupoProductoDto grupoSeleccionado = null;
            for (GrupoProductoDto grupo : gruposDisponibles) {
                if (grupo.getNombre().equals(nombreGrupoSeleccionado)) {
                    grupoSeleccionado = grupo;
                    break;
                }
            }
            
            if (grupoSeleccionado == null) {
                showMessage(new Respuesta(false, "Error", "Grupo de producto no encontrado"));
                return;
            }
            
            // Actualizar campos del producto
            productEdit.setIdGrupoProducto(grupoSeleccionado.getIdGrupoProducto());
            productEdit.setNombre(txfName.getText().trim());
            productEdit.setNombreCorto(txfShortName.getText().trim());
            productEdit.setPrecio(new BigDecimal(txfPrice.getText().trim()));
            productEdit.setDescripcion(txaDescription.getText().trim());
            productEdit.setAccesoRapido(cbShortcut.isSelected() ? "S" : "N");
            productEdit.setEstado(cbStatus.isSelected() ? "A" : "I");

            // Guardar en el servidor
            Respuesta respuesta = productoService.guardarProducto(productEdit);
            
            if (!respuesta.getEstado()) {
                showMessage(new Respuesta(false, "Error", "No se pudo guardar el producto: " + respuesta.getMensaje()));
                return;
            }
            
            showMessage(new Respuesta(true, "Éxito", "Producto actualizado correctamente"));
            
            // Recargar tabla en el controlador padre si existe
            if (parentController != null) {
                parentController.loadProductsFromServer();
            }

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
            errors += "• Nombre vacio\n";
        }

        if (txfShortName.getText() == null || txfShortName.getText().trim().isEmpty()) {
            errors += "• Nombre corto vacio\n";
        }
        
        if (cmbGroup.getSelectionModel().getSelectedItem() == null) {
            errors += "• Debe seleccionar un grupo\n";
        }

        if (txfPrice.getText() == null || txfPrice.getText().trim().isEmpty()) {
            errors += "• Precio vacio\n";
        } else {
            try {
                double precio = Double.parseDouble(txfPrice.getText().trim());
                if (precio <= 0) {
                    errors += "• Precio debe ser mayor a 0\n";
                }
            } catch (NumberFormatException e) {
                errors += "• Precio debe ser un número válido\n";
            }
        }

        if (!errors.isEmpty()) {
            return new Respuesta(false, "Error en la validacion", "Complete los campos correctamente:", "errores", errors);
        }

        return new Respuesta(true, "Validacion correcta", "Campos validados con exito");
    }

    private void closeWindow() {

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showMessage(Respuesta respuesta) {
        Alert.AlertType alertType = respuesta.getEstado() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(alertType);
        alert.setTitle(respuesta.getEstado() ? "Exito" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(respuesta.getMensaje());
        alert.showAndWait();
    }

    public void setParentController(ItemsMgmtController parent) {
        this.parentController = parent;
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
