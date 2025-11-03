package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Schema(description = "Detalle de factura - producto facturado")
public class DetalleFacturaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador del detalle", example = "1")
    @JsonbProperty("idDetalleFactura")
    private Long idDetalleFactura;

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

    
    @Schema(description = "ID de la factura", example = "5")
    @JsonbProperty("idFactura")
    private Long idFactura;

    @NotNull(message = "El producto es obligatorio")
    @Schema(description = "ID del producto", example = "15", required = true)
    @JsonbProperty("idProducto")
    private Long idProducto;

    
    @Schema(description = "Nombre del producto", example = "Hamburguesa Clásica")
    @JsonbProperty("nombreProducto")
    private String nombreProducto;

    @Schema(description = "Descripción del producto", example = "Con queso y vegetales")
    @JsonbProperty("descripcionProducto")
    private String descripcionProducto;

    
    public DetalleFacturaDto() {
        this.cantidad = 0;
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public DetalleFacturaDto(DetalleFactura detalle) {
        this();
        if (detalle != null) {
            this.idDetalleFactura = detalle.getIdDetalleFactura();
            this.cantidad = detalle.getCantidad();
            this.precioUnitario = detalle.getPrecioUnitario();
            this.subtotal = detalle.getSubtotal();

            if (detalle.getFactura() != null) {
                this.idFactura = detalle.getFactura().getIdFactura();
            }
            if (detalle.getProducto() != null) {
                this.idProducto = detalle.getProducto().getIdProducto();
                this.nombreProducto = detalle.getProducto().getNombre();
                this.descripcionProducto = detalle.getProducto().getDescripcion();
            }
        }
    }

    
    public Long getIdDetalleFactura() {
        return idDetalleFactura;
    }

    public void setIdDetalleFactura(Long idDetalleFactura) {
        this.idDetalleFactura = idDetalleFactura;
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

    public Long getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura = idFactura;
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
        return "DetalleFacturaDto{" +
                "idDetalleFactura=" + idDetalleFactura +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", producto='" + nombreProducto + '\'' +
                '}';
    }
}
