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
 * DTO para transferencia de datos de Orden
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Schema(description = "Orden del sistema RESTaurant")
public class OrdenDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador de la orden", example = "1")
    @JsonbProperty("idOrden")
    private Long idOrden;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Schema(description = "Fecha y hora de la orden", example = "2024-05-30T12:30:00", required = true)
    @JsonbProperty("fechaHora")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El estado es obligatorio")
    @Size(min = 1, max = 20, message = "El estado debe tener entre 1 y 20 caracteres")
    @Schema(description = "Estado de la orden", allowableValues = {"PENDIENTE", "EN_PREPARACION", "LISTA", "ENTREGADA", "CANCELADA"}, example = "PENDIENTE", required = true)
    @JsonbProperty("estado")
    private String estado;

    @Schema(description = "Subtotal de la orden", example = "15000.00")
    @JsonbProperty("subtotal")
    private BigDecimal subtotal;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Schema(description = "Observaciones de la orden", example = "Sin cebolla en la ensalada")
    @JsonbProperty("observaciones")
    private String observaciones;

    // IDs de relaciones
    @Schema(description = "ID de la mesa", example = "5")
    @JsonbProperty("idMesa")
    private Long idMesa;

    @Schema(description = "ID del cliente", example = "10")
    @JsonbProperty("idCliente")
    private Long idCliente;

    @NotNull(message = "La sección es obligatoria")
    @Schema(description = "ID de la sección", example = "2", required = true)
    @JsonbProperty("idSeccion")
    private Long idSeccion;

    @NotNull(message = "El salonero es obligatorio")
    @Schema(description = "ID del salonero que atiende", example = "3", required = true)
    @JsonbProperty("idSalonero")
    private Long idSalonero;

    // Nombres para mostrar
    @Schema(description = "Número de la mesa", example = "5")
    @JsonbProperty("numeroMesa")
    private String numeroMesa;

    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    @JsonbProperty("nombreCliente")
    private String nombreCliente;

    @Schema(description = "Nombre de la sección", example = "Terraza")
    @JsonbProperty("nombreSeccion")
    private String nombreSeccion;

    @Schema(description = "Nombre del salonero", example = "María García")
    @JsonbProperty("nombreSalonero")
    private String nombreSalonero;

    // Detalles de la orden
    @Schema(description = "Lista de productos en la orden")
    @JsonbProperty("detalles")
    private List<DetalleOrdenDto> detalles;

    // Campos de control
    @Schema(description = "Indica si el registro fue modificado", example = "false")
    @JsonbProperty("modificado")
    private Boolean modificado;

    @Schema(description = "Marca de tiempo de modificación", example = "2024-06-01T12:15:00")
    @JsonbProperty("fecha")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fecha;

    // Constructores
    public OrdenDto() {
        this.modificado = false;
        this.fecha = LocalDateTime.now();
        this.fechaHora = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.subtotal = BigDecimal.ZERO;
        this.detalles = new ArrayList<>();
    }

    public OrdenDto(Orden orden) {
        this();
        if (orden != null) {
            this.idOrden = orden.getIdOrden();
            this.fechaHora = orden.getFechaHora();
            this.estado = orden.getEstado();
            this.subtotal = orden.getSubtotal();
            this.observaciones = orden.getObservaciones();

            // IDs
            if (orden.getMesa() != null) {
                this.idMesa = orden.getMesa().getIdMesa();
                this.numeroMesa = orden.getMesa().getNumeroMesa();
            }
            if (orden.getCliente() != null) {
                this.idCliente = orden.getCliente().getIdCliente();
                this.nombreCliente = orden.getCliente().getNombre();
            }
            if (orden.getSeccion() != null) {
                this.idSeccion = orden.getSeccion().getIdSeccion();
                this.nombreSeccion = orden.getSeccion().getNombre();
            }
            if (orden.getSalonero() != null) {
                this.idSalonero = orden.getSalonero().getIdUsuario();
                this.nombreSalonero = orden.getSalonero().getNombre();
            }

            // Detalles
            if (orden.getDetalles() != null) {
                this.detalles = new ArrayList<>();
                for (DetalleOrden detalle : orden.getDetalles()) {
                    this.detalles.add(new DetalleOrdenDto(detalle));
                }
            }
        }
    }

    // Getters y Setters
    public Long getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(Long idMesa) {
        this.idMesa = idMesa;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(Long idSeccion) {
        this.idSeccion = idSeccion;
    }

    public Long getIdSalonero() {
        return idSalonero;
    }

    public void setIdSalonero(Long idSalonero) {
        this.idSalonero = idSalonero;
    }

    public String getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(String numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreSeccion() {
        return nombreSeccion;
    }

    public void setNombreSeccion(String nombreSeccion) {
        this.nombreSeccion = nombreSeccion;
    }

    public String getNombreSalonero() {
        return nombreSalonero;
    }

    public void setNombreSalonero(String nombreSalonero) {
        this.nombreSalonero = nombreSalonero;
    }

    public List<DetalleOrdenDto> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrdenDto> detalles) {
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
        return "OrdenDto{" +
                "idOrden=" + idOrden +
                ", fechaHora=" + fechaHora +
                ", estado='" + estado + '\'' +
                ", subtotal=" + subtotal +
                ", mesa='" + numeroMesa + '\'' +
                ", cliente='" + nombreCliente + '\'' +
                '}';
    }
}
