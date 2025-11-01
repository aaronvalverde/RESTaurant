package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO para transferencia de datos de DetalleOrden
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Schema(description = "Detalle de orden - producto ordenado")
public class DetalleOrdenDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador del detalle", example = "1")
    @JsonbProperty("idDetalleOrden")
    private Long idDetalleOrden;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Schema(description = "Cantidad del producto", example = "2", required = true)
    @JsonbProperty("cantidad")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Schema(description = "Precio unitario del producto", example = "5000.00", required = true)
    @JsonbProperty("precioUnitario")
    private BigDecimal precioUnitario;

    @NotNull(message = "El subtotal es obligatorio")
    @Schema(description = "Subtotal (cantidad × precio)", example = "10000.00", required = true)
    @JsonbProperty("subtotal")
    private BigDecimal subtotal;

    // IDs de relaciones
    @Schema(description = "ID de la orden", example = "5")
    @JsonbProperty("idOrden")
    private Long idOrden;

    @NotNull(message = "El producto es obligatorio")
    @Schema(description = "ID del producto", example = "15", required = true)
    @JsonbProperty("idProducto")
    private Long idProducto;

    // Información adicional del producto
    @Schema(description = "Nombre del producto", example = "Hamburguesa Clásica")
    @JsonbProperty("nombreProducto")
    private String nombreProducto;

    @Schema(description = "Descripción del producto", example = "Con queso y vegetales")
    @JsonbProperty("descripcionProducto")
    private String descripcionProducto;

    // Constructores
    public DetalleOrdenDto() {
        this.cantidad = 0;
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public DetalleOrdenDto(DetalleOrden detalle) {
        this();
        if (detalle != null) {
            this.idDetalleOrden = detalle.getIdDetalleOrden();
            this.cantidad = detalle.getCantidad();
            this.precioUnitario = detalle.getPrecioUnitario();
            this.subtotal = detalle.getSubtotal();

            if (detalle.getOrden() != null) {
                this.idOrden = detalle.getOrden().getIdOrden();
            }
            if (detalle.getProducto() != null) {
                this.idProducto = detalle.getProducto().getIdProducto();
                this.nombreProducto = detalle.getProducto().getNombre();
                this.descripcionProducto = detalle.getProducto().getDescripcion();
            }
        }
    }

    // Getters y Setters
    public Long getIdDetalleOrden() {
        return idDetalleOrden;
    }

    public void setIdDetalleOrden(Long idDetalleOrden) {
        this.idDetalleOrden = idDetalleOrden;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Long getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcionProducto() {
        return descripcionProducto;
    }

    public void setDescripcionProducto(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    @Override
    public String toString() {
        return "DetalleOrdenDto{" +
                "idDetalleOrden=" + idDetalleOrden +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", producto='" + nombreProducto + '\'' +
                '}';
    }
}
