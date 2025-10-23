/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fonse
 */
public class OrdenDto implements Serializable {
    
    private Long idOrden;
    private Long idMesa;
    private Long idSeccion;
    private Long idCliente;
    private Long idUsuarioSaloreno;
    private String numeroOrden;
    private String estado;
    private String observaciones;
    private LocalDate fechaCreacion;
    private Double subtotal;
    private List<DetalleOrdenDto> detalles;

    public OrdenDto(Long idOrden, Long idMesa, Long idSeccion, Long idCliente, Long idUsuarioSaloreno, String numeroOrden, String estado, String observaciones, LocalDate fechaCreacion, Double subtotal) {
        this.idOrden = idOrden;
        this.idMesa = idMesa;
        this.idSeccion = idSeccion;
        this.idCliente = idCliente;
        this.idUsuarioSaloreno = idUsuarioSaloreno;
        this.numeroOrden = numeroOrden;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fechaCreacion = fechaCreacion;
        this.subtotal = subtotal;
    }

    public OrdenDto() {
        
        this.fechaCreacion = LocalDate.now();
        this.estado = "A";
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

    public Long getIdUsuarioSaloreno() {
        return idUsuarioSaloreno;
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

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
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

    public void setIdUsuarioSaloreno(Long idUsuarioSaloreno) {
        this.idUsuarioSaloreno = idUsuarioSaloreno;
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

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public void setDetalles(List<DetalleOrdenDto> detalles) {
        this.detalles = detalles;
    }
    
    public void calcularSubtotal(){
        
        subtotal = detalles.stream().mapToDouble(DetalleOrdenDto::getSubtotal).sum();
        
    }
    
}
