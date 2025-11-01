package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.dto.ProductoDto;
import cr.ac.una.restuna.model.GrupoProductoDto;
import cr.ac.una.restuna.service.GrupoProductoService;
import cr.ac.una.restuna.service.ProductoService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Respuesta;
import javafx.concurrent.Task;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class ItemsMgmtController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXButton btnBack;
    @FXML
    private MFXFilterComboBox<String> cmbGroups;
    @FXML
    private MFXFilterComboBox<String> cmbStatus;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<ProductoDto, Void> tbcActions;
    @FXML
    private TreeTableColumn<ProductoDto, Long> tbcID;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcName;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcGroup;
    @FXML
    private TreeTableColumn<ProductoDto, Double> tbcPrice;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcShortcut;
    @FXML
    private TreeTableColumn<ProductoDto, String> tbcStatus;
    @FXML
    private JFXTreeTableView<ProductoDto> tbvMenuItems;
    @FXML
    private MFXTextField txfSearch;

    private final ObservableList<ProductoDto> product = FXCollections.observableArrayList();
    private final ObservableList<ProductoDto> filteredProducts = FXCollections.observableArrayList();
    private final ProductoService productoService = new ProductoService();
    private final GrupoProductoService grupoProductoService = new GrupoProductoService();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvMenuItems.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvMenuItems.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbStatus.getItems().setAll("Activo", "Inactivo");
        loadGruposAsync(); // Cargar grupos desde el servidor
        configurarFiltros();

        tbcID.setCellValueFactory(new TreeItemPropertyValueFactory<>("idProducto"));
        tbcName.setCellValueFactory(new TreeItemPropertyValueFactory<>("nombre"));
        tbcGroup.setCellValueFactory(new TreeItemPropertyValueFactory<>("nombreGrupo"));
        tbcPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("precio"));
        tbcShortcut.setCellValueFactory(new TreeItemPropertyValueFactory<>("nombreCorto"));
        tbcStatus.setCellValueFactory(new TreeItemPropertyValueFactory<>("estado"));

        TreeItem<ProductoDto> root = new RecursiveTreeItem<>(product, RecursiveTreeObject::getChildren);
        tbvMenuItems.setRoot(root);
        tbvMenuItems.setShowRoot(false);

        confEvent();
        setActionsColumn();
        loadProductsAsync(); // Cargar productos desde el servidor
    }

    private void confEvent() {

        tbvMenuItems.setOnMouseClicked((MouseEvent event) -> {

            if (event.getClickCount() == 2) {
                ProductoDto select = tbvMenuItems.getSelectionModel().getSelectedItem().getValue();
                if (select != null) {
                    onEditItem(select);
                }
            }
        });
    }

    @Override
    public void initialize() {
    }

    @FXML
    void onActionBtnBack(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MANAGEMENT);
    }

    @FXML
    void onActionBtnClearFilters(ActionEvent event) {
        txfSearch.clear();
        cmbGroups.clearSelection();
        cmbStatus.clearSelection();
    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        try {
            NewItemController item = (NewItemController) FlowController.getInstance().getController(AppKeys.NEW_MENU_ITEM);
            item.setParentController(this);
            item.clear();
            FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_ITEM, new Stage(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // este onAction no se esta usando por el momento 
    private void onEditItem(ProductoDto productoDto) {
        NewItemController controller = (NewItemController) FlowController.getInstance()
                .getController(AppKeys.NEW_MENU_ITEM);
        controller.setParentController(this);
        controller.loadProduct(productoDto);
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_MENU_ITEM, new Stage(), false);
    }

    private void onDeleteItem(ProductoDto productoDto) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar eliminación");
        confirmAlert.setHeaderText("¿Está seguro que desea eliminar este producto?");
        confirmAlert.setContentText("Producto: " + productoDto.getNombre());

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                Task<Respuesta> deleteTask = new Task<Respuesta>() {
                    @Override
                    protected Respuesta call() throws Exception {
                        return productoService.eliminarProducto(productoDto.getIdProducto());
                    }
                };

                deleteTask.setOnSucceeded(e -> {
                    Respuesta respuesta = deleteTask.getValue();

                    if (respuesta.getEstado()) {
                        showMessage("Producto eliminado correctamente");
                        loadProductsFromServer(); // Recargar la tabla
                    } else {
                        showMessage("Error al eliminar el producto: " + respuesta.getMensaje());
                    }
                });

                deleteTask.setOnFailed(e -> {
                    showMessage("Excepción al eliminar producto: " + deleteTask.getException().getMessage());
                    deleteTask.getException().printStackTrace();
                });

                new Thread(deleteTask).start();
            }
        });
    }

    public void addProduct(String name, String shortName, double price, String description, String shortcut, String status) {

        for (ProductoDto product : product) {
            if (product.getNombre().equalsIgnoreCase(name)) {
                showMessage("El producto ya existe: " + name);
                return;
            }
        }
        ProductoDto newProduct = new ProductoDto();
        newProduct.setIdProducto(System.currentTimeMillis());
        newProduct.setNombre(name);
        newProduct.setNombreCorto(shortName);
        newProduct.setPrecio(BigDecimal.valueOf(price));
        newProduct.setDescripcion(description);
        newProduct.setAccesoRapido(shortcut);
        newProduct.setEstado(status);

        product.add(newProduct);

    }

    private void setActionsColumn() {
        tbcActions.setCellFactory(col -> new TreeTableCell<ProductoDto, Void>() {
            private final MFXButton btnEdit = new MFXButton(" ");
            private final MFXButton btnDelete = new MFXButton();
            private final HBox actionButtons = new HBox(5);

            {
                try {
                    ImageView editIcon = new ImageView(new Image(
                        getClass().getResourceAsStream("/cr/ac/una/restuna/resources/icons/icons8-edit-50.png")
                    ));
                    editIcon.setFitWidth(20);
                    editIcon.setFitHeight(20);
                    btnEdit.setGraphic(editIcon);
                    
                    ImageView deleteIcon = new ImageView(new Image(
                        getClass().getResourceAsStream("/cr/ac/una/restuna/resources/icons/icons8-delete-50.png")
                    ));
                    deleteIcon.setFitWidth(20);
                    deleteIcon.setFitHeight(20);
                    btnDelete.setGraphic(deleteIcon);
                } catch (Exception e) {
                    System.err.println("Error cargando iconos: " + e.getMessage());
                }

                btnEdit.setOnAction(e -> {
                    ProductoDto productoDto = getTreeTableView().getTreeItem(getIndex()).getValue();
                    if (productoDto != null) {
                        onEditItem(productoDto);
                    }
                });
                
                btnDelete.setOnAction(e -> {
                    ProductoDto productoDto = getTreeTableView().getTreeItem(getIndex()).getValue();
                    if (productoDto != null) {
                        onDeleteItem(productoDto);
                    }
                });
                
                actionButtons.getChildren().addAll(btnEdit, btnDelete);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });
    }

    private String getLanguageString(String key) {
        return FlowController.getInstance().getLanguage().getString(key);
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    /**
     * Carga productos desde el servidor
     */
    public void loadProductsFromServer() {
        loadProductsAsync();
    }
    
    private void loadProductsAsync() {
        Task<Respuesta> loadTask = new Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                return productoService.getProductosActivos();
            }
        };
        
        loadTask.setOnSucceeded(e -> {
            Respuesta respuesta = loadTask.getValue();
            
            if (respuesta.getEstado()) {
                String contenido = (String) respuesta.getResultado("Productos");
                if (contenido != null && !contenido.trim().isEmpty()) {
                    procesarProductosDesdeJson(contenido);
                }
            } else {
                System.err.println("Error al cargar productos: " + respuesta.getMensaje());
                showMessage("Error al cargar productos: " + respuesta.getMensaje());
            }
        });
        
        loadTask.setOnFailed(e -> {
            System.err.println("Excepción cargando productos: " + loadTask.getException().getMessage());
            showMessage("Error al cargar productos: " + loadTask.getException().getMessage());
        });
        
        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private void procesarProductosDesdeJson(String contenido) {
        System.out.println("DEBUG: Iniciando procesarProductosDesdeJson()");
        System.out.println("DEBUG: Contenido recibido: " + (contenido != null ? contenido.substring(0, Math.min(100, contenido.length())) + "..." : "null"));
        
        product.clear();
        
        // Extraer objetos JSON del array
        List<String> objetosProductos = JsonParser.extraerObjetosDelArray(contenido);
        System.out.println("DEBUG: Se encontraron " + objetosProductos.size() + " productos");
        
        for (String objetoJson : objetosProductos) {
            ProductoDto producto = new ProductoDto(objetoJson);
            System.out.println("DEBUG: Producto parseado: " + producto.getNombre());
            product.add(producto);
        }
        
        // Refrescar la tabla con el workaround de JFXTreeTableView
        TreeItem<ProductoDto> root = new RecursiveTreeItem<>(product, RecursiveTreeObject::getChildren);
        tbvMenuItems.setRoot(null);
        tbvMenuItems.setRoot(root);
        
        System.out.println("DEBUG: Total productos: " + product.size());
    }
    
    /**
     * Carga grupos desde el servidor para el combo box de filtro
     */
    private void loadGruposAsync() {
        Task<Respuesta> loadTask = new Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                return grupoProductoService.getGrupoProductosActivos();
            }
        };
        
        loadTask.setOnSucceeded(e -> {
            Respuesta respuesta = loadTask.getValue();
            
            if (respuesta.getEstado()) {
                String contenido = (String) respuesta.getResultado("GrupoProductos");
                System.out.println("DEBUG: Contenido grupos recibido: " + contenido);
                
                if (contenido != null && !contenido.trim().isEmpty()) {
                    List<String> objetosGrupos = JsonParser.extraerObjetosDelArray(contenido);
                    System.out.println("DEBUG: Se encontraron " + objetosGrupos.size() + " grupos");
                    
                    cmbGroups.getItems().clear(); // Limpiar antes de agregar
                    
                    for (String objetoJson : objetosGrupos) {
                        GrupoProductoDto grupo = parsearGrupo(objetoJson);
                        if (grupo != null && grupo.getNombre() != null) {
                            System.out.println("DEBUG: Agregando grupo: " + grupo.getNombre());
                            cmbGroups.getItems().add(grupo.getNombre());
                        }
                    }
                    
                    System.out.println("DEBUG: Total grupos en combo: " + cmbGroups.getItems().size());
                }
            } else {
                System.err.println("Error al cargar grupos: " + respuesta.getMensaje());
            }
        });
        
        loadTask.setOnFailed(e -> {
            System.err.println("Excepción cargando grupos: " + loadTask.getException().getMessage());
            loadTask.getException().printStackTrace();
        });
        
        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    private GrupoProductoDto parsearGrupo(String objetoJson) {
        try {
            GrupoProductoDto grupo = new GrupoProductoDto();
            
            objetoJson = objetoJson.replaceAll("[{}]", "");
            String[] pairs = objetoJson.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    
                    switch (key) {
                        case "idGrupoProducto":
                            if (!value.equals("null")) {
                                grupo.setIdGrupoProducto(Long.parseLong(value));
                            }
                            break;
                        case "nombre":
                            if (!value.equals("null")) {
                                grupo.setNombre(value);
                            }
                            break;
                    }
                }
            }
            
            return grupo;
        } catch (Exception e) {
            System.err.println("Error parseando grupo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Configura los listeners para los filtros
     */
    private void configurarFiltros() {
        // Filtro de búsqueda por texto
        txfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltros();
        });
        
        // Filtro por grupo
        cmbGroups.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltros();
        });
        
        // Filtro por estado
        cmbStatus.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltros();
        });
    }
    
    /**
     * Aplica los filtros seleccionados a la lista de productos
     */
    private void aplicarFiltros() {
        filteredProducts.clear();
        
        String searchText = txfSearch.getText() != null ? txfSearch.getText().toLowerCase().trim() : "";
        String selectedGroup = cmbGroups.getSelectedItem();
        String selectedStatus = cmbStatus.getSelectedItem();
        
        for (ProductoDto producto : product) {
            boolean matches = true;
            
            // Filtro de búsqueda de texto (nombre o nombre corto)
            if (!searchText.isEmpty()) {
                boolean matchesName = producto.getNombre() != null && 
                                     producto.getNombre().toLowerCase().contains(searchText);
                boolean matchesShortName = producto.getNombreCorto() != null && 
                                           producto.getNombreCorto().toLowerCase().contains(searchText);
                matches = matchesName || matchesShortName;
            }
            
            // Filtro por grupo
            if (matches && selectedGroup != null && !selectedGroup.isEmpty()) {
                matches = producto.getNombreGrupo() != null && 
                         producto.getNombreGrupo().equals(selectedGroup);
            }
            
            // Filtro por estado
            if (matches && selectedStatus != null && !selectedStatus.isEmpty()) {
                // Convertir texto amigable a código
                String statusCode = selectedStatus.equals("Activo") ? "A" : "I";
                matches = producto.getEstado() != null && 
                         producto.getEstado().equals(statusCode);
            }
            
            if (matches) {
                filteredProducts.add(producto);
            }
        }
        
        // Actualizar la tabla con los productos filtrados
        TreeItem<ProductoDto> root = new RecursiveTreeItem<>(filteredProducts, RecursiveTreeObject::getChildren);
        tbvMenuItems.setRoot(null);
        tbvMenuItems.setRoot(root);
    }
}
