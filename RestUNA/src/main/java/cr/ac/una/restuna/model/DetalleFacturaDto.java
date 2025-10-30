/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.Serializable;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 *
 * @author fonse
 */
public class DetalleFacturaDto extends RecursiveTreeObject<DetalleFacturaDto> implements Serializable {

    private LongProperty idDetalleFactura;
    private LongProperty idFactura;
    private LongProperty idProducto;
    private LongProperty cantidad;
    private LongProperty precioUnitario;
    private LongProperty subtotal;


  
    public DetalleFacturaDto() {

        this.idDetalleFactura = new SimpleLongProperty();
        this.idFactura = new SimpleLongProperty();
        this.idProducto = new SimpleLongProperty();
        this.cantidad = new SimpleLongProperty();
        this.precioUnitario = new SimpleLongProperty();
        this.subtotal = new SimpleLongProperty();
    }

    public Long getIdDetalleFactura() {
        return idDetalleFactura.get();
    }

    public Long getIdFactura() {
        return idFactura.get();
    }

    public Long getIdProducto() {
        return idProducto.get();
    }

    public Long getCantidad() {
        return cantidad.get();
    }

    public Long getPrecioUnitario() {
        return precioUnitario.get();
    }

    public Long getSubtotal() {
        return subtotal.get();
    }

    public void setIdDetalleFactura(Long idDetalleFactura) {
        this.idDetalleFactura.set(idDetalleFactura);
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura.set(idFactura);
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto.set(idProducto);
    }

    public void setCantidad(Long cantidad) {
        this.cantidad.set(cantidad);
    }

    public void setPrecioUnitario(Long PrecioUnitario) {
        this.precioUnitario.set(PrecioUnitario);   
    }

    public void setSubtotal(Long subtotal) {
        this.subtotal.set(subtotal);
    }

    
}
