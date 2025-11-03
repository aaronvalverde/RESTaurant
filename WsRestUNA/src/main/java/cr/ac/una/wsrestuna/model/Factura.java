package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidad que representa una factura en el sistema.
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Entity
@Table(name = "FACTURA", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "Factura.findAll", query = "SELECT f FROM Factura f ORDER BY f.fechaHora DESC"),
    @NamedQuery(name = "Factura.findByCliente", query = "SELECT f FROM Factura f WHERE f.cliente.idCliente = :idCliente ORDER BY f.fechaHora DESC"),
    @NamedQuery(name = "Factura.findByCajero", query = "SELECT f FROM Factura f WHERE f.cajero.idUsuario = :idUsuario ORDER BY f.fechaHora DESC"),
    @NamedQuery(name = "Factura.findByFecha", query = "SELECT f FROM Factura f WHERE f.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY f.fechaHora DESC"),
    @NamedQuery(name = "Factura.findByMetodoPago", query = "SELECT f FROM Factura f WHERE f.metodoPago = :metodoPago ORDER BY f.fechaHora DESC")
})
public class Factura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SEQ_FACTURA", sequenceName = "SEQ_FACTURA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FACTURA")
    @Column(name = "ID_FACTURA")
    private Long idFactura;

    @NotNull
    @Column(name = "FECHA_HORA", nullable = false)
    private LocalDateTime fechaHora;

    @NotNull
    @Column(name = "SUBTOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @Column(name = "IMPUESTO_VENTA", nullable = false, precision = 10, scale = 2)
    private BigDecimal impuestoVenta;

    @NotNull
    @Column(name = "IMPUESTO_SERVICIO", nullable = false, precision = 10, scale = 2)
    private BigDecimal impuestoServicio;

    @NotNull
    @Column(name = "TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "METODO_PAGO", nullable = false, length = 20)
    private String metodoPago; // EFECTIVO, TARJETA, MIXTO

    @Column(name = "EFECTIVO_RECIBIDO", precision = 10, scale = 2)
    private BigDecimal efectivoRecibido;

    @Column(name = "TARJETA_RECIBIDO", precision = 10, scale = 2)
    private BigDecimal tarjetaRecibido;

    @Column(name = "VUELTO", precision = 10, scale = 2)
    private BigDecimal vuelto;

    @Size(max = 500)
    @Column(name = "OBSERVACIONES", length = 500)
    private String observaciones;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "ID_ORDEN", referencedColumnName = "ID_ORDEN")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "ID_CLIENTE", referencedColumnName = "ID_CLIENTE")
    private Cliente cliente;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_CAJERO", referencedColumnName = "ID_USUARIO", nullable = false)
    private Usuario cajero;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalles;

    // Constructores
    public Factura() {
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

    // Métodos de negocio
    public void calcularTotales() {
        this.subtotal = detalles.stream()
                .map(DetalleFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.total = this.subtotal.add(this.impuestoVenta).add(this.impuestoServicio);
    }

    public void calcularVuelto() {
        BigDecimal totalRecibido = this.efectivoRecibido.add(this.tarjetaRecibido);
        this.vuelto = totalRecibido.subtract(this.total);
        if (this.vuelto.compareTo(BigDecimal.ZERO) < 0) {
            this.vuelto = BigDecimal.ZERO;
        }
    }

    public void agregarDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
        calcularTotales();
    }

    public void removerDetalle(DetalleFactura detalle) {
        detalles.remove(detalle);
        detalle.setFactura(null);
        calcularTotales();
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
        calcularVuelto();
    }

    public BigDecimal getTarjetaRecibido() {
        return tarjetaRecibido;
    }

    public void setTarjetaRecibido(BigDecimal tarjetaRecibido) {
        this.tarjetaRecibido = tarjetaRecibido;
        calcularVuelto();
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

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getCajero() {
        return cajero;
    }

    public void setCajero(Usuario cajero) {
        this.cajero = cajero;
    }

    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFactura);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Factura other = (Factura) obj;
        return Objects.equals(idFactura, other.idFactura);
    }

    @Override
    public String toString() {
        return "Factura{" +
                "idFactura=" + idFactura +
                ", fechaHora=" + fechaHora +
                ", total=" + total +
                ", metodoPago='" + metodoPago + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "N/A") +
                ", cajero=" + (cajero != null ? cajero.getNombre() : "N/A") +
                '}';
    }
}
