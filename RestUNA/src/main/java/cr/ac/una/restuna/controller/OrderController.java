package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.ClienteDto;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


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

    
    private Boolean billingMode = false;
    
    private Boolean sectionMode = false;
    
    private Boolean quickBillingMode = false;
    
    private boolean isClearing = false;

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

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        quickBillingMode = false;
        initBoxes();

        currentOrder = new OrdenDto();
        groupProduct = new ArrayList<>();
        
        
        inicializarTablaProductos();
        
        
        configurarCombos();
        
        
        cargarSecciones();
        cargarGruposAccesoRapido(); 
        cargarTodosGrupos(); 
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
    
    
    private void guardarOrden() {
        
        if (currentOrder.getDetalles() == null || currentOrder.getDetalles().isEmpty()) {
            mostrarAlerta("No hay productos", "Debe agregar al menos un producto a la orden");
            return;
        }
        
        
        System.out.println("DEBUG - billingMode: " + billingMode);
        System.out.println("DEBUG - sectionMode: " + sectionMode);
        System.out.println("DEBUG - quickBillingMode: " + quickBillingMode);
        System.out.println("DEBUG - currentSection: " + currentSection);
        System.out.println("DEBUG - currentMesa: " + currentMesa);
        
        
        if (!billingMode && !sectionMode && !quickBillingMode) {
            
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
            
            currentOrder.setIdMesa(currentMesa.getIdMesa());
            currentOrder.setIdSeccion(currentMesa.getIdSeccion());
        } else if (billingMode && currentMesa != null && currentSection != null) {
            
            currentOrder.setIdMesa(currentMesa.getIdMesa());
            currentOrder.setIdSeccion(currentSection.getIdSeccion());
        } else if (quickBillingMode) {
            
            
            if (currentSection == null) {
                mostrarAlerta("Sección requerida", "Debe seleccionar una sección");
                return;
            }
            currentOrder.setIdSeccion(currentSection.getIdSeccion());
        }
        
        
        if (UserSession.getInstance().isAuthenticated()) {
            currentOrder.setIdSalonero(UserSession.getInstance().getCurrentUser().getIdUsuario());
        }
        
        
        currentOrder.setFechaHora(java.time.LocalDateTime.now());
        
        
        currentOrder.setEstado("ABIERTA");
        
        
        currentOrder.calcularSubtotal();
        
        
        String nombreCliente = txfClientName.getText();
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validación");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, ingrese el nombre del cliente antes de guardar la orden.");
            alert.showAndWait();
            return;
        }
        
        
        System.out.println("DEBUG - Guardando cliente con nombre: " + nombreCliente.trim());
        guardarClienteYOrden(nombreCliente.trim());
    }
    
    
    private void guardarClienteYOrden(String nombreCliente) {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                
                ClienteDto cliente = new ClienteDto();
                cliente.setNombre(nombreCliente);
                
                
                Respuesta respuestaCliente = clienteService.guardarCliente(cliente);
                
                if (respuestaCliente.getEstado()) {
                    
                    String clienteJson = (String) respuestaCliente.getResultado("Cliente");
                    Long idCliente = JsonParser.extraerValorLong(clienteJson, "idCliente");
                    
                    System.out.println("DEBUG - Cliente guardado con ID: " + idCliente);
                    
                    
                    currentOrder.setIdCliente(idCliente);
                } else {
                    System.err.println("Error guardando cliente: " + respuestaCliente.getMensaje());
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                
                guardarOrdenDirectamente();
            }
            
            @Override
            protected void failed() {
                System.err.println("Error en tarea de guardar cliente: " + getException().getMessage());
                
                guardarOrdenDirectamente();
            }
        };
        
        new Thread(task).start();
    }
    
    
    private void guardarOrdenDirectamente() {
        
        
        javafx.concurrent.Task<Respuesta> task = new javafx.concurrent.Task<Respuesta>() {
            @Override
            protected Respuesta call() throws Exception {
                
                Respuesta respuesta = ordenService.guardarOrden(currentOrder);
                
                
                if (respuesta.getEstado() && currentOrder.getIdMesa() != null) {
                    MesaDto mesa = null;
                    
                    
                    if (currentMesa != null && currentMesa.getIdMesa().equals(currentOrder.getIdMesa())) {
                        mesa = currentMesa;
                    } else {
                        
                        for (MesaDto m : mesas) {
                            if (m.getIdMesa().equals(currentOrder.getIdMesa())) {
                                mesa = m;
                                break;
                            }
                        }
                    }
                    
                    
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
                    
                    
                    cargarSecciones();
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
    
    
    private void limpiarOrden() {
        isClearing = true; 
        
        currentOrder = new OrdenDto();
        orderContainer.getChildren().clear();
        txfClientName.clear();
        
        
        cmbSection.clearSelection();
        cmbTable.clearSelection();
        currentSection = null;
        currentMesa = null;
        
        
        cmbGroups.clearSelection();
        mostrarTodosLosProductos();
        
        updateTotals();
        
        isClearing = false; 
    }
    
    
    private void cargarOrdenDeMesa(Long idMesa) {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("DEBUG - Cargando orden de mesa ID: " + idMesa);
                Respuesta respuesta = ordenService.getOrdenesPorMesa(idMesa);
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("Ordenes");
                        System.out.println("DEBUG - JSON de órdenes recibido: " + jsonArray);
                        
                        if (jsonArray != null && !jsonArray.trim().isEmpty() && !jsonArray.equals("[]")) {
                            
                            List<String> objetosOrdenes = JsonParser.extraerObjetosDelArray(jsonArray);
                            System.out.println("DEBUG - Número de órdenes encontradas: " + objetosOrdenes.size());
                            
                            
                            boolean ordenEncontrada = false;
                            for (String objetoJson : objetosOrdenes) {
                                String estado = JsonParser.extraerValor(objetoJson, "estado");
                                System.out.println("DEBUG - Orden con estado: " + estado);
                                if ("ABIERTA".equals(estado)) {
                                    
                                    parsearYCargarOrden(objetoJson);
                                    ordenEncontrada = true;
                                    break;
                                }
                            }
                            
                            if (!ordenEncontrada) {
                                System.out.println("DEBUG - No se encontró ninguna orden ABIERTA");
                                mostrarAlerta("Mesa Ocupada", 
                                    "Esta mesa está marcada como OCUPADA pero no tiene una orden activa.\n" +
                                    "Puede crear una nueva orden o cambiar el estado de la mesa.");
                            }
                        } else {
                            System.out.println("DEBUG - Array de órdenes vacío o nulo");
                            mostrarAlerta("Mesa Ocupada", 
                                "Esta mesa está marcada como OCUPADA pero no tiene órdenes registradas.\n" +
                                "Puede crear una nueva orden o cambiar el estado de la mesa a LIBRE.");
                        }
                    } else {
                        System.out.println("DEBUG - Error al obtener órdenes: " + respuesta.getMensaje());
                        mostrarAlerta("Error", "Error al cargar órdenes: " + respuesta.getMensaje());
                    }
                });
                
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    
    private void parsearYCargarOrden(String objetoJson) {
        try {
            System.out.println("DEBUG - Parseando orden: " + objetoJson);
            
            
            OrdenDto orden = new OrdenDto();
            orden.setIdOrden(JsonParser.extraerValorLong(objetoJson, "idOrden"));
            orden.setIdMesa(JsonParser.extraerValorLong(objetoJson, "idMesa"));
            orden.setIdSeccion(JsonParser.extraerValorLong(objetoJson, "idSeccion"));
            
            
            Long idSalonero = JsonParser.extraerValorLong(objetoJson, "idSalonero");
            if (idSalonero == null) {
                idSalonero = JsonParser.extraerValorLong(objetoJson, "idUsuarioSalonero");
            }
            orden.setIdSalonero(idSalonero);
            orden.setEstado(JsonParser.extraerValor(objetoJson, "estado"));
            
            
            Long idCliente = JsonParser.extraerValorLong(objetoJson, "idCliente");
            if (idCliente != null) {
                orden.setIdCliente(idCliente);
            }
            
            
            String nombreCliente = JsonParser.extraerValor(objetoJson, "nombreCliente");
            if (nombreCliente != null && !nombreCliente.trim().isEmpty()) {
                
                javafx.application.Platform.runLater(() -> {
                    txfClientName.setText(nombreCliente);
                });
            }
            
            
            String fechaStr = JsonParser.extraerValor(objetoJson, "fecha");
            if (fechaStr == null || fechaStr.isEmpty()) {
                fechaStr = JsonParser.extraerValor(objetoJson, "fechaCreacion");
            }
            if (fechaStr != null && !fechaStr.isEmpty()) {
                orden.setFechaHora(java.time.LocalDateTime.parse(fechaStr));
            }
            
            System.out.println("DEBUG - Orden parseada - ID: " + orden.getIdOrden() + ", Mesa: " + orden.getIdMesa());
            
            
            String detallesJson = JsonParser.extraerArray(objetoJson, "detalles");
            System.out.println("DEBUG - Detalles JSON: " + detallesJson);
            
            List<DetalleOrdenDto> detalles = new ArrayList<>();
            
            if (detallesJson != null && !detallesJson.equals("[]")) {
                List<String> objetosDetalles = JsonParser.extraerObjetosDelArray(detallesJson);
                System.out.println("DEBUG - Número de detalles: " + objetosDetalles.size());
                
                for (String detalleJson : objetosDetalles) {
                    System.out.println("DEBUG - Parseando detalle: " + detalleJson);
                    DetalleOrdenDto detalle = new DetalleOrdenDto();
                    detalle.setIdDetalleOrden(JsonParser.extraerValorLong(detalleJson, "idDetalleOrden"));
                    detalle.setIdProducto(JsonParser.extraerValorLong(detalleJson, "idProducto"));
                    
                    Integer cantidad = JsonParser.extraerValorInteger(detalleJson, "cantidad");
                    if (cantidad != null) {
                        detalle.setCantidad(cantidad);
                    }
                    
                    String precioStr = JsonParser.extraerValorNumerico(detalleJson, "precioUnitario");
                    if (precioStr != null) {
                        detalle.setPrecioUnitario(Double.parseDouble(precioStr));
                    }
                    
                    String subtotalStr = JsonParser.extraerValorNumerico(detalleJson, "subtotal");
                    if (subtotalStr != null) {
                        detalle.setSubtotal(Double.parseDouble(subtotalStr));
                    }
                    
                    detalle.setObservaciones(JsonParser.extraerValor(detalleJson, "observaciones"));
                    
                    System.out.println("DEBUG - Detalle parseado - Producto ID: " + detalle.getIdProducto() + 
                                     ", Cantidad: " + detalle.getCantidad() + ", Precio: " + detalle.getPrecioUnitario());
                    
                    detalles.add(detalle);
                }
                
                
                
                
            }
            
            
            
            currentOrder.setIdOrden(orden.getIdOrden());
            currentOrder.setIdMesa(orden.getIdMesa());
            currentOrder.setIdSeccion(orden.getIdSeccion());
            currentOrder.setIdCliente(orden.getIdCliente());
            currentOrder.setIdSalonero(orden.getIdSalonero());
            currentOrder.setEstado(orden.getEstado());
            currentOrder.setFechaHora(orden.getFechaHora());
            
            System.out.println("DEBUG - Orden establecida como actual, cargando " + detalles.size() + " detalles en interfaz");
            
            
            currentOrder.getDetalles().clear();
            
            
            orderContainer.getChildren().clear();
            System.out.println("DEBUG - OrderContainer limpiado, cargando productos...");
            
            
            for (DetalleOrdenDto detalle : detalles) {
                
                cargarProductoYAgregarDetalle(detalle);
            }
            
            
            
            
            
            mostrarAlerta("Orden Cargada", "Se ha cargado la orden existente de esta mesa. Puede modificarla o ir a facturar.");
            
        } catch (Exception e) {
            System.err.println("Error parseando orden: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    private void cargarProductoYAgregarDetalle(DetalleOrdenDto detalle) {
        System.out.println("DEBUG - Cargando producto ID: " + detalle.getIdProducto() + " con cantidad: " + detalle.getCantidad());
        
        
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("DEBUG - Solicitando producto al servicio...");
                
                Respuesta respuesta = productoService.getProducto(detalle.getIdProducto());
                
                javafx.application.Platform.runLater(() -> {
                    System.out.println("DEBUG - Respuesta del servicio - Estado: " + respuesta.getEstado());
                    if (respuesta.getEstado()) {
                        
                        Object resultado = respuesta.getResultado("Producto");
                        ProductoDto producto = null;
                        
                        if (resultado instanceof ProductoDto) {
                            
                            producto = (ProductoDto) resultado;
                            System.out.println("DEBUG - Producto recibido directamente: " + producto.getNombre());
                        } else if (resultado instanceof String) {
                            
                            String productoJson = (String) resultado;
                            System.out.println("DEBUG - Producto JSON recibido: " + productoJson);
                            producto = parsearProducto(productoJson);
                        }
                        
                        if (producto != null) {
                            System.out.println("DEBUG - Producto listo: " + producto.getNombre());
                            System.out.println("DEBUG - Cantidad del detalle: " + detalle.getCantidad());
                            System.out.println("DEBUG - Precio unitario: " + detalle.getPrecioUnitario());
                            System.out.println("DEBUG - Subtotal del detalle: " + detalle.getSubtotal());
                            
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/OrderItem.fxml"));
                                Node itemNode = loader.load();

                                OrderItemController itemController = loader.getController();
                                
                                itemController.loadExistingDetail(producto, detalle);
                                itemController.setParentController(OrderController.this);
                                
                                
                                currentOrder.getDetalles().add(detalle);
                                
                                
                                itemNode.setUserData(itemController);
                                orderContainer.getChildren().add(itemNode);
                                
                                
                                updateTotals();
                                
                                System.out.println("DEBUG - Producto agregado a interfaz exitosamente");
                            } catch (Exception e) {
                                System.err.println("DEBUG - Error cargando OrderItem: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            System.err.println("DEBUG - Error: no se pudo obtener el producto");
                        }
                    } else {
                        System.err.println("DEBUG - Error en respuesta del servicio: " + respuesta.getMensaje());
                    }
                });
                
                return null;
            }
        };
        
        new Thread(task).start();
    }
    
    
    private ProductoDto parsearProducto(String objetoJson) {
        try {
            ProductoDto producto = new ProductoDto();
            producto.setIdProducto(JsonParser.extraerValorLong(objetoJson, "idProducto"));
            producto.setNombre(JsonParser.extraerValor(objetoJson, "nombre"));
            producto.setDescripcion(JsonParser.extraerValor(objetoJson, "descripcion"));
            
            String precioStr = JsonParser.extraerValorNumerico(objetoJson, "precio");
            if (precioStr != null) {
                producto.setPrecio(Double.parseDouble(precioStr));
            }
            
            producto.setEstado(JsonParser.extraerValor(objetoJson, "estado"));
            
            return producto;
        } catch (Exception e) {
            System.err.println("Error parseando producto: " + e.getMessage());
            return null;
        }
    }
    
    
    public String getMonedaActual() {
        if (parametrosMap != null && parametrosMap.containsKey("MONEDA")) {
            return parametrosMap.get("MONEDA").getValor();
        }
        return "CRC - Colón"; 
    }
    
    
    private void mostrarAlerta(String titulo, String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void onActionBtnToBill(ActionEvent event) {
        if (currentMesa == null) {
            mostrarAlerta("Error", "No hay mesa seleccionada para facturar.");
            return;
        }
        
        
        BillingController billingController = (BillingController) FlowController.getInstance()
            .getController(AppKeys.BILLING);
        
        if (billingController != null) {
            billingController.cargarMesa(currentMesa);
        }
        
        
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
    
    
    private void inicializarTablaProductos() {
        tableProductos = new TableView<>();
        tableProductos.setMaxHeight(Double.MAX_VALUE);
        
        
        TableColumn<ProductoDto, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(cellData -> {
            String nombre = cellData.getValue().getNombreCorto() != null && 
                          !cellData.getValue().getNombreCorto().isEmpty()
                ? cellData.getValue().getNombreCorto()
                : cellData.getValue().getNombre();
            return new javafx.beans.property.SimpleStringProperty(nombre);
        });
        colNombre.setPrefWidth(250);
        
        
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
        
        
        TableColumn<ProductoDto, String> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(formatearPrecio(cellData.getValue().getPrecio()));
        });
        colPrecio.setPrefWidth(120);
        
        tableProductos.getColumns().addAll(colNombre, colGrupo, colPrecio);
        
        
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
    
    
    private void menuGroups() {
        groupsBox.getChildren().clear();

        for (GrupoProductoDto group : groupProduct) {
            MFXButton btnGroup = new MFXButton(group.getNombre());
            btnGroup.getStyleClass().add("group-button");
            btnGroup.setOnAction(x -> {
                
                GrupoProductoDto grupoEnCombo = grupos.stream()
                    .filter(g -> g.getIdGrupoProducto().equals(group.getIdGrupoProducto()))
                    .findFirst()
                    .orElse(null);
                
                if (grupoEnCombo != null) {
                    
                    cmbGroups.selectItem(grupoEnCombo);
                    mostrarProductosPorGrupo(grupoEnCombo);
                } else {
                    
                    mostrarProductosPorGrupo(group);
                }
            });
            groupsBox.getChildren().add(btnGroup);
        }
    }

    private void addProduct(ProductoDto product) {
        
        boolean mesaValida = false;
        
        if (quickBillingMode) {
            
            mesaValida = true;
        } else if (currentMesa != null) {
            
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
            
            
            DetalleOrdenDto detalle = itemController.getDetail();
            currentOrder.getDetalles().add(detalle);
            
            
            itemNode.setUserData(itemController);

            orderContainer.getChildren().add(itemNode);

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
        
        boolean sectionHasTax = currentSection != null && currentSection.cobraImpuesto();
        
        
        BillingCalculator.BillingResult result = BillingCalculator.calculateBilling(
            currentOrder.getDetalles(),
            sectionHasTax,
            parametrosMap
        );
        
        
        lbSubtotal.setText(result.getFormattedSubtotal());
        lbVAT.setText(result.getFormattedIva());
        lbServiceTax.setText(result.getFormattedServiceTax());
        lbTotal.setText(result.getFormattedTotal());
        
        
        actualizarPreciosItems();
    }
    
    
    private void actualizarPreciosItems() {
        for (javafx.scene.Node node : orderContainer.getChildren()) {
            Object userData = node.getUserData();
            if (userData instanceof OrderItemController) {
                ((OrderItemController) userData).updatePriceDisplay();
            }
        }
    }
    
    
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
    
    
    private void procesarParametros(String jsonArray) {
        parametrosMap.clear();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }
        
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{[^{}]*\\}");
        java.util.regex.Matcher matcher = pattern.matcher(jsonArray);
        
        while (matcher.find()) {
            String objetoJson = matcher.group();
            ParametroDto parametro = parsearParametro(objetoJson);
            if (parametro != null && parametro.getClave() != null) {
                parametrosMap.put(parametro.getClave(), parametro);
            }
        }
        
        
        updateTotals();
    }
    
    
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
    
    
    public void setCurrentSection(SeccionDto section) {
        this.currentSection = section;
        updateTotals();
    }
    
    
    public void setCurrentMesa(MesaDto mesa) {
        this.currentMesa = mesa;
        if (mesa != null && lbTable != null) {
            lbTable.setText("Mesa: " + mesa.getNumeroMesa());
        }
    }
    
    
    private void configurarCombos() {
        
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
        
        
        cmbTable.setItems(mesas);
        cmbTable.setConverter(new javafx.util.StringConverter<MesaDto>() {
            @Override
            public String toString(MesaDto mesa) {
                if (mesa == null) return "";
                
                String estado = mesa.getEstado();
                if ("OCUPADA".equals(estado)) {
                    return mesa.getNumeroMesa() + " (OCUPADA)";
                }
                return mesa.getNumeroMesa();
            }
            
            @Override
            public MesaDto fromString(String string) {
                return null;
            }
        });
        
        cmbTable.selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            
            if (isClearing) {
                return;
            }
            
            if (newVal != null) {
                currentMesa = newVal;
                
                if ("OCUPADA".equals(newVal.getEstado())) {
                    cargarOrdenDeMesa(newVal.getIdMesa());
                } else {
                    
                    currentOrder = new OrdenDto();
                    orderContainer.getChildren().clear();
                    txfClientName.clear();
                    updateTotals();
                }
            }
        });
        
        
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
    
    
    private void procesarMesas(String jsonArray) {
        mesas.clear();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }
        
        List<String> objetosMesas = JsonParser.extraerObjetosDelArray(jsonArray);
        
        for (String objetoJson : objetosMesas) {
            MesaDto mesa = parsearMesa(objetoJson);
            
            if (mesa != null && ("LIBRE".equals(mesa.getEstado()) || "OCUPADA".equals(mesa.getEstado()))) {
                mesas.add(mesa);
            }
        }
    }
    
    
    private MesaDto parsearMesa(String objetoJson) {
        try {
            MesaDto mesa = new MesaDto();
            mesa.setIdMesa(JsonParser.extraerValorLong(objetoJson, "idMesa"));
            mesa.setIdSeccion(JsonParser.extraerValorLong(objetoJson, "idSeccion"));
            mesa.setNumeroMesa(JsonParser.extraerValor(objetoJson, "numeroMesa"));
            mesa.setEstado(JsonParser.extraerValor(objetoJson, "estado"));
            
            
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
        
        
        menuGroups();
    }
    
    
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
    
    
    private void configurarBuscador() {
        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarProductos(newVal);
        });
    }
    
    
    private void filtrarProductos(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            
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
    
    
    private void mostrarProductosPorGrupo(GrupoProductoDto grupo) {
        List<ProductoDto> productosDelGrupo = todosLosProductos.stream()
            .filter(p -> p.getIdGrupoProducto().equals(grupo.getIdGrupoProducto()))
            .collect(java.util.stream.Collectors.toList());
        
        
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
    
    
    private void mostrarTodosLosProductos() {
        String filtro = txfSearch.getText();
        if (filtro != null && !filtro.trim().isEmpty()) {
            filtrarProductos(filtro);
        } else {
            mostrarProductos(todosLosProductos);
        }
    }
    
    
    private void mostrarProductos(List<ProductoDto> productos) {
        javafx.collections.ObservableList<ProductoDto> items = 
            javafx.collections.FXCollections.observableArrayList(productos);
        tableProductos.setItems(items);
    }
    
    
    public String formatearPrecio(Double precioCRC) {
        if (precioCRC == null) {
            precioCRC = 0.0;
        }
        
        String currency = "CRC - Colón";
        if (parametrosMap != null && parametrosMap.containsKey("MONEDA")) {
            currency = parametrosMap.get("MONEDA").getValor();
        }
        
        
        java.math.BigDecimal tipoCambio = obtenerTipoCambio(currency);
        
        
        java.math.BigDecimal precioBase = java.math.BigDecimal.valueOf(precioCRC);
        java.math.BigDecimal precioConvertido = precioBase.multiply(tipoCambio)
            .setScale(2, java.math.RoundingMode.HALF_UP);
        
        
        return BillingCalculator.formatCurrency(precioConvertido, currency);
    }
    
    
    public java.math.BigDecimal obtenerTipoCambio(String moneda) {
        if (moneda == null || moneda.startsWith("CRC")) {
            return java.math.BigDecimal.ONE; 
        }
        
        if (moneda.startsWith("USD")) {
            ParametroDto usdParam = parametrosMap.get("TIPO_CAMBIO_USD");
            if (usdParam != null && usdParam.getValorComoDecimal() != null) {
                java.math.BigDecimal rate = java.math.BigDecimal.valueOf(usdParam.getValorComoDecimal());
                return java.math.BigDecimal.ONE.divide(rate, 6, java.math.RoundingMode.HALF_UP);
            }
            
            return java.math.BigDecimal.ONE.divide(java.math.BigDecimal.valueOf(520), 6, java.math.RoundingMode.HALF_UP);
        }
        
        if (moneda.startsWith("EUR")) {
            ParametroDto eurParam = parametrosMap.get("TIPO_CAMBIO_EUR");
            if (eurParam != null && eurParam.getValorComoDecimal() != null) {
                java.math.BigDecimal rate = java.math.BigDecimal.valueOf(eurParam.getValorComoDecimal());
                return java.math.BigDecimal.ONE.divide(rate, 6, java.math.RoundingMode.HALF_UP);
            }
            
            return java.math.BigDecimal.ONE.divide(java.math.BigDecimal.valueOf(570), 6, java.math.RoundingMode.HALF_UP);
        }
        
        return java.math.BigDecimal.ONE; 
    }
    
    
    private void actualizarTablaProductos() {
        if (tableProductos != null) {
            tableProductos.refresh();
        }
    }
    
    
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
