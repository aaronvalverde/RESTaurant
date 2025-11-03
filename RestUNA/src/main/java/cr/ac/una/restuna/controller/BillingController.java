package cr.ac.una.restuna.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import cr.ac.una.restuna.model.ClienteDto;
import cr.ac.una.restuna.model.DetalleFacturaDto;
import cr.ac.una.restuna.model.DetalleOrdenDto;
import cr.ac.una.restuna.model.FacturaDto;
import cr.ac.una.restuna.model.MesaDto;
import cr.ac.una.restuna.model.OrdenDto;
import cr.ac.una.restuna.service.MesaService;
import cr.ac.una.restuna.service.OrdenService;
import cr.ac.una.restuna.service.FacturaService;
import cr.ac.una.restuna.service.ClienteService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.UserSession;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class BillingController extends Controller implements Initializable {

    
    @FXML
    private MFXButton btnCash;
    @FXML
    private MFXButton btnCard;
    @FXML
    private MFXButton btnSinpe;
    @FXML
    private MFXButton btnTip;

    @FXML
    private MFXButton btnOk;
    @FXML
    private MFXButton btnCancel;

    @FXML
    private MFXTextField txfAmount; 
    @FXML
    private TreeTableColumn<DetalleFacturaDto, String> tbcProduct; 
    @FXML
    private TreeTableColumn<DetalleFacturaDto, Long> tbcQuantity; 
    @FXML
    private TreeTableColumn<DetalleFacturaDto, Long> tbcUnitPrice; 
    @FXML
    private TreeTableColumn<DetalleFacturaDto, Long> tbcTotal; 
    @FXML
    private JFXTreeTableView<DetalleFacturaDto> tbvPaymentBreakdown;
    @FXML
    private MFXTextField txfAmountDue; 
    @FXML
    private MFXTextField txfAmountTendered; 
    @FXML
    private MFXTextField txfChange; 
    @FXML
    private MFXTextField txfClient; 
    @FXML
    private MFXTextField txfClientEmail; 
    @FXML
    private MFXTextField txfTotalDue; 
    @FXML
    private MFXTextField txfTotalTip; 
    @FXML
    private VBox numberKeypadRoot;
    @FXML
    private MFXButton btnRegisterAmount;

    private ObservableList<DetalleFacturaDto> detailBill = FXCollections.observableArrayList();
    private FacturaDto currentBill = new FacturaDto();
    private MesaDto mesaActual;
    private OrdenDto ordenActual;

    private double totalToPay = 0.0;
    private double totalPaid = 0.0;
    private double totalTip = 0.0;
    
    
    private double cashPayment = 0.0;      
    private double cardPayment = 0.0;      
    private double paypalPayment = 0.0;    
    
    
    private final MesaService mesaService = new MesaService();
    private final OrdenService ordenService = new OrdenService();
    private final FacturaService facturaService = new FacturaService();
    private final ClienteService clienteService = new ClienteService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configTable();
        loadNumberKeypad();
        clear();

    }

    @Override
    public void initialize() {
    }

    private void configTable() {

        tbcProduct.setCellValueFactory(new TreeItemPropertyValueFactory<>("nombreProducto"));
        tbcQuantity.setCellValueFactory(new TreeItemPropertyValueFactory<>("cantidad"));
        tbcUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("precioUnitario"));
        tbcTotal.setCellValueFactory(new TreeItemPropertyValueFactory<>("subtotal"));

        TreeItem<DetalleFacturaDto> root = new TreeItem<>(new DetalleFacturaDto());
        tbvPaymentBreakdown.setRoot(root);
        tbvPaymentBreakdown.setShowRoot(false);

    }

    
    @FXML
    void onActionBtnCash(ActionEvent event) {
        try {
            double amount = Double.parseDouble(txfAmount.getText().trim());
            
            if (amount <= 0) {
                showMessage("El monto debe ser mayor a cero.");
                return;
            }
            
            
            double pendiente = totalToPay - totalPaid;
            if (amount < pendiente) {
                showMessage("Monto insuficiente. Debe: ₡" + String.format("%.2f", pendiente));
                return;
            }
            
            
            cashPayment = amount;
            totalPaid = amount;
            
            txfAmountTendered.setText(String.format("%.2f", totalPaid));
            updateTotal();
            txfAmount.clear();
            
            System.out.println("Pago en efectivo registrado: ₡" + amount);
            
        } catch (NumberFormatException e) {
            showMessage("Ingrese un monto válido.");
        }
    }

    @FXML
    void onActionBtnCard(ActionEvent event) {
        try {
            double amount = Double.parseDouble(txfAmount.getText().trim());
            
            if (amount <= 0) {
                showMessage("El monto debe ser mayor a cero.");
                return;
            }
            
            
            double pendiente = totalToPay - totalPaid;
            if (amount < pendiente) {
                showMessage("Monto insuficiente. Debe: ₡" + String.format("%.2f", pendiente));
                return;
            }
            
            
            cardPayment = amount;
            totalPaid = amount;
            
            txfAmountTendered.setText(String.format("%.2f", totalPaid));
            updateTotal();
            txfAmount.clear();
            
            System.out.println("Pago con tarjeta registrado: ₡" + amount);
            
        } catch (NumberFormatException e) {
            showMessage("Ingrese un monto válido.");
        }
    }

    @FXML
    void onActionBtnPayPal(ActionEvent event) {
        try {
            double amount = Double.parseDouble(txfAmount.getText().trim());
            
            if (amount <= 0) {
                showMessage("El monto debe ser mayor a cero.");
                return;
            }
            
            
            double pendiente = totalToPay - totalPaid;
            if (amount < pendiente) {
                showMessage("Monto insuficiente. Debe: ₡" + String.format("%.2f", pendiente));
                return;
            }
            
            
            paypalPayment = amount;
            totalPaid = amount;
            
            txfAmountTendered.setText(String.format("%.2f", totalPaid));
            updateTotal();
            txfAmount.clear();
            
            System.out.println("Pago con PayPal registrado: ₡" + amount);
            
        } catch (NumberFormatException e) {
            showMessage("Ingrese un monto válido.");
        }
    }

    @FXML
    void onActionBtnTip(ActionEvent event) {
        try {
            double amount = Double.parseDouble(txfAmount.getText().trim());
            
            if (amount <= 0) {
                showMessage("La propina debe ser mayor a cero.");
                return;
            }
            
            
            totalTip = amount;
            
            txfTotalTip.setText(String.format("%.2f", totalTip));
            txfAmount.clear();
            
            System.out.println("Propina registrada: ₡" + amount);
            
        } catch (NumberFormatException e) {
            showMessage("Ingrese un monto válido para la propina.");
        }
    }

    
    @FXML
    void onActionBtnOk(ActionEvent event) {

        if (txfClient.getText().trim().isEmpty()) {
            showMessage("Ingrese el nombre del cliente.");
            return;
        }
        
        if (totalPaid < totalToPay) {
            showMessage("El monto pagado es insuficiente.");
            return;
        }
        
        
        double cambio = (totalPaid > totalToPay) ? (totalPaid - totalToPay) : 0.0;
        
        currentBill = new FacturaDto();
        currentBill.setIdFactura(System.currentTimeMillis());
        
        
        long totalLong = (long) totalToPay;
        long subtotalCalc = Math.round(totalLong / 1.23);
        long impuestoVentaCalc = Math.round(subtotalCalc * 0.13);  
        long impuestoServicioCalc = Math.round(subtotalCalc * 0.10);  
        
        currentBill.setSubtotal(subtotalCalc);
        currentBill.setImpuestoVenta(impuestoVentaCalc);
        currentBill.setImpuestoServicio(impuestoServicioCalc);
        currentBill.setTotal(totalLong);
        currentBill.setEfectivoRecibido((long) cashPayment);
        currentBill.setTarjetaRecibida((long) cardPayment);
        currentBill.setVuelto((long) cambio);
        currentBill.setFechaFactura(new Date());
        
        
        if (UserSession.getInstance().getCurrentUser() != null) {
            currentBill.setIdUsuarioCajero(UserSession.getInstance().getCurrentUser().getIdUsuario());
        }
        
        
        if (ordenActual != null) {
            currentBill.setIdOrden(ordenActual.getIdOrden());
            currentBill.setIdCliente(ordenActual.getIdCliente());
        }
        
        
        System.out.println("=== RESUMEN DE FACTURA ===");
        System.out.println("Total a pagar: ₡" + String.format("%.2f", totalToPay));
        System.out.println("Efectivo: ₡" + String.format("%.2f", cashPayment));
        System.out.println("Tarjeta: ₡" + String.format("%.2f", cardPayment));
        System.out.println("PayPal: ₡" + String.format("%.2f", paypalPayment));
        System.out.println("Total pagado: ₡" + String.format("%.2f", totalPaid));
        System.out.println("Cambio devuelto: ₡" + String.format("%.2f", cambio));
        System.out.println("--- ADICIONAL ---");
        System.out.println("Propina recibida: ₡" + String.format("%.2f", totalTip));
        System.out.println("==========================");
        
        
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                
                String correoCliente = txfClientEmail.getText().trim();
                if (!correoCliente.isEmpty() && currentBill.getIdCliente() != null) {
                    
                    Respuesta respCliente = clienteService.getCliente(currentBill.getIdCliente());
                    if (respCliente.getEstado()) {
                        String clienteJson = (String) respCliente.getResultado("Cliente");
                        if (clienteJson != null && !clienteJson.trim().isEmpty()) {
                            
                            ClienteDto cliente = parsearCliente(clienteJson);
                            cliente.setCorreo(correoCliente);
                            
                            
                            Respuesta respGuardar = clienteService.guardarCliente(cliente);
                            if (!respGuardar.getEstado()) {
                                System.err.println("Error al guardar correo del cliente: " + respGuardar.getMensaje());
                            } else {
                                System.out.println("Correo del cliente actualizado: " + correoCliente);
                            }
                        }
                    }
                }
                
                
                Respuesta respFactura = facturaService.guardarFactura(currentBill);
                if (!respFactura.getEstado()) {
                    throw new Exception("Error al guardar factura: " + respFactura.getMensaje());
                }
                
                
                if (mesaActual != null && mesaActual.getIdMesa() != null) {
                    mesaActual.setEstado("LIBRE");
                    Respuesta respMesa = mesaService.actualizarEstadoMesa(mesaActual.getIdMesa(), "LIBRE");
                    if (!respMesa.getEstado()) {
                        throw new Exception("Error al liberar la mesa: " + respMesa.getMensaje());
                    }
                }
                
                
                if (ordenActual != null && ordenActual.getIdOrden() != null) {
                    ordenActual.setEstado("FACTURADA");
                    
                    
                    if (ordenActual.getIdSalonero() == null) {
                        ordenActual.setIdSalonero(UserSession.getInstance().getCurrentUser().getIdUsuario());
                    }
                    
                    Respuesta respOrden = ordenService.cambiarEstadoOrden(ordenActual.getIdOrden(), "FACTURADA");
                    if (!respOrden.getEstado()) {
                        throw new Exception("Error al actualizar estado de la orden: " + respOrden.getMensaje());
                    }
                }
                
                return null;
            }
            
            @Override
            protected void succeeded() {
                showMessage("Factura generada correctamente. Mesa liberada.");
                clear();
                FlowController.getInstance().goView(AppKeys.SECTIONS);
            }
            
            @Override
            protected void failed() {
                showMessage("Error al generar factura: " + getException().getMessage());
            }
        };
        
        new Thread(task).start();
    }

    @FXML
    void onActionBtnCancel(ActionEvent event) {
        
        clear();
        FlowController.getInstance().goView(AppKeys.SECTIONS);
    }

    @FXML
    private void onActionBtnRegisterAmount(ActionEvent event) {

        try {
            double amount = Double.parseDouble(txfAmount.getText().trim());
            totalToPay += amount;
            txfTotalDue.setText(String.format("%.2f", totalToPay));
            updateTotal();
        } catch (NumberFormatException e) {
            showMessage("Ingrese un monto válido.");
        }
    }

    private void updateTotal() {
        double change = totalPaid - totalToPay;
        if (change >= 0) {
            txfChange.setText(String.format("%.2f", change));
            txfAmountDue.setText("0.00");
        } else {
            txfAmountDue.setText(String.format("%.2f", -change));
            txfChange.setText("0.00");
        }
    }

    private void loadNumberKeypad() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/NumberKeypad.fxml"));
            AnchorPane keypadView = loader.load();
            NumberKeypadController numberKeypad = loader.getController();

            numberKeypad.setInputRoot(txfAmount);
            numberKeypadRoot.getChildren().add(keypadView);

            keypadView.prefHeightProperty().bind(numberKeypadRoot.heightProperty());
            keypadView.prefWidthProperty().bind(numberKeypadRoot.widthProperty());

        } catch (IOException ex) {
            System.getLogger(BillingController.class.getName())
                    .log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void clear() {
        txfAmount.setText("");
        txfTotalDue.setText("0.00");
        txfAmountDue.setText("0.00");
        txfAmountTendered.setText("0.00");
        txfChange.setText("0.00");
        txfTotalTip.setText("0.00");
        txfClient.setText("");
        txfClientEmail.setText("");
        
        totalPaid = 0.0;
        totalToPay = 0.0;
        totalTip = 0.0;
        
        
        cashPayment = 0.0;
        cardPayment = 0.0;
        paypalPayment = 0.0;
        
        detailBill.clear();
        
        
        if (tbvPaymentBreakdown != null && tbvPaymentBreakdown.getRoot() != null) {
            tbvPaymentBreakdown.getRoot().getChildren().clear();
        }
    }
    
    
    public void cargarMesa(MesaDto mesa) {
        this.mesaActual = mesa;
        
        
        if (mesa != null && mesa.getIdMesa() != null) {
            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    cr.ac.una.restuna.util.Respuesta respuesta = ordenService.getOrdenesPorMesa(mesa.getIdMesa());
                    
                    if (respuesta.getEstado()) {
                        String contenido = (String) respuesta.getResultado("Ordenes");
                        if (contenido != null && !contenido.trim().isEmpty()) {
                            
                            if (contenido.trim().startsWith("[")) {
                                List<String> ordenes = JsonParser.extraerObjetosDelArray(contenido);
                                if (!ordenes.isEmpty()) {
                                    ordenActual = parsearOrden(ordenes.get(0));
                                }
                            } else {
                                ordenActual = parsearOrden(contenido);
                            }
                        }
                    }
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    if (ordenActual != null) {
                        cargarDatosOrden();
                    }
                }
                
                @Override
                protected void failed() {
                    showMessage("Error al cargar la orden: " + getException().getMessage());
                }
            };
            
            new Thread(task).start();
        }
    }
    
    
    public void cargarOrden(OrdenDto orden) {
        this.ordenActual = orden;
        
        
        if (orden != null && orden.getIdMesa() != null) {
            javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    cr.ac.una.restuna.util.Respuesta respuesta = mesaService.getMesa(orden.getIdMesa());
                    
                    if (respuesta.getEstado()) {
                        String contenido = (String) respuesta.getResultado("Mesa");
                        if (contenido != null && !contenido.trim().isEmpty()) {
                            mesaActual = parsearMesa(contenido);
                        }
                    }
                    return null;
                }
            };
            
            new Thread(task).start();
        }
        
        
        cargarDatosOrden();
    }
    
    
    private OrdenDto parsearOrden(String json) {
        OrdenDto orden = new OrdenDto();
        
        orden.setIdOrden(JsonParser.extraerValorLong(json, "idOrden"));
        orden.setIdMesa(JsonParser.extraerValorLong(json, "idMesa"));
        orden.setIdSeccion(JsonParser.extraerValorLong(json, "idSeccion"));
        orden.setIdCliente(JsonParser.extraerValorLong(json, "idCliente"));
        orden.setIdSalonero(JsonParser.extraerValorLong(json, "idUsuarioSalonero"));
        orden.setEstado(JsonParser.extraerValorString(json, "estado"));
        orden.setNombreCliente(JsonParser.extraerValorString(json, "nombreCliente"));
        
        String subtotalStr = JsonParser.extraerValorNumerico(json, "subtotal");
        if (subtotalStr != null) {
            orden.setSubtotal(Double.parseDouble(subtotalStr));
        }
        
        
        String detallesJson = JsonParser.extraerArray(json, "detalles");
        if (detallesJson != null) {
            List<String> detallesList = JsonParser.extraerObjetosDelArray(detallesJson);
            for (String detalleJson : detallesList) {
                DetalleOrdenDto detalle = parsearDetalleOrden(detalleJson);
                orden.getDetalles().add(detalle);
            }
        }
        
        return orden;
    }
    
    
    private DetalleOrdenDto parsearDetalleOrden(String json) {
        DetalleOrdenDto detalle = new DetalleOrdenDto();
        
        detalle.setIdDetalleOrden(JsonParser.extraerValorLong(json, "idDetalleOrden"));
        detalle.setIdProducto(JsonParser.extraerValorLong(json, "idProducto"));
        detalle.setCantidad(JsonParser.extraerValorInteger(json, "cantidad"));
        detalle.setNombreProducto(JsonParser.extraerValorString(json, "nombreProducto"));
        
        String precioUnitarioStr = JsonParser.extraerValorNumerico(json, "precioUnitario");
        if (precioUnitarioStr != null) {
            detalle.setPrecioUnitario(Double.parseDouble(precioUnitarioStr));
        }
        
        String subtotalStr = JsonParser.extraerValorNumerico(json, "subtotal");
        if (subtotalStr != null) {
            detalle.setSubtotal(Double.parseDouble(subtotalStr));
        }
        
        detalle.setObservaciones(JsonParser.extraerValorString(json, "observaciones"));
        
        return detalle;
    }
    
    
    private MesaDto parsearMesa(String json) {
        MesaDto mesa = new MesaDto();
        
        mesa.setIdMesa(JsonParser.extraerValorLong(json, "idMesa"));
        
        String numeroMesaStr = JsonParser.extraerValorNumerico(json, "numeroMesa");
        if (numeroMesaStr != null) {
            mesa.setNumeroMesa(numeroMesaStr);
        }
        
        String capacidadStr = JsonParser.extraerValorNumerico(json, "capacidad");
        if (capacidadStr != null) {
            try {
                mesa.setCapacidad(Integer.parseInt(capacidadStr));
            } catch (NumberFormatException e) {
                
            }
        }
        
        mesa.setEstado(JsonParser.extraerValorString(json, "estado"));
        mesa.setIdSeccion(JsonParser.extraerValorLong(json, "idSeccion"));
        
        return mesa;
    }
    
    
    private cr.ac.una.restuna.model.ClienteDto parsearCliente(String json) {
        cr.ac.una.restuna.model.ClienteDto cliente = new cr.ac.una.restuna.model.ClienteDto();
        
        cliente.setIdCliente(JsonParser.extraerValorLong(json, "idCliente"));
        cliente.setNombre(JsonParser.extraerValorString(json, "nombre"));
        cliente.setCorreo(JsonParser.extraerValorString(json, "correo"));
        
        return cliente;
    }
    
    
    private void cargarDatosOrden() {
        if (ordenActual == null) {
            return;
        }
        
        
        clear();
        
        
        if (ordenActual.getNombreCliente() != null && !ordenActual.getNombreCliente().trim().isEmpty()) {
            txfClient.setText(ordenActual.getNombreCliente());
        } else if (ordenActual.getIdCliente() != null) {
            
            cargarNombreCliente(ordenActual.getIdCliente());
        }
        
        
        totalToPay = 0.0;
        
        if (ordenActual.getDetalles() != null && !ordenActual.getDetalles().isEmpty()) {
            for (DetalleOrdenDto detalleOrden : ordenActual.getDetalles()) {
                double precioUnitario = detalleOrden.getPrecioUnitario() != null ? detalleOrden.getPrecioUnitario() : 0.0;
                int cantidad = detalleOrden.getCantidad() != null ? detalleOrden.getCantidad() : 1;
                double subtotal = detalleOrden.getSubtotal() != null ? detalleOrden.getSubtotal() : (precioUnitario * cantidad);
                String nombreProducto = detalleOrden.getNombreProducto() != null ? detalleOrden.getNombreProducto() : "Producto sin nombre";
                
                totalToPay += subtotal;
                
                
                DetalleFacturaDto itemParaTabla = new DetalleFacturaDto();
                itemParaTabla.setIdDetalleFactura(detalleOrden.getIdDetalleOrden());
                itemParaTabla.setNombreProducto(nombreProducto);
                itemParaTabla.setCantidad((long) cantidad);
                itemParaTabla.setPrecioUnitario((long) precioUnitario);
                itemParaTabla.setSubtotal((long) subtotal);
                
                
                TreeItem<DetalleFacturaDto> item = new TreeItem<>(itemParaTabla);
                tbvPaymentBreakdown.getRoot().getChildren().add(item);
            }
        }
        
        
        txfTotalDue.setText(String.format("%.2f", totalToPay));
        txfAmountDue.setText(String.format("%.2f", totalToPay));
        txfAmountTendered.setText("0.00");
        txfChange.setText("0.00");
        txfTotalTip.setText("0.00");
        
        
        txfAmount.requestFocus();
    }
    
    
    private void cargarNombreCliente(Long idCliente) {
        javafx.concurrent.Task<String> task = new javafx.concurrent.Task<String>() {
            @Override
            protected String call() throws Exception {
                cr.ac.una.restuna.util.Respuesta respuesta = clienteService.getCliente(idCliente);
                
                if (respuesta.getEstado()) {
                    String contenido = (String) respuesta.getResultado("Cliente");
                    if (contenido != null && !contenido.trim().isEmpty()) {
                        return JsonParser.extraerValorString(contenido, "nombre");
                    }
                }
                return null;
            }
            
            @Override
            protected void succeeded() {
                String nombre = getValue();
                if (nombre != null && !nombre.trim().isEmpty()) {
                    txfClient.setText(nombre);
                }
            }
            
            @Override
            protected void failed() {
                
            }
        };
        
        new Thread(task).start();
    }
}
