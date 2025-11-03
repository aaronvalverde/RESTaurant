package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;


@Entity
@Table(name = "DETALLE_ORDEN", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "DetalleOrden.findAll", query = "SELECT d FROM DetalleOrden d"),
    @NamedQuery(name = "DetalleOrden.findByOrden", query = "SELECT d FROM DetalleOrden d WHERE d.orden.idOrden = :idOrden"),
    @NamedQuery(name = "DetalleOrden.findByProducto", query = "SELECT d FROM DetalleOrden d WHERE d.producto.idProducto = :idProducto")
})
public class DetalleOrden implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SEQ_DETALLE_ORDEN", sequenceName = "SEQ_DETALLE_ORDEN", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DETALLE_ORDEN")
    @Column(name = "ID_DETALLE_ORDEN")
    private Long idDetalleOrden;

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

    
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_ORDEN", referencedColumnName = "ID_ORDEN", nullable = false)
    private Orden orden;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_PRODUCTO", referencedColumnName = "ID_PRODUCTO", nullable = false)
    private Producto producto;

    
    public DetalleOrden() {
        this.cantidad = 0;
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public DetalleOrden(Producto producto, Integer cantidad) {
        this();
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        calcularSubtotal();
    }

    
    public void calcularSubtotal() {
        if (this.precioUnitario != null && this.cantidad != null) {
            this.subtotal = this.precioUnitario.multiply(new BigDecimal(this.cantidad));
        }
    }

    
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

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
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
        return Objects.hash(idDetalleOrden);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DetalleOrden other = (DetalleOrden) obj;
        return Objects.equals(idDetalleOrden, other.idDetalleOrden);
    }

    @Override
    public String toString() {
        return "DetalleOrden{" +
                "idDetalleOrden=" + idDetalleOrden +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", producto=" + (producto != null ? producto.getNombre() : "N/A") +
                '}';
    }
}
