package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.DetalleOrdenDto;
import cr.ac.una.restuna.model.GrupoProductoDto;
import cr.ac.una.restuna.model.MesaDto;
import cr.ac.una.restuna.model.OrdenDto;
import cr.ac.una.restuna.model.ParametroDto;
import cr.ac.una.restuna.model.ProductoDto;
import cr.ac.una.restuna.model.SeccionDto;
import cr.ac.una.restuna.service.ClienteService;
import cr.ac.una.restuna.service.GrupoProductoService;
import cr.ac.una.restuna.service.MesaService;
import cr.ac.una.restuna.service.OrdenService;
import cr.ac.una.restuna.service.ParametroService;
import cr.ac.una.restuna.service.ProductoService;
import cr.ac.una.restuna.service.SeccionService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.BillingCalculator;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.UserSession;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class OrderController extends Controller implements Initializable {

    @FXML
    private Label lbTable;
    @FXML
    private Label lbSection;
    @FXML
    private MFXButton btnClose;
    @FXML
    private MFXTextField txfSearch;
    @FXML
    private MFXScrollPane groupsRoot;
    @FXML
    private MFXScrollPane productsRoot;
    @FXML
    private GridPane itemsGrid;
    @FXML
    private MFXTextField txfClientName;
    @FXML
    private MFXScrollPane orderRoot;
    @FXML
    private VBox orderContainer;
    @FXML
    private Label lbVAT;
    @FXML
    private Label lbServiceTax;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private MFXButton btnSave;
    @FXML
    private MFXButton btnToBill;
    @FXML
    private MFXComboBox<SeccionDto> cmbSection;
    @FXML
    private MFXComboBox<MesaDto> cmbTable;
    @FXML
    private MFXComboBox<GrupoProductoDto> cmbGroups;
    @FXML
    private HBox groupsBox;
    @FXML
    private Label lbSubtotal;
    @FXML
    private Label lbTotal;
    @FXML
    private HBox billingModeBox;
    @FXML
    private MFXCheckbox cbQuickBilling;
    @FXML
    private VBox sectionModeBox;

    //al entrar desde facturación en vista principal.
    private Boolean billingMode = false;
    //al entrar desde vista de salón (drag&drop y click en mesa).
    private Boolean sectionMode = false;
    //settea el quick billing
    private Boolean quickBillingMode = false;

    private OrdenDto currentOrder;
    private List<GrupoProductoDto> groupProduct;
    private Double impIVA = 0.13;
    private Double impService = 0.10;
    private SeccionDto currentSection;
    private MesaDto currentMesa;
    private final ParametroService parametroService = new ParametroService();
    private final SeccionService seccionService = new SeccionService();
    private final MesaService mesaService = new MesaService();
    private final GrupoProductoService grupoProductoService = new GrupoProductoService();
    private final ProductoService productoService = new ProductoService();
    private final OrdenService ordenService = new OrdenService();
    private final ClienteService clienteService = new ClienteService();
    private java.util.Map<String, ParametroDto> parametrosMap = new java.util.HashMap<>();
    private javafx.collections.ObservableList<SeccionDto> secciones = javafx.collections.FXCollections.observableArrayList();
    private javafx.collections.ObservableList<MesaDto> mesas = javafx.collections.FXCollections.observableArrayList();
    private javafx.collections.ObservableList<GrupoProductoDto> grupos = javafx.collections.FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        quickBillingMode = false;
        initBoxes();

        currentOrder = new OrdenDto();
        groupProduct = new ArrayList<>();
        
        // Configurar combos
        configurarCombos();
        
        // Cargar datos
        cargarSecciones();
        cargarGrupos();
        cargarParametros();

        updateTotals();
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnClose(ActionEvent event) {
        FlowController.getInstance().goHome();
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        currentOrder = new OrdenDto();
        orderContainer.getChildren().clear();

    }

    @FXML
    private void onActionBtnSave(ActionEvent event) {
        guardarOrden();
    }
    
    /**
     * Guarda la orden en el servidor
     */
    private void guardarOrden() {
        // Validaciones
        if (currentOrder.getDetalles() == null || currentOrder.getDetalles().isEmpty()) {
            mostrarAlerta("No hay productos", "Debe agregar al menos un producto a la orden");
            return;
        }
        
        // Establecer datos de la orden
        if (!billingMode && !sectionMode && !quickBillingMode) {
            // Modo normal - requiere sección y mesa
            if (cmbSection.getSelectedItem() == null) {
                mostrarAlerta("Sección requerida", "Debe seleccionar una sección");
                return;
            }
            if (cmbTable.getSelectedItem() == null) {
                mostrarAlerta("Mesa requerida", "Debe seleccionar una mesa");
                return;
            }
            currentOrder.setIdMesa(cmbTable.getSelectedItem().getIdMesa());
            currentOrder.setIdSeccion(cmbSection.getSelectedItem().getIdSeccion());
        } else if (sectionMode && currentMesa != null) {
            // Modo desde vista de sección
            currentOrder.setIdMesa(currentMesa.getIdMesa());
            currentOrder.setIdSeccion(currentMesa.getIdSeccion());
        }
        
        // Establecer usuario (salonero actual)
        if (UserSession.getInstance().isAuthenticated()) {
            currentOrder.setIdSalonero(UserSession.getInstance().getCurrentUser().getIdUsuario());
        }
        
        // Fecha y hora actual
        currentOrder.setFechaHora(java.time.LocalDateTime.now());
        
        // Estado inicial
        currentOrder.setEstado("PENDIENTE");
        
        // Guardar en background
        javafx.concurrent.Task<Respuesta> task = new javafx.concurrent.Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                return ordenService.guardarOrden(currentOrder);
            }
            
            @Override
            protected void succeeded() {
                Respuesta respuesta = getValue();
                if (respuesta.getEstado()) {
                    mostrarAlerta("Éxito", "Orden guardada correctamente");
                    limpiarOrden();
                } else {
                    mostrarAlerta("Error", "Error al guardar la orden: " + respuesta.getMensaje());
                }
            }
            
            @Override
            protected void failed() {
                mostrarAlerta("Error", "Error al guardar la orden: " + getException().getMessage());
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Limpia la orden actual
     */
    private void limpiarOrden() {
        currentOrder = new OrdenDto();
        orderContainer.getChildren().clear();
        txfClientName.clear();
        updateTotals();
    }
    
    /**
     * Muestra un diálogo de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void onActionBtnToBill(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.BILLING);
    }

    private void initBoxes() {
        if (sectionMode) {
            billingModeBox.setVisible(false);
            billingModeBox.setManaged(false);
            cbQuickBilling.setVisible(false);
            cbQuickBilling.setManaged(false);
            return;
        }
        sectionModeBox.setVisible(false);
        sectionModeBox.setManaged(false);
    }

    public void onOrderMode() {
        sectionMode = true;
    }

    public void onBillingMode() {
        billingMode = true;
    }

    @FXML
    private void onActionCbQuickBilling(ActionEvent event) {
        quickBillingMode = !quickBillingMode;
        setQuickBillingMode(quickBillingMode);
    }

    private void setQuickBillingMode(Boolean isVisible) {
        billingModeBox.setVisible(!isVisible);
        billingModeBox.setManaged(!isVisible);
    }

    private void menuGroups() {
        groupsBox.getChildren().clear();

        for (GrupoProductoDto group : groupProduct) {

            MFXButton btnGroup = new MFXButton(group.getNombre());
            btnGroup.getStyleClass().add("group-button");
            btnGroup.setOnAction(x -> showProduct(group));
            groupsBox.getChildren().add(btnGroup);

        }

    }

    private void showProduct(GrupoProductoDto group) {

        itemsGrid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (ProductoDto product : group.getProduct()) {
            MFXButton btnProduct = new MFXButton(product.getNombreCorto() + "\n$" + product.getPrecio());
            btnProduct.getStyleClass().add("group-button");
            btnProduct.setOnAction(x -> addProduct(product));
            itemsGrid.add(btnProduct, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

    }

    private void addProduct(ProductoDto product) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/OrderItem.fxml"));
            Node itemNode = loader.load();

            OrderItemController itemController = loader.getController();
            itemController.selectProduct(product);
            itemController.setParentController(this);

            orderContainer.getChildren().add(itemNode);

            DetalleOrdenDto detalle = new DetalleOrdenDto();
            detalle.setIdProducto(product.getIdProducto());
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(product.getPrecio());
            detalle.setSubtotal(product.getPrecio());
            currentOrder.getDetalles().add(detalle);

            updateTotals();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteDetail(DetalleOrdenDto detail) {
        currentOrder.getDetalles().remove(detail);
        updateTotals();
    }

    public void updateTotals() {
        // Determinar si la sección cobra impuesto
        boolean sectionHasTax = currentSection != null && currentSection.cobraImpuesto();
        
        // Calcular usando BillingCalculator
        BillingCalculator.BillingResult result = BillingCalculator.calculateBilling(
            currentOrder.getDetalles(),
            sectionHasTax,
            parametrosMap
        );
        
        // Actualizar labels con formato de moneda
        lbSubtotal.setText(result.getFormattedSubtotal());
        lbVAT.setText(result.getFormattedIva());
        lbServiceTax.setText(result.getFormattedServiceTax());
        lbTotal.setText(result.getFormattedTotal());
    }
    
    /**
     * Cargar parámetros del sistema (impuestos y moneda)
     */
    private void cargarParametros() {
        if (!UserSession.getInstance().isAuthenticated()) {
            System.err.println("No hay usuario autenticado para cargar parámetros");
            return;
        }
        
        Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
        if (idUsuario == null) {
            System.err.println("No se pudo obtener el ID del usuario");
            return;
        }
        
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = parametroService.getParametrosPorUsuario(idUsuario);
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("Parametros");
                        procesarParametros(jsonArray);
                    } else {
                        System.err.println("Error cargando parámetros: " + respuesta.getMensaje());
                    }
                });
                
                return null;
            }
            
            @Override
            protected void failed() {
                javafx.application.Platform.runLater(() -> {
                    System.err.println("Error cargando parámetros: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Procesar JSON de parámetros
     */
    private void procesarParametros(String jsonArray) {
        parametrosMap.clear();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }
        
        // Extraer cada objeto del array JSON
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{[^{}]*\\}");
        java.util.regex.Matcher matcher = pattern.matcher(jsonArray);
        
        while (matcher.find()) {
            String objetoJson = matcher.group();
            ParametroDto parametro = parsearParametro(objetoJson);
            if (parametro != null && parametro.getClave() != null) {
                parametrosMap.put(parametro.getClave(), parametro);
            }
        }
        
        // Actualizar totales con nuevos parámetros
        updateTotals();
    }
    
    /**
     * Parsear un parámetro desde JSON
     */
    private ParametroDto parsearParametro(String objetoJson) {
        try {
            ParametroDto parametro = new ParametroDto();
            
            parametro.setIdParametro(JsonParser.extraerValorLong(objetoJson, "idParametro"));
            parametro.setIdUsuario(JsonParser.extraerValorLong(objetoJson, "idUsuario"));
            parametro.setClave(JsonParser.extraerValor(objetoJson, "clave"));
            parametro.setValor(JsonParser.extraerValor(objetoJson, "valor"));
            parametro.setDescripcion(JsonParser.extraerValor(objetoJson, "descripcion"));
            parametro.setTipoDato(JsonParser.extraerValor(objetoJson, "tipoDato"));
            
            return parametro;
        } catch (Exception e) {
            System.err.println("Error parseando parámetro: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Establecer la sección actual
     */
    public void setCurrentSection(SeccionDto section) {
        this.currentSection = section;
        updateTotals();
    }
    
    /**
     * Establecer la mesa actual
     */
    public void setCurrentMesa(MesaDto mesa) {
        this.currentMesa = mesa;
        if (mesa != null && lbTable != null) {
            lbTable.setText("Mesa: " + mesa.getNumeroMesa());
        }
    }
    
    /**
     * Configurar los combo boxes
     */
    private void configurarCombos() {
        // Configurar combo de secciones
        cmbSection.setItems(secciones);
        cmbSection.setConverter(new javafx.util.StringConverter<SeccionDto>() {
            @Override
            public String toString(SeccionDto seccion) {
                return seccion != null ? seccion.getNombre() : "";
            }
            
            @Override
            public SeccionDto fromString(String string) {
                return null;
            }
        });
        
        cmbSection.selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentSection = newVal;
                cargarMesasPorSeccion(newVal.getIdSeccion());
            }
        });
        
        // Configurar combo de mesas
        cmbTable.setItems(mesas);
        cmbTable.setConverter(new javafx.util.StringConverter<MesaDto>() {
            @Override
            public String toString(MesaDto mesa) {
                return mesa != null ? mesa.getNumeroMesa() : "";
            }
            
            @Override
            public MesaDto fromString(String string) {
                return null;
            }
        });
        
        // Configurar combo de grupos
        cmbGroups.setItems(grupos);
        cmbGroups.setConverter(new javafx.util.StringConverter<GrupoProductoDto>() {
            @Override
            public String toString(GrupoProductoDto grupo) {
                return grupo != null ? grupo.getNombre() : "";
            }
            
            @Override
            public GrupoProductoDto fromString(String string) {
                return null;
            }
        });
        
        cmbGroups.selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarProductosPorGrupo(newVal);
            }
        });
    }
    
    /**
     * Cargar todas las secciones
     */
    private void cargarSecciones() {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = seccionService.getSecciones();
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("Secciones");
                        procesarSecciones(jsonArray);
                    }
                });
                
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Procesar JSON de secciones
     */
    private void procesarSecciones(String jsonArray) {
        secciones.clear();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }
        
        List<String> objetosSecciones = JsonParser.extraerObjetosDelArray(jsonArray);
        
        for (String objetoJson : objetosSecciones) {
            SeccionDto seccion = parsearSeccion(objetoJson);
            if (seccion != null) {
                secciones.add(seccion);
            }
        }
    }
    
    /**
     * Parsear una sección desde JSON
     */
    private SeccionDto parsearSeccion(String objetoJson) {
        try {
            SeccionDto seccion = new SeccionDto();
            seccion.setIdSeccion(JsonParser.extraerValorLong(objetoJson, "idSeccion"));
            seccion.setNombre(JsonParser.extraerValor(objetoJson, "nombre"));
            seccion.setTipo(JsonParser.extraerValor(objetoJson, "tipo"));
            
            String cobraImpuesto = JsonParser.extraerValor(objetoJson, "cobraImpuesto");
            seccion.setCobraImpuesto(cobraImpuesto);
            
            return seccion;
        } catch (Exception e) {
            System.err.println("Error parseando sección: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cargar mesas de una sección
     */
    private void cargarMesasPorSeccion(Long idSeccion) {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = mesaService.getMesasPorSeccion(idSeccion);
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("Mesas");
                        procesarMesas(jsonArray);
                    }
                });
                
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Procesar JSON de mesas
     */
    private void procesarMesas(String jsonArray) {
        mesas.clear();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }
        
        List<String> objetosMesas = JsonParser.extraerObjetosDelArray(jsonArray);
        
        for (String objetoJson : objetosMesas) {
            MesaDto mesa = parsearMesa(objetoJson);
            if (mesa != null && "LIBRE".equals(mesa.getEstado())) {
                mesas.add(mesa);
            }
        }
    }
    
    /**
     * Parsear una mesa desde JSON
     */
    private MesaDto parsearMesa(String objetoJson) {
        try {
            MesaDto mesa = new MesaDto();
            mesa.setIdMesa(JsonParser.extraerValorLong(objetoJson, "idMesa"));
            mesa.setIdSeccion(JsonParser.extraerValorLong(objetoJson, "idSeccion"));
            mesa.setNumeroMesa(JsonParser.extraerValor(objetoJson, "numeroMesa"));
            mesa.setEstado(JsonParser.extraerValor(objetoJson, "estado"));
            
            return mesa;
        } catch (Exception e) {
            System.err.println("Error parseando mesa: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cargar grupos de productos
     */
    private void cargarGrupos() {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = grupoProductoService.getGrupoProductosAccesoRapido();
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("GrupoProductos");
                        procesarGrupos(jsonArray);
                    }
                });
                
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Procesar JSON de grupos
     */
    private void procesarGrupos(String jsonArray) {
        grupos.clear();
        groupProduct.clear();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }
        
        List<String> objetosGrupos = JsonParser.extraerObjetosDelArray(jsonArray);
        
        for (String objetoJson : objetosGrupos) {
            GrupoProductoDto grupo = parsearGrupo(objetoJson);
            if (grupo != null) {
                grupos.add(grupo);
                groupProduct.add(grupo);
            }
        }
        
        // Mostrar grupos en la barra horizontal
        menuGroups();
    }
    
    /**
     * Parsear un grupo desde JSON
     */
    private GrupoProductoDto parsearGrupo(String objetoJson) {
        try {
            GrupoProductoDto grupo = new GrupoProductoDto();
            grupo.setIdGrupoProducto(JsonParser.extraerValorLong(objetoJson, "idGrupoProducto"));
            grupo.setNombre(JsonParser.extraerValor(objetoJson, "nombre"));
            grupo.setDescripcion(JsonParser.extraerValor(objetoJson, "descripcion"));
            grupo.setAccesoRapido(JsonParser.extraerValor(objetoJson, "accesoRapido"));
            
            // Cargar productos del grupo
            cargarProductosPorGrupo(grupo);
            
            return grupo;
        } catch (Exception e) {
            System.err.println("Error parseando grupo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cargar productos de un grupo
     */
    private void cargarProductosPorGrupo(GrupoProductoDto grupo) {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = productoService.getProductosPorGrupoActivos(grupo.getIdGrupoProducto());
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("Productos");
                        List<ProductoDto> productos = procesarProductos(jsonArray);
                        grupo.setProductos(productos);
                        showProduct(grupo);
                    }
                });
                
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Procesar JSON de productos
     */
    private List<ProductoDto> procesarProductos(String jsonArray) {
        List<ProductoDto> productos = new ArrayList<>();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return productos;
        }
        
        List<String> objetosProductos = JsonParser.extraerObjetosDelArray(jsonArray);
        
        for (String objetoJson : objetosProductos) {
            ProductoDto producto = parsearProducto(objetoJson);
            if (producto != null) {
                productos.add(producto);
            }
        }
        
        return productos;
    }
    
    /**
     * Parsear un producto desde JSON
     */
    private ProductoDto parsearProducto(String objetoJson) {
        try {
            ProductoDto producto = new ProductoDto();
            producto.setIdProducto(JsonParser.extraerValorLong(objetoJson, "idProducto"));
            producto.setIdGrupoProducto(JsonParser.extraerValorLong(objetoJson, "idGrupoProducto"));
            producto.setNombre(JsonParser.extraerValor(objetoJson, "nombre"));
            producto.setNombreCorto(JsonParser.extraerValor(objetoJson, "nombreCorto"));
            
            String precioStr = JsonParser.extraerValorNumerico(objetoJson, "precio");
            if (precioStr != null) {
                producto.setPrecio(Double.parseDouble(precioStr));
            }
            
            return producto;
        } catch (Exception e) {
            System.err.println("Error parseando producto: " + e.getMessage());
            return null;
        }
    }
}
