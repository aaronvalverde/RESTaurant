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
    // Bandera para evitar limpiar automáticamente al cambiar combos
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
        
        // Validar que el campo del nombre del cliente tenga texto
        String nombreCliente = txfClientName.getText();
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validación");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, ingrese el nombre del cliente antes de guardar la orden.");
            alert.showAndWait();
            return;
        }
        
        // Guardar cliente (solo nombre) y luego asociarlo con la orden
        System.out.println("DEBUG - Guardando cliente con nombre: " + nombreCliente.trim());
        guardarClienteYOrden(nombreCliente.trim());
    }
    
    /**
     * Guardar cliente y luego la orden con el cliente asociado
     */
    private void guardarClienteYOrden(String nombreCliente) {
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Crear cliente
                ClienteDto cliente = new ClienteDto();
                cliente.setNombre(nombreCliente);
                
                // Guardar cliente
                Respuesta respuestaCliente = clienteService.guardarCliente(cliente);
                
                if (respuestaCliente.getEstado()) {
                    // Parsear el cliente guardado para obtener su ID
                    String clienteJson = (String) respuestaCliente.getResultado("Cliente");
                    Long idCliente = JsonParser.extraerValorLong(clienteJson, "idCliente");
                    
                    System.out.println("DEBUG - Cliente guardado con ID: " + idCliente);
                    
                    // Asignar cliente a la orden
                    currentOrder.setIdCliente(idCliente);
                } else {
                    System.err.println("Error guardando cliente: " + respuestaCliente.getMensaje());
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                // Ahora guardar la orden con el cliente asociado
                guardarOrdenDirectamente();
            }
            
            @Override
            protected void failed() {
                System.err.println("Error en tarea de guardar cliente: " + getException().getMessage());
                // Guardar orden sin cliente si falla
                guardarOrdenDirectamente();
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Guardar la orden en la base de datos
     */
    private void guardarOrdenDirectamente() {
        
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
                    
                    // Limpiar la orden actual (incluye limpiar combos)
                    limpiarOrden();
                    
                    // Recargar secciones para actualizar la vista con estados actualizados
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
    
    /**
     * Limpia la orden actual
     */
    private void limpiarOrden() {
        isClearing = true; // Activar bandera antes de limpiar
        
        currentOrder = new OrdenDto();
        orderContainer.getChildren().clear();
        txfClientName.clear();
        
        // Limpiar selección de sección y mesa
        cmbSection.clearSelection();
        cmbTable.clearSelection();
        currentSection = null;
        currentMesa = null;
        
        // Limpiar filtro de grupos y mostrar todos los productos
        cmbGroups.clearSelection();
        mostrarTodosLosProductos();
        
        updateTotals();
        
        isClearing = false; // Desactivar bandera después de limpiar
    }
    
    /**
     * Cargar la orden activa de una mesa ocupada
     */
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
                            // Parsear las órdenes
                            List<String> objetosOrdenes = JsonParser.extraerObjetosDelArray(jsonArray);
                            System.out.println("DEBUG - Número de órdenes encontradas: " + objetosOrdenes.size());
                            
                            // Buscar la orden ABIERTA (activa)
                            boolean ordenEncontrada = false;
                            for (String objetoJson : objetosOrdenes) {
                                String estado = JsonParser.extraerValor(objetoJson, "estado");
                                System.out.println("DEBUG - Orden con estado: " + estado);
                                if ("ABIERTA".equals(estado)) {
                                    // Parsear la orden completa
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
    
    /**
     * Parsear y cargar una orden desde JSON
     */
    private void parsearYCargarOrden(String objetoJson) {
        try {
            System.out.println("DEBUG - Parseando orden: " + objetoJson);
            
            // Crear nueva orden con los datos parseados
            OrdenDto orden = new OrdenDto();
            orden.setIdOrden(JsonParser.extraerValorLong(objetoJson, "idOrden"));
            orden.setIdMesa(JsonParser.extraerValorLong(objetoJson, "idMesa"));
            orden.setIdSeccion(JsonParser.extraerValorLong(objetoJson, "idSeccion"));
            
            // Intentar ambos nombres de campo para el salonero
            Long idSalonero = JsonParser.extraerValorLong(objetoJson, "idSalonero");
            if (idSalonero == null) {
                idSalonero = JsonParser.extraerValorLong(objetoJson, "idUsuarioSalonero");
            }
            orden.setIdSalonero(idSalonero);
            orden.setEstado(JsonParser.extraerValor(objetoJson, "estado"));
            
            // Parsear ID del cliente
            Long idCliente = JsonParser.extraerValorLong(objetoJson, "idCliente");
            if (idCliente != null) {
                orden.setIdCliente(idCliente);
            }
            
            // Parsear nombre del cliente para mostrar en la interfaz
            String nombreCliente = JsonParser.extraerValor(objetoJson, "nombreCliente");
            if (nombreCliente != null && !nombreCliente.trim().isEmpty()) {
                // Establecer el nombre en el campo de texto
                javafx.application.Platform.runLater(() -> {
                    txfClientName.setText(nombreCliente);
                });
            }
            
            // Parsear fecha - intentar ambos nombres de campo
            String fechaStr = JsonParser.extraerValor(objetoJson, "fecha");
            if (fechaStr == null || fechaStr.isEmpty()) {
                fechaStr = JsonParser.extraerValor(objetoJson, "fechaCreacion");
            }
            if (fechaStr != null && !fechaStr.isEmpty()) {
                orden.setFechaHora(java.time.LocalDateTime.parse(fechaStr));
            }
            
            System.out.println("DEBUG - Orden parseada - ID: " + orden.getIdOrden() + ", Mesa: " + orden.getIdMesa());
            
            // Parsear detalles - usar extraerArray para obtener el array anidado
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
                
                // NO agregamos los detalles a la orden aquí
                // Los detalles se agregarán cuando se carguen los productos en la interfaz
                // orden.setDetalles(detalles);
            }
            
            // Establecer como orden actual SIN detalles
            // Los detalles se crearán al cargar los productos
            currentOrder.setIdOrden(orden.getIdOrden());
            currentOrder.setIdMesa(orden.getIdMesa());
            currentOrder.setIdSeccion(orden.getIdSeccion());
            currentOrder.setIdCliente(orden.getIdCliente());
            currentOrder.setIdSalonero(orden.getIdSalonero());
            currentOrder.setEstado(orden.getEstado());
            currentOrder.setFechaHora(orden.getFechaHora());
            
            System.out.println("DEBUG - Orden establecida como actual, cargando " + detalles.size() + " detalles en interfaz");
            
            // Limpiar los detalles antiguos del currentOrder antes de cargar los nuevos
            currentOrder.getDetalles().clear();
            
            // Cargar detalles en la interfaz
            orderContainer.getChildren().clear();
            System.out.println("DEBUG - OrderContainer limpiado, cargando productos...");
            
            // Pasar los detalles parseados para cargar los productos
            for (DetalleOrdenDto detalle : detalles) {
                // Buscar el producto correspondiente y agregar a la interfaz
                cargarProductoYAgregarDetalle(detalle);
            }
            
            // NO llamar updateTotals() aquí porque los productos se cargan de forma asíncrona
            // updateTotals() se llamará después de que se agregue el último producto
            
            // Mostrar mensaje
            mostrarAlerta("Orden Cargada", "Se ha cargado la orden existente de esta mesa. Puede modificarla o ir a facturar.");
            
        } catch (Exception e) {
            System.err.println("Error parseando orden: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Cargar producto y agregar detalle a la interfaz
     */
    private void cargarProductoYAgregarDetalle(DetalleOrdenDto detalle) {
        System.out.println("DEBUG - Cargando producto ID: " + detalle.getIdProducto() + " con cantidad: " + detalle.getCantidad());
        
        // Buscar el producto en la lista actual de productos
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("DEBUG - Solicitando producto al servicio...");
                // Obtener el producto desde el servicio
                Respuesta respuesta = productoService.getProducto(detalle.getIdProducto());
                
                javafx.application.Platform.runLater(() -> {
                    System.out.println("DEBUG - Respuesta del servicio - Estado: " + respuesta.getEstado());
                    if (respuesta.getEstado()) {
                        // El servicio puede devolver ProductoDto directamente o como JSON String
                        Object resultado = respuesta.getResultado("Producto");
                        ProductoDto producto = null;
                        
                        if (resultado instanceof ProductoDto) {
                            // Si es directamente un ProductoDto
                            producto = (ProductoDto) resultado;
                            System.out.println("DEBUG - Producto recibido directamente: " + producto.getNombre());
                        } else if (resultado instanceof String) {
                            // Si es JSON String
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
                                // Usar loadExistingDetail() para cargar el detalle con su cantidad específica
                                itemController.loadExistingDetail(producto, detalle);
                                itemController.setParentController(OrderController.this);
                                
                                // Agregar el detalle al currentOrder manualmente
                                currentOrder.getDetalles().add(detalle);
                                
                                // Guardar referencia del controlador en el nodo
                                itemNode.setUserData(itemController);
                                orderContainer.getChildren().add(itemNode);
                                
                                // Actualizar totales después de agregar este producto
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
    
    /**
     * Parsear un producto desde JSON
     */
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
                // Buscar el grupo correspondiente en la lista del combo por ID
                GrupoProductoDto grupoEnCombo = grupos.stream()
                    .filter(g -> g.getIdGrupoProducto().equals(group.getIdGrupoProducto()))
                    .findFirst()
                    .orElse(null);
                
                if (grupoEnCombo != null) {
                    // Seleccionar grupo en combo y filtrar
                    cmbGroups.selectItem(grupoEnCombo);
                    mostrarProductosPorGrupo(grupoEnCombo);
                } else {
                    // Si no está en el combo, solo filtrar sin seleccionar
                    mostrarProductosPorGrupo(group);
                }
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
                if (mesa == null) return "";
                // Agregar distintivo si la mesa está ocupada
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
            // Ignorar cambios durante limpieza automática
            if (isClearing) {
                return;
            }
            
            if (newVal != null) {
                currentMesa = newVal;
                // Si la mesa está ocupada, cargar su orden
                if ("OCUPADA".equals(newVal.getEstado())) {
                    cargarOrdenDeMesa(newVal.getIdMesa());
                } else {
                    // Si es una mesa libre, solo limpiar productos pero mantener la selección
                    currentOrder = new OrdenDto();
                    orderContainer.getChildren().clear();
                    txfClientName.clear();
                    updateTotals();
                }
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
            // Mostrar todas las mesas (LIBRE y OCUPADA)
            if (mesa != null && ("LIBRE".equals(mesa.getEstado()) || "OCUPADA".equals(mesa.getEstado()))) {
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
