package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.GrupoProductoDto;
import cr.ac.una.restuna.service.GrupoProductoService;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.TextFieldValidator;
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
    private GroupsMgmtController parentController;
    private final GrupoProductoService grupoProductoService = new GrupoProductoService();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        TextFieldValidator.addTextOnlyValidation(txfName);
        
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

    public void loadSection() {
        editMode = true;
        
        
        
        
 
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

            if (parentController == null) {
                throw new IllegalStateException("No hay un controlador padre asignado para refrescar los datos.");
            }
            parentController.addGroup(name, description, shortcut, status);

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
            
            Respuesta validacion = format();
            if (!validacion.getEstado()) {
                showMessage(validacion);
                return;
            }

            String name = txfName.getText().trim();
            String description = txaDescription.getText().trim();
            String shortcut = cbShortcut.isSelected() ? "S" : "N";
            String status = cbStatus.isSelected() ? "A" : "I";

            
            GrupoProductoDto dto = new GrupoProductoDto();
            
            
            if (editMode && groupId != null) {
                dto.setIdGrupoProducto(Long.parseLong(groupId));
            }
            
            dto.setNombre(name);
            dto.setDescripcion(description);
            dto.setAccesoRapido(shortcut);
            dto.setEstado(status);
            dto.setOrdenVisualizacion(1); 

            System.out.println("Guardando grupo: " + name + (editMode ? " (editando ID: " + groupId + ")" : " (nuevo)"));

            
            Respuesta respuesta = grupoProductoService.guardarGrupoProducto(dto);

            if (respuesta.getEstado()) {
                System.out.println("Grupo guardado exitosamente en el servidor");
                
                
                if (parentController != null) {
                    parentController.loadGroupsFromServer();
                }
                
                showMessage(new Respuesta(true, "Éxito", editMode ? "Grupo actualizado correctamente" : "Grupo creado correctamente"));
                closeWindow();
            } else {
                System.err.println("Error al guardar grupo: " + respuesta.getMensaje());
                showMessage(respuesta);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Respuesta error = new Respuesta(false, "Error", "No se pudo guardar el grupo: " + e.getMessage());
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
        
        
        this.editMode = false;
        this.groupId = null;
        initButtons();
    }

    public void setParentController(GroupsMgmtController parent) {
        this.parentController = parent;
    }

    public void loadSection(GrupoProductoDto gpDto) {
        editMode = true;

        
        this.groupId = gpDto.getIdGrupoProducto()!= null ? gpDto.getIdGrupoProducto().toString() : null;

        
        txfName.setText(gpDto.getNombre());
        txaDescription.setText(gpDto.getDescripcion());
        cbShortcut.setSelected("S".equals(gpDto.getAccesoRapido()));
        cbStatus.setSelected("A".equals(gpDto.getEstado()));

        initButtons();
    }
}
