package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad que representa el detalle de una factura (productos facturados).
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Entity
@Table(name = "DETALLE_FACTURA", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "DetalleFactura.findAll", query = "SELECT d FROM DetalleFactura d"),
    @NamedQuery(name = "DetalleFactura.findByFactura", query = "SELECT d FROM DetalleFactura d WHERE d.factura.idFactura = :idFactura"),
    @NamedQuery(name = "DetalleFactura.findByProducto", query = "SELECT d FROM DetalleFactura d WHERE d.producto.idProducto = :idProducto")
})
public class DetalleFactura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SEQ_DETALLE_FACTURA", sequenceName = "SEQ_DETALLE_FACTURA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DETALLE_FACTURA")
    @Column(name = "ID_DETALLE_FACTURA")
    private Long idDetalleFactura;

    @NotNull
    @Min(1)
    @Column(name = "CANTIDAD", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "PRECIO_UNITARIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull
    @Column(name = "SUBTOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Relaciones
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_FACTURA", referencedColumnName = "ID_FACTURA", nullable = false)
    private Factura factura;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_PRODUCTO", referencedColumnName = "ID_PRODUCTO", nullable = false)
    private Producto producto;

    // Constructores
    public DetalleFactura() {
        this.cantidad = 0;
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public DetalleFactura(Producto producto, Integer cantidad) {
        this();
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        calcularSubtotal();
    }

    // Métodos de negocio
    public void calcularSubtotal() {
        if (this.precioUnitario != null && this.cantidad != null) {
            this.subtotal = this.precioUnitario.multiply(new BigDecimal(this.cantidad));
        }
    }

    // Getters y Setters
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
        calcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) {
            this.precioUnitario = producto.getPrecio();
            calcularSubtotal();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDetalleFactura);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DetalleFactura other = (DetalleFactura) obj;
        return Objects.equals(idDetalleFactura, other.idDetalleFactura);
    }

    @Override
    public String toString() {
        return "DetalleFactura{" +
                "idDetalleFactura=" + idDetalleFactura +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", producto=" + (producto != null ? producto.getNombre() : "N/A") +
                '}';
    }
}
