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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private VBox productsContainer;
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
    private MFXButton btnClearFilters;
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
    private List<ProductoDto> todosLosProductos = new ArrayList<>();
    private TableView<ProductoDto> tableProductos;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        quickBillingMode = false;
        initBoxes();

        currentOrder = new OrdenDto();
        groupProduct = new ArrayList<>();
        
        // Inicializar tabla de productos
        inicializarTablaProductos();
        
        // Configurar combos
        configurarCombos();
        
        // Cargar datos
        cargarSecciones();
        cargarGruposAccesoRapido(); // Para barra horizontal
        cargarTodosGrupos(); // Para combo box
        cargarTodosLosProductos();
        configurarBuscador();
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
    
    @FXML
    private void onActionBtnClearFilters(ActionEvent event) {
        txfSearch.clear();
        cmbGroups.clearSelection();
        mostrarTodosLosProductos();
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
        
        // DEBUG: Verificar modos activos
        System.out.println("DEBUG - billingMode: " + billingMode);
        System.out.println("DEBUG - sectionMode: " + sectionMode);
        System.out.println("DEBUG - quickBillingMode: " + quickBillingMode);
        System.out.println("DEBUG - currentSection: " + currentSection);
        System.out.println("DEBUG - currentMesa: " + currentMesa);
        
        // Establecer datos de la orden
        if (!billingMode && !sectionMode && !quickBillingMode) {
            // Modo normal - requiere sección y mesa
            if (currentSection == null) {
                mostrarAlerta("Sección requerida", "Debe seleccionar una sección");
                return;
            }
            if (currentMesa == null) {
                mostrarAlerta("Mesa requerida", "Debe seleccionar una mesa");
                return;
            }
            currentOrder.setIdMesa(currentMesa.getIdMesa());
            currentOrder.setIdSeccion(currentSection.getIdSeccion());
        } else if (sectionMode && currentMesa != null) {
            // Modo desde vista de sección
            currentOrder.setIdMesa(currentMesa.getIdMesa());
            currentOrder.setIdSeccion(currentMesa.getIdSeccion());
        } else if (billingMode && currentMesa != null && currentSection != null) {
            // Modo facturación - desde BillingView
            currentOrder.setIdMesa(currentMesa.getIdMesa());
            currentOrder.setIdSeccion(currentSection.getIdSeccion());
        } else if (quickBillingMode) {
            // Modo facturación rápida - no requiere mesa
            // Solo requiere sección
            if (currentSection == null) {
                mostrarAlerta("Sección requerida", "Debe seleccionar una sección");
                return;
            }
            currentOrder.setIdSeccion(currentSection.getIdSeccion());
        }
        
        // Establecer usuario (salonero actual)
        if (UserSession.getInstance().isAuthenticated()) {
            currentOrder.setIdSalonero(UserSession.getInstance().getCurrentUser().getIdUsuario());
        }
        
        // Fecha y hora actual
        currentOrder.setFechaHora(java.time.LocalDateTime.now());
        
        // Estado inicial
        currentOrder.setEstado("ABIERTA");
        
        // Calcular subtotal de la orden
        currentOrder.calcularSubtotal();
        
        // Guardar en background
        javafx.concurrent.Task<Respuesta> task = new javafx.concurrent.Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                // Guardar la orden
                Respuesta respuesta = ordenService.guardarOrden(currentOrder);
                
                // Si se guardó exitosamente, actualizar estado de la mesa a OCUPADA
                if (respuesta.getEstado() && currentOrder.getIdMesa() != null) {
                    MesaDto mesa = null;
                    
                    // Obtener la mesa actual
                    if (currentMesa != null && currentMesa.getIdMesa().equals(currentOrder.getIdMesa())) {
                        mesa = currentMesa;
                    } else {
                        // Buscar en el combo
                        for (MesaDto m : mesas) {
                            if (m.getIdMesa().equals(currentOrder.getIdMesa())) {
                                mesa = m;
                                break;
                            }
                        }
                    }
                    
                    // DEBUG: Ver coordenadas antes de guardar
                    if (mesa != null) {
                        System.out.println("DEBUG - Mesa antes de guardar: " + mesa.getNumeroMesa() + 
                                         " posX=" + mesa.getPosicionX() + " posY=" + mesa.getPosicionY());
                        mesa.setEstado("OCUPADA");
                        mesaService.guardarMesa(mesa);
                    }
                }
                
                return respuesta;
            }
            
            @Override
            protected void succeeded() {
                Respuesta respuesta = getValue();
                if (respuesta.getEstado()) {
                    mostrarAlerta("Éxito", "Orden guardada correctamente. La mesa ahora está ocupada.");
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
     * Obtener la moneda actual configurada
     */
    public String getMonedaActual() {
        if (parametrosMap != null && parametrosMap.containsKey("MONEDA")) {
            return parametrosMap.get("MONEDA").getValor();
        }
        return "CRC - Colón"; // Default
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
    
    /**
     * Inicializar tabla de productos
     */
    private void inicializarTablaProductos() {
        tableProductos = new TableView<>();
        tableProductos.setMaxHeight(Double.MAX_VALUE);
        
        // Columna Nombre
        TableColumn<ProductoDto, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(cellData -> {
            String nombre = cellData.getValue().getNombreCorto() != null && 
                          !cellData.getValue().getNombreCorto().isEmpty()
                ? cellData.getValue().getNombreCorto()
                : cellData.getValue().getNombre();
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });
        colNombre.setPrefWidth(250);
        
        // Columna Grupo
        TableColumn<ProductoDto, String> colGrupo = new TableColumn<>("Grupo");
        colGrupo.setCellValueFactory(cellData -> {
            Long idGrupo = cellData.getValue().getIdGrupoProducto();
            String nombreGrupo = "";
            if (idGrupo != null) {
                for (GrupoProductoDto grupo : grupos) {
                    if (grupo.getIdGrupoProducto().equals(idGrupo)) {
                        nombreGrupo = grupo.getNombre();
                        break;
                    }
                }
            }
            return new javafx.beans.property.SimpleStringProperty(nombreGrupo);
        });
        colGrupo.setPrefWidth(150);
        
        // Columna Precio
        TableColumn<ProductoDto, String> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(formatearPrecio(cellData.getValue().getPrecio()));
        });
        colPrecio.setPrefWidth(120);
        
        tableProductos.getColumns().addAll(colNombre, colGrupo, colPrecio);
        
        // Evento de doble clic para agregar producto
        tableProductos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ProductoDto selected = tableProductos.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    addProduct(selected);
                }
            }
        });
        
        productsContainer.getChildren().add(tableProductos);
        VBox.setVgrow(tableProductos, javafx.scene.layout.Priority.ALWAYS);
    }
    
    /**
     * Mostrar grupos en la barra horizontal
     */
    private void menuGroups() {
        groupsBox.getChildren().clear();

        for (GrupoProductoDto group : groupProduct) {
            MFXButton btnGroup = new MFXButton(group.getNombre());
            btnGroup.getStyleClass().add("group-button");
            btnGroup.setOnAction(x -> {
                // Seleccionar grupo en combo y filtrar
                cmbGroups.selectItem(group);
                mostrarProductosPorGrupo(group);
            });
            groupsBox.getChildren().add(btnGroup);
        }
    }

    private void addProduct(ProductoDto product) {
        // Validar que haya mesa seleccionada
        boolean mesaValida = false;
        
        if (quickBillingMode) {
            // En modo quick billing no requiere mesa
            mesaValida = true;
        } else if (currentMesa != null) {
            // Si hay mesa (de cualquier modo), es válido
            mesaValida = true;
        }
        
        if (!mesaValida) {
            mostrarAlerta("Mesa requerida", "Debe seleccionar una sección y mesa antes de agregar productos");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/OrderItem.fxml"));
            Node itemNode = loader.load();

            OrderItemController itemController = loader.getController();
            itemController.selectProduct(product);
            itemController.setParentController(this);
            
            // Guardar referencia del controlador en el nodo para actualizaciones futuras
            itemNode.setUserData(itemController);

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
        
        // Actualizar precios de todos los items en la vista
        actualizarPreciosItems();
    }
    
    /**
     * Actualizar la visualización de precios de todos los items de la orden
     */
    private void actualizarPreciosItems() {
        for (javafx.scene.Node node : orderContainer.getChildren()) {
            Object userData = node.getUserData();
            if (userData instanceof OrderItemController) {
                ((OrderItemController) userData).updatePriceDisplay();
            }
        }
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
                        // Actualizar tabla después de cargar parámetros
                        actualizarTablaProductos();
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
        
        cmbTable.selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentMesa = newVal;
            }
        });
        
        // Configurar combo de grupos
        cmbGroups.setItems(grupos);
        cmbGroups.setPromptText("Todos los grupos");
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
                mostrarProductosPorGrupo(newVal);
            } else {
                mostrarTodosLosProductos();
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
            
            // Parsear coordenadas de posición
            String posXStr = JsonParser.extraerValorNumerico(objetoJson, "posicionX");
            if (posXStr != null && !posXStr.isEmpty()) {
                mesa.setPosicionX(Double.parseDouble(posXStr));
            }
            
            String posYStr = JsonParser.extraerValorNumerico(objetoJson, "posicionY");
            if (posYStr != null && !posYStr.isEmpty()) {
                mesa.setPosicionY(Double.parseDouble(posYStr));
            }
            
            return mesa;
        } catch (Exception e) {
            System.err.println("Error parseando mesa: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cargar grupos de productos
     */
    /**
     * Cargar grupos de acceso rápido para la barra horizontal
     */
    private void cargarGruposAccesoRapido() {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = grupoProductoService.getGrupoProductosAccesoRapido();
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("GrupoProductos");
                        procesarGruposAccesoRapido(jsonArray);
                    }
                });
                
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Cargar todos los grupos para el combo box
     */
    private void cargarTodosGrupos() {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = grupoProductoService.getGrupoProductosActivos();
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("GrupoProductos");
                        procesarTodosGrupos(jsonArray);
                    }
                });
                
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Procesar JSON de grupos de acceso rápido (para barra horizontal)
     */
    private void procesarGruposAccesoRapido(String jsonArray) {
        groupProduct.clear();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }
        
        List<String> objetosGrupos = JsonParser.extraerObjetosDelArray(jsonArray);
        
        for (String objetoJson : objetosGrupos) {
            GrupoProductoDto grupo = parsearGrupo(objetoJson);
            if (grupo != null) {
                groupProduct.add(grupo);
            }
        }
        
        // Mostrar grupos en la barra horizontal
        menuGroups();
    }
    
    /**
     * Procesar JSON de todos los grupos (para combo box)
     */
    private void procesarTodosGrupos(String jsonArray) {
        grupos.clear();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }
        
        List<String> objetosGrupos = JsonParser.extraerObjetosDelArray(jsonArray);
        
        for (String objetoJson : objetosGrupos) {
            GrupoProductoDto grupo = parsearGrupo(objetoJson);
            if (grupo != null) {
                grupos.add(grupo);
            }
        }
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
            
            return grupo;
        } catch (Exception e) {
            System.err.println("Error parseando grupo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cargar todos los productos activos
     */
    private void cargarTodosLosProductos() {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = productoService.getProductosActivos();
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("Productos");
                        todosLosProductos = procesarProductos(jsonArray);
                        mostrarTodosLosProductos();
                    }
                });
                
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Configurar el buscador de productos
     */
    private void configurarBuscador() {
        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarProductos(newVal);
        });
    }
    
    /**
     * Filtrar productos por texto de búsqueda
     */
    private void filtrarProductos(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            // Si no hay filtro, mostrar según grupo seleccionado
            if (cmbGroups.getSelectedItem() != null) {
                mostrarProductosPorGrupo(cmbGroups.getSelectedItem());
            } else {
                mostrarTodosLosProductos();
            }
            return;
        }
        
        String filtroLower = filtro.toLowerCase();
        List<ProductoDto> productosFiltrados = todosLosProductos.stream()
            .filter(p -> p.getNombre().toLowerCase().contains(filtroLower) ||
                        (p.getNombreCorto() != null && p.getNombreCorto().toLowerCase().contains(filtroLower)))
            .collect(java.util.stream.Collectors.toList());
        
        mostrarProductos(productosFiltrados);
    }
    
    /**
     * Mostrar productos de un grupo específico
     */
    private void mostrarProductosPorGrupo(GrupoProductoDto grupo) {
        List<ProductoDto> productosDelGrupo = todosLosProductos.stream()
            .filter(p -> p.getIdGrupoProducto().equals(grupo.getIdGrupoProducto()))
            .collect(java.util.stream.Collectors.toList());
        
        // Aplicar filtro de búsqueda si existe
        String filtro = txfSearch.getText();
        if (filtro != null && !filtro.trim().isEmpty()) {
            String filtroLower = filtro.toLowerCase();
            productosDelGrupo = productosDelGrupo.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(filtroLower) ||
                            (p.getNombreCorto() != null && p.getNombreCorto().toLowerCase().contains(filtroLower)))
                .collect(java.util.stream.Collectors.toList());
        }
        
        mostrarProductos(productosDelGrupo);
    }
    
    /**
     * Mostrar todos los productos
     */
    private void mostrarTodosLosProductos() {
        String filtro = txfSearch.getText();
        if (filtro != null && !filtro.trim().isEmpty()) {
            filtrarProductos(filtro);
        } else {
            mostrarProductos(todosLosProductos);
        }
    }
    
    /**
     * Mostrar una lista de productos en la tabla
     */
    private void mostrarProductos(List<ProductoDto> productos) {
        javafx.collections.ObservableList<ProductoDto> items = 
            javafx.collections.FXCollections.observableArrayList(productos);
        tableProductos.setItems(items);
    }
    
    /**
     * Formatear precio con moneda actual y conversión usando BillingCalculator
     */
    public String formatearPrecio(Double precioCRC) {
        if (precioCRC == null) {
            precioCRC = 0.0;
        }
        
        String currency = "CRC - Colón";
        if (parametrosMap != null && parametrosMap.containsKey("MONEDA")) {
            currency = parametrosMap.get("MONEDA").getValor();
        }
        
        // Obtener tipo de cambio (multiplicador para convertir CRC a moneda destino)
        java.math.BigDecimal tipoCambio = obtenerTipoCambio(currency);
        
        // Convertir precio
        java.math.BigDecimal precioBase = java.math.BigDecimal.valueOf(precioCRC);
        java.math.BigDecimal precioConvertido = precioBase.multiply(tipoCambio)
            .setScale(2, java.math.RoundingMode.HALF_UP);
        
        // Formatear con símbolo de moneda
        return BillingCalculator.formatCurrency(precioConvertido, currency);
    }
    
    /**
     * Obtener tipo de cambio según la moneda (devuelve multiplicador)
     * Para CRC: 1 (sin conversión)
     * Para USD: 1/520 (convierte CRC a USD)
     * Para EUR: 1/570 (convierte CRC a EUR)
     */
    public java.math.BigDecimal obtenerTipoCambio(String moneda) {
        if (moneda == null || moneda.startsWith("CRC")) {
            return java.math.BigDecimal.ONE; // Sin conversión para CRC
        }
        
        if (moneda.startsWith("USD")) {
            ParametroDto usdParam = parametrosMap.get("TIPO_CAMBIO_USD");
            if (usdParam != null && usdParam.getValorComoDecimal() != null) {
                java.math.BigDecimal rate = java.math.BigDecimal.valueOf(usdParam.getValorComoDecimal());
                return java.math.BigDecimal.ONE.divide(rate, 6, java.math.RoundingMode.HALF_UP);
            }
            // Default: 1 USD = 520 CRC, entonces para convertir CRC a USD dividimos entre 520
            return java.math.BigDecimal.ONE.divide(java.math.BigDecimal.valueOf(520), 6, java.math.RoundingMode.HALF_UP);
        }
        
        if (moneda.startsWith("EUR")) {
            ParametroDto eurParam = parametrosMap.get("TIPO_CAMBIO_EUR");
            if (eurParam != null && eurParam.getValorComoDecimal() != null) {
                java.math.BigDecimal rate = java.math.BigDecimal.valueOf(eurParam.getValorComoDecimal());
                return java.math.BigDecimal.ONE.divide(rate, 6, java.math.RoundingMode.HALF_UP);
            }
            // Default: 1 EUR = 570 CRC, entonces para convertir CRC a EUR dividimos entre 570
            return java.math.BigDecimal.ONE.divide(java.math.BigDecimal.valueOf(570), 6, java.math.RoundingMode.HALF_UP);
        }
        
        return java.math.BigDecimal.ONE; // Default sin conversión
    }
    
    /**
     * Actualizar la tabla de productos para reflejar cambios de moneda
     */
    private void actualizarTablaProductos() {
        if (tableProductos != null) {
            tableProductos.refresh();
        }
    }
    
    /**
     * Procesar JSON de productos usando el constructor del DTO
     */
    private List<ProductoDto> procesarProductos(String jsonArray) {
        List<ProductoDto> productos = new ArrayList<>();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return productos;
        }
        
        List<String> objetosProductos = JsonParser.extraerObjetosDelArray(jsonArray);
        
        for (String objetoJson : objetosProductos) {
            try {
                ProductoDto producto = new ProductoDto(objetoJson);
                if (producto.getIdProducto() != null) {
                    productos.add(producto);
                }
            } catch (Exception e) {
                System.err.println("Error creando ProductoDto: " + e.getMessage());
            }
        }
        
        return productos;
    }
}
