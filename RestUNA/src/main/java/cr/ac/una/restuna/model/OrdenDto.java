package cr.ac.una.restuna.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class OrdenDto implements Serializable {
    
    private Long idOrden;
    private Long idMesa;
    private Long idSeccion;
    private Long idCliente;
    private Long idUsuarioSalonero;
    private String numeroOrden;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaHora;
    private Double subtotal;
    private List<DetalleOrdenDto> detalles;
    
    
    private transient String nombreCliente;

    public OrdenDto() {
        this.fechaHora = LocalDateTime.now();
        this.estado = "PENDIENTE";
        this.subtotal = 0.0;
        this.detalles = new ArrayList<>();
    }

    
    public Long getIdOrden() {
        return idOrden;
    }

    public Long getIdMesa() {
        return idMesa;
    }

    public Long getIdSeccion() {
        return idSeccion;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public Long getIdSalonero() {
        return idUsuarioSalonero;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public String getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public List<DetalleOrdenDto> getDetalles() {
        return detalles;
    }

    
    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public void setIdMesa(Long idMesa) {
        this.idMesa = idMesa;
    }

    public void setIdSeccion(Long idSeccion) {
        this.idSeccion = idSeccion;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public void setIdSalonero(Long idSalonero) {
        this.idUsuarioSalonero = idSalonero;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public void setDetalles(List<DetalleOrdenDto> detalles) {
        this.detalles = detalles;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public void calcularSubtotal() {
        subtotal = detalles.stream().mapToDouble(DetalleOrdenDto::getSubtotal).sum();
    }
}
