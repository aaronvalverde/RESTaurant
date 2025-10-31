package cr.ac.una.restuna.controller;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.model.GrupoProductoDto;
import cr.ac.una.restuna.service.GrupoProductoService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeTableColumn;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import java.util.List;
import java.util.ArrayList;
import cr.ac.una.restuna.util.JsonParser;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class GroupsMgmtController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXFilterComboBox<String> cmbShortcut;
    @FXML
    private MFXFilterComboBox<String> cmbStatus;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<GrupoProductoDto, Void> tbcActions;
    @FXML
    private TreeTableColumn<GrupoProductoDto, String> tbcDescription;
    @FXML
    private TreeTableColumn<GrupoProductoDto, Long> tbcID;
    @FXML
    private TreeTableColumn<GrupoProductoDto, String> tbcName;
    @FXML
    private TreeTableColumn<GrupoProductoDto, String> tbcShortcut;
    @FXML
    private TreeTableColumn<GrupoProductoDto, String> tbcStatus;
    @FXML
    private JFXTreeTableView<GrupoProductoDto> tbvMenuGroups;
    @FXML
    private MFXTextField txfSearch;

    private final ObservableList<GrupoProductoDto> group = FXCollections.observableArrayList();
    private final GrupoProductoService grupoProductoService = new GrupoProductoService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvMenuGroups.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvMenuGroups.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbStatus.getItems().addAll("A", "I");
        cmbShortcut.getItems().addAll("S", "N");

        tbcID.setCellValueFactory(x -> x.getValue().getValue().idGrupoProductoProperty().asObject());
        tbcName.setCellValueFactory(x -> x.getValue().getValue().nombreProperty());
        tbcDescription.setCellValueFactory(x -> x.getValue().getValue().descripcionProperty());
        tbcShortcut.setCellValueFactory(x -> x.getValue().getValue().accesoRapidoProperty());
        tbcStatus.setCellValueFactory(x -> x.getValue().getValue().estadoProperty());

        TreeItem<GrupoProductoDto> root = new RecursiveTreeItem<>(group, RecursiveTreeObject::getChildren);
        tbvMenuGroups.setRoot(root);
        tbvMenuGroups.setShowRoot(false);

        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> groupFilter());
        cmbShortcut.valueProperty().addListener((obs, oldVal, newVal) -> groupFilter());
        cmbStatus.valueProperty().addListener((obs, oldVal, newVal) -> groupFilter());
        
        setActionsColumn();
        loadGroups(); // Cargar grupos desde el servidor
    }

    @Override
    public void initialize() {
    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        try {
            NewGroupController item = (NewGroupController) FlowController.getInstance().getController(AppKeys.NEW_MENU_GROUP);
            item.clear();
            FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_GROUP, new Stage(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditSection(/*DTO de la seccion*/) {
        NewGroupController controller = new NewGroupController();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_GROUP, new Stage(), false);
        controller.loadSection(/*DTO de la seccion*/);
    }
    
    /**
     * Carga los grupos desde el servidor
     */
    private void loadGroups() {
        System.out.println("DEBUG: Iniciando loadGroups()");
        Respuesta respuesta = grupoProductoService.getGrupoProductos();
        
        if (!respuesta.getEstado()) {
            System.err.println("Error al cargar grupos: " + respuesta.getMensaje());
            showMessage("Error al cargar grupos: " + respuesta.getMensaje());
            return;
        }
        
        String contenido = (String) respuesta.getResultado("GrupoProductos");
        System.out.println("DEBUG: Contenido recibido: " + (contenido != null ? contenido.substring(0, Math.min(100, contenido.length())) + "..." : "null"));
        
        if (contenido == null || contenido.trim().isEmpty()) {
            System.out.println("DEBUG: No hay grupos en la base de datos");
            return;
        }
        
        group.clear();
        
        // Extraer objetos JSON de nivel superior del array
        List<String> objetosGrupos = extraerObjetosDelArray(contenido);
        System.out.println("DEBUG: Se encontraron " + objetosGrupos.size() + " grupos");
        
        for (String objetoJson : objetosGrupos) {
            GrupoProductoDto grupo = parsearGrupoProducto(objetoJson);
            
            if (grupo != null) {
                System.out.println("DEBUG: Grupo parseado: " + grupo.getNombre());
                group.add(grupo);
            }
        }
        
        groupFilter();
        System.out.println("DEBUG: loadGroups() completado. Total grupos: " + group.size());
    }
    
    /**
     * Extrae objetos JSON de primer nivel de un array JSON
     */
    private List<String> extraerObjetosDelArray(String jsonArray) {
        List<String> objetos = new ArrayList<>();
        
        if (jsonArray == null || !jsonArray.trim().startsWith("[")) {
            return objetos;
        }
        
        int nivel = 0;
        int inicioObjeto = -1;
        
        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);
            
            if (c == '{') {
                if (nivel == 0) {
                    inicioObjeto = i;
                }
                nivel++;
            } else if (c == '}') {
                nivel--;
                if (nivel == 0 && inicioObjeto != -1) {
                    objetos.add(jsonArray.substring(inicioObjeto, i + 1));
                    inicioObjeto = -1;
                }
            }
        }
        
        return objetos;
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
            
            // Convertir Long a Integer para estos campos
            Long ordenVis = JsonParser.extraerValorLong(objetoJson, "ordenVisualizacion");
            if (ordenVis != null) {
                grupo.setOrdenVisualizacion(ordenVis.intValue());
            }
            
            Long cantVendida = JsonParser.extraerValorLong(objetoJson, "cantidadVendida");
            if (cantVendida != null) {
                grupo.setCantidadVendida(cantVendida.intValue());
            }
            
            grupo.setEstado(JsonParser.extraerValor(objetoJson, "estado"));
            
            return grupo;
        } catch (Exception e) {
            System.err.println("Error parseando grupo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void addGroup(String nameGroup, String description, String shorcut, String status) {
        System.out.println("DEBUG: Agregando grupo: " + nameGroup);
        
        // Verificar duplicados locales
        for (GrupoProductoDto grp : group) {
            if (grp.getNombre().equalsIgnoreCase(nameGroup)) {
                showMessage("El grupo ya existe: " + nameGroup);
                return;
            }
        }
        
        // Crear DTO para enviar al servidor
        GrupoProductoDto nuevoGrupo = new GrupoProductoDto();
        nuevoGrupo.setNombre(nameGroup);
        nuevoGrupo.setDescripcion(description);
        nuevoGrupo.setAccesoRapido(shorcut);
        nuevoGrupo.setEstado(status);
        
        // Determinar el siguiente orden de visualización
        Integer maxOrden = 0;
        for (GrupoProductoDto grp : group) {
            if (grp.getOrdenVisualizacion() != null && grp.getOrdenVisualizacion() > maxOrden) {
                maxOrden = grp.getOrdenVisualizacion();
            }
        }
        nuevoGrupo.setOrdenVisualizacion(maxOrden + 1);
        
        // Guardar en el servidor
        Respuesta respuesta = grupoProductoService.guardarGrupoProducto(nuevoGrupo);
        
        if (!respuesta.getEstado()) {
            System.err.println("Error guardando grupo: " + respuesta.getMensaje());
            showMessage("Error al guardar el grupo: " + respuesta.getMensaje());
            return;
        }
        
        System.out.println("Grupo guardado exitosamente en el servidor");
        
        // Recargar grupos desde el servidor
        loadGroups();
    }

    private void groupFilter() {

        String search = txfSearch.getText() == null ? "" : txfSearch.getText().toLowerCase();
        String shorcut = cmbShortcut.getValue();
        String status = cmbStatus.getValue();

        ObservableList<GrupoProductoDto> filter = group.filtered(x
                -> x.getNombre().toLowerCase().contains(search) || x.getDescripcion().toLowerCase().contains(search))
                .filtered(x -> shorcut == null || shorcut.isEmpty() || x.getAccesoRapido().equals(shorcut))
                .filtered(x -> status == null || status.isEmpty() || x.getEstado().equals(status));

        TreeItem<GrupoProductoDto> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
        tbvMenuGroups.setRoot(root);
        tbvMenuGroups.setShowRoot(false);
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void onEditGroup(GrupoProductoDto gpDto) {
        NewGroupController controller = (NewGroupController) FlowController.getInstance()
                .getController(AppKeys.NEW_MENU_GROUP);
        controller.setParentController(this);
        controller.loadSection(gpDto);
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_GROUP, new Stage(), false);
    }

    private void setActionsColumn() {
        tbcActions.setCellFactory(col -> new TreeTableCell<GrupoProductoDto, Void>() {
            MFXButton btnEdit = new MFXButton(" ");
            MFXButton btnDelete = new MFXButton();

            {
                btnEdit.setGraphic(new ImageView(new Image("../resources/icons/icons8-edit-50.png")));
                btnDelete.setGraphic(new ImageView(new Image("../resources/icons/icons8-delete-50.png")));

                btnEdit.setOnAction(e -> {
                    GrupoProductoDto gpDto = getTreeTableRow().getItem();
                    if (gpDto != null) {
                        onEditGroup(gpDto);
                    }
                });
                btnDelete.setOnAction(e -> {
                    GrupoProductoDto gpDto = getTreeTableRow().getItem();
                    if (gpDto != null) {
                        onDeleteGroup(gpDto);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, btnEdit, btnDelete);
                    setGraphic(hbox);
                }
            }
        });
    }
    
    private void onDeleteGroup(GrupoProductoDto grupo) {
        System.out.println("DEBUG: Eliminando grupo: " + grupo.getNombre());
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar grupo?");
        confirmacion.setContentText("¿Está seguro de eliminar el grupo '" + grupo.getNombre() + "'?");
        
        if (confirmacion.showAndWait().get() != javafx.scene.control.ButtonType.OK) {
            return;
        }
        
        Respuesta respuesta = grupoProductoService.eliminarGrupoProducto(grupo.getIdGrupoProducto());
        
        if (!respuesta.getEstado()) {
            System.err.println("Error eliminando grupo: " + respuesta.getMensaje());
            showMessage("Error al eliminar el grupo: " + respuesta.getMensaje());
            return;
        }
        
        System.out.println("Grupo eliminado exitosamente");
        showMessage("Grupo eliminado correctamente");
        
        // Recargar grupos
        loadGroups();
    }

    private String getLanguageString(String key) {
        return FlowController.getInstance().getLanguage().getString(key);
    }

}
