package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para transferencia de datos de Factura
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Schema(description = "Factura del sistema RESTaurant")
public class FacturaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador de la factura", example = "1")
    @JsonbProperty("idFactura")
    private Long idFactura;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Schema(description = "Fecha y hora de la factura", example = "2024-05-30T14:30:00", required = true)
    @JsonbProperty("fechaHora")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHora;

    @NotNull(message = "El subtotal es obligatorio")
    @Schema(description = "Subtotal de la factura", example = "15000.00", required = true)
    @JsonbProperty("subtotal")
    private BigDecimal subtotal;

    @NotNull(message = "El impuesto de venta es obligatorio")
    @Schema(description = "Impuesto de venta (IVA)", example = "1950.00", required = true)
    @JsonbProperty("impuestoVenta")
    private BigDecimal impuestoVenta;

    @NotNull(message = "El impuesto de servicio es obligatorio")
    @Schema(description = "Impuesto de servicio", example = "1500.00", required = true)
    @JsonbProperty("impuestoServicio")
    private BigDecimal impuestoServicio;

    @NotNull(message = "El total es obligatorio")
    @Schema(description = "Total de la factura", example = "18450.00", required = true)
    @JsonbProperty("total")
    private BigDecimal total;

    @NotBlank(message = "El método de pago es obligatorio")
    @Size(min = 1, max = 20, message = "El método de pago debe tener entre 1 y 20 caracteres")
    @Schema(description = "Método de pago", allowableValues = {"EFECTIVO", "TARJETA", "MIXTO"}, example = "EFECTIVO", required = true)
    @JsonbProperty("metodoPago")
    private String metodoPago;

    @Schema(description = "Efectivo recibido", example = "20000.00")
    @JsonbProperty("efectivoRecibido")
    private BigDecimal efectivoRecibido;

    @Schema(description = "Monto pagado con tarjeta", example = "0.00")
    @JsonbProperty("tarjetaRecibido")
    private BigDecimal tarjetaRecibido;

    @Schema(description = "Vuelto entregado", example = "1550.00")
    @JsonbProperty("vuelto")
    private BigDecimal vuelto;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Schema(description = "Observaciones de la factura", example = "Pago con billete de 20000")
    @JsonbProperty("observaciones")
    private String observaciones;

    // IDs de relaciones
    @Schema(description = "ID de la orden relacionada", example = "10")
    @JsonbProperty("idOrden")
    private Long idOrden;

    @Schema(description = "ID del cliente", example = "5")
    @JsonbProperty("idCliente")
    private Long idCliente;

    @NotNull(message = "El cajero es obligatorio")
    @Schema(description = "ID del cajero que genera la factura", example = "2", required = true)
    @JsonbProperty("idCajero")
    private Long idCajero;

    // Nombres para mostrar
    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    @JsonbProperty("nombreCliente")
    private String nombreCliente;

    @Schema(description = "Nombre del cajero", example = "María García")
    @JsonbProperty("nombreCajero")
    private String nombreCajero;

    // Detalles de la factura
    @Schema(description = "Lista de productos en la factura")
    @JsonbProperty("detalles")
    private List<DetalleFacturaDto> detalles;

    // Campos de control
    @Schema(description = "Indica si el registro fue modificado", example = "false")
    @JsonbProperty("modificado")
    private Boolean modificado;

    @Schema(description = "Marca de tiempo de modificación", example = "2024-06-01T12:15:00")
    @JsonbProperty("fecha")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fecha;

    // Constructores
    public FacturaDto() {
        this.modificado = false;
        this.fecha = LocalDateTime.now();
        this.fechaHora = LocalDateTime.now();
        this.subtotal = BigDecimal.ZERO;
        this.impuestoVenta = BigDecimal.ZERO;
        this.impuestoServicio = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.efectivoRecibido = BigDecimal.ZERO;
        this.tarjetaRecibido = BigDecimal.ZERO;
        this.vuelto = BigDecimal.ZERO;
        this.detalles = new ArrayList<>();
    }

    public FacturaDto(Factura factura) {
        this();
        if (factura != null) {
            this.idFactura = factura.getIdFactura();
            this.fechaHora = factura.getFechaHora();
            this.subtotal = factura.getSubtotal();
            this.impuestoVenta = factura.getImpuestoVenta();
            this.impuestoServicio = factura.getImpuestoServicio();
            this.total = factura.getTotal();
            this.metodoPago = factura.getMetodoPago();
            this.efectivoRecibido = factura.getEfectivoRecibido();
            this.tarjetaRecibido = factura.getTarjetaRecibido();
            this.vuelto = factura.getVuelto();
            this.observaciones = factura.getObservaciones();

            // IDs
            if (factura.getOrden() != null) {
                this.idOrden = factura.getOrden().getIdOrden();
            }
            if (factura.getCliente() != null) {
                this.idCliente = factura.getCliente().getIdCliente();
                this.nombreCliente = factura.getCliente().getNombre() + " " + factura.getCliente().getApellidos();
            }
            if (factura.getCajero() != null) {
                this.idCajero = factura.getCajero().getIdUsuario();
                this.nombreCajero = factura.getCajero().getNombre();
            }

            // Detalles
            if (factura.getDetalles() != null) {
                this.detalles = new ArrayList<>();
                for (DetalleFactura detalle : factura.getDetalles()) {
                    this.detalles.add(new DetalleFacturaDto(detalle));
                }
            }
        }
    }

    // Getters y Setters
    public Long getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura = idFactura;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuestoVenta() {
        return impuestoVenta;
    }

    public void setImpuestoVenta(BigDecimal impuestoVenta) {
        this.impuestoVenta = impuestoVenta;
    }

    public BigDecimal getImpuestoServicio() {
        return impuestoServicio;
    }

    public void setImpuestoServicio(BigDecimal impuestoServicio) {
        this.impuestoServicio = impuestoServicio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getEfectivoRecibido() {
        return efectivoRecibido;
    }

    public void setEfectivoRecibido(BigDecimal efectivoRecibido) {
        this.efectivoRecibido = efectivoRecibido;
    }

    public BigDecimal getTarjetaRecibido() {
        return tarjetaRecibido;
    }

    public void setTarjetaRecibido(BigDecimal tarjetaRecibido) {
        this.tarjetaRecibido = tarjetaRecibido;
    }

    public BigDecimal getVuelto() {
        return vuelto;
    }

    public void setVuelto(BigDecimal vuelto) {
        this.vuelto = vuelto;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdCajero() {
        return idCajero;
    }

    public void setIdCajero(Long idCajero) {
        this.idCajero = idCajero;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreCajero() {
        return nombreCajero;
    }

    public void setNombreCajero(String nombreCajero) {
        this.nombreCajero = nombreCajero;
    }

    public List<DetalleFacturaDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaDto> detalles) {
        this.detalles = detalles;
    }

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "FacturaDto{" +
                "idFactura=" + idFactura +
                ", fechaHora=" + fechaHora +
                ", total=" + total +
                ", metodoPago='" + metodoPago + '\'' +
                ", cliente='" + nombreCliente + '\'' +
                ", cajero='" + nombreCajero + '\'' +
                '}';
    }
}
