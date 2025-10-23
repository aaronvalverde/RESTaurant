/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.dto;

/**
 *
 * @author fonse
 */
public class DetalleOrdenDto {
    
    private Long idDetalleOrden;
    private Long idOrden;
    private Long idProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
    private String observaciones;

    public DetalleOrdenDto(Long idDetalleOrden, Long idOrden, Long idProducto, Integer cantidad, Double precioUnitario, Double subtotal, String observaciones) {
        this.idDetalleOrden = idDetalleOrden;
        this.idOrden = idOrden;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.observaciones = observaciones;
    }

    public DetalleOrdenDto() {
    }

    public Long getIdDetalleOrden() {
        return idDetalleOrden;
    }

    public Long getIdOrden() {
        return idOrden;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setIdDetalleOrden(Long idDetalleOrden) {
        this.idDetalleOrden = idDetalleOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    private void calcularSubtotal(){
        if(precioUnitario != null && cantidad != null){
            this.subtotal = precioUnitario * cantidad;
        }
    }
    
    
}
