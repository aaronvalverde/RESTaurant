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
 * Entidad que representa una orden en el sistema.
 * 
 * @author gambo
 */
@Entity
@Table(name = "ORDEN", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "Orden.findAll", query = "SELECT o FROM Orden o ORDER BY o.fechaHora DESC"),
    @NamedQuery(name = "Orden.findByCliente", query = "SELECT o FROM Orden o WHERE o.cliente.idCliente = :idCliente ORDER BY o.fechaHora DESC"),
    @NamedQuery(name = "Orden.findByMesa", query = "SELECT o FROM Orden o WHERE o.mesa.idMesa = :idMesa AND o.estado = :estado"),
    @NamedQuery(name = "Orden.findByEstado", query = "SELECT o FROM Orden o WHERE o.estado = :estado ORDER BY o.fechaHora DESC"),
    @NamedQuery(name = "Orden.findBySeccion", query = "SELECT o FROM Orden o WHERE o.seccion.idSeccion = :idSeccion AND o.estado = :estado ORDER BY o.fechaHora DESC"),
    @NamedQuery(name = "Orden.findBySalonero", query = "SELECT o FROM Orden o WHERE o.salonero.idUsuario = :idUsuario ORDER BY o.fechaHora DESC"),
    @NamedQuery(name = "Orden.findByFecha", query = "SELECT o FROM Orden o WHERE o.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY o.fechaHora DESC")
})
public class Orden implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SEQ_ORDEN", sequenceName = "SEQ_ORDEN", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ORDEN")
    @Column(name = "ID_ORDEN")
    private Long idOrden;

    @NotNull
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaHora;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private String estado; // PENDIENTE, EN_PREPARACION, LISTA, ENTREGADA, CANCELADA

    @Column(name = "SUBTOTAL", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Size(max = 500)
    @Column(name = "OBSERVACIONES", length = 500)
    private String observaciones;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "ID_MESA", referencedColumnName = "ID_MESA")
    private Mesa mesa;

    @ManyToOne
    @JoinColumn(name = "ID_CLIENTE", referencedColumnName = "ID_CLIENTE")
    private Cliente cliente;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_SECCION", referencedColumnName = "ID_SECCION", nullable = false)
    private Seccion seccion;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO_SALONERO", referencedColumnName = "ID_USUARIO", nullable = false)
    private Usuario salonero;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles;

    // Constructores
    public Orden() {
        this.fechaHora = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.subtotal = BigDecimal.ZERO;
        this.detalles = new ArrayList<>();
    }

    // Métodos de negocio
    public void calcularSubtotal() {
        this.subtotal = detalles.stream()
                .map(DetalleOrden::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void agregarDetalle(DetalleOrden detalle) {
        detalles.add(detalle);
        detalle.setOrden(this);
        calcularSubtotal();
    }

    public void removerDetalle(DetalleOrden detalle) {
        detalles.remove(detalle);
        detalle.setOrden(null);
        calcularSubtotal();
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

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public Usuario getSalonero() {
        return salonero;
    }

    public void setSalonero(Usuario salonero) {
        this.salonero = salonero;
    }

    public List<DetalleOrden> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleOrden> detalles) {
        this.detalles = detalles;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrden);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Orden other = (Orden) obj;
        return Objects.equals(idOrden, other.idOrden);
    }

    @Override
    public String toString() {
        return "Orden{" +
                "idOrden=" + idOrden +
                ", fechaHora=" + fechaHora +
                ", estado='" + estado + '\'' +
                ", subtotal=" + subtotal +
                ", mesa=" + (mesa != null ? mesa.getNumeroMesa() : "N/A") +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "N/A") +
                ", seccion=" + (seccion != null ? seccion.getNombre() : "N/A") +
                '}';
    }
}
