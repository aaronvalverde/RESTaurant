/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author fonse
 */
public class FacturaDto implements Serializable {

    private Long idFactura;
    private Long idOrden;
    private Long idCliente;
    private Long idUsuarioCajero;
    private String numeroFactura;
    private Date fechaFactura;
    private Long subtotal;
    private Long impuestoVenta;
    private Long impuestoServicio;
    private Long descuento;
    private Long total;
    private Long efectivoRecibido;
    private Long tarjetaRecibida;
    private Long vuelto;
    private String estado;
    private String correoEnviado;

    public FacturaDto() {
    }

    public Long getIdFactura() {
        return idFactura;
    }

    public Long getIdOrden() {
        return idOrden;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public Long getIdUsuarioCajero() {
        return idUsuarioCajero;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public Date getFechaFactura() {
        return fechaFactura;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public Long getImpuestoVenta() {
        return impuestoVenta;
    }

    public Long getImpuestoServicio() {
        return impuestoServicio;
    }

    public Long getDescuento() {
        return descuento;
    }

    public Long getTotal() {
        return total;
    }

    public Long getEfectivoRecibido() {
        return efectivoRecibido;
    }

    public Long getTarjetaRecibida() {
        return tarjetaRecibida;
    }

    public Long getVuelto() {
        return vuelto;
    }

    public String getEstado() {
        return estado;
    }

    public String getCorreoEnviado() {
        return correoEnviado;
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura = idFactura;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public void setIdUsuarioCajero(Long idUsuarioCajero) {
        this.idUsuarioCajero = idUsuarioCajero;
    }

    public void setNumeroFactura(String NumeroFactura) {
        this.numeroFactura = NumeroFactura;
    }

    public void setFechaFactura(Date FechaFactura) {
        this.fechaFactura = FechaFactura;
    }

    public void setSubtotal(Long subtotal) {
        this.subtotal = subtotal;
    }

    public void setImpuestoVenta(Long ImpuestoVenta) {
        this.impuestoVenta = ImpuestoVenta;
    }

    public void setImpuestoServicio(Long ImpuestoServicio) {
        this.impuestoServicio = ImpuestoServicio;
    }

    public void setDescuento(Long Descuento) {
        this.descuento = Descuento;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public void setEfectivoRecibido(Long EfectivoRecibido) {
        this.efectivoRecibido = EfectivoRecibido;
    }

    public void setTarjetaRecibida(Long TarjetaRecibida) {
        this.tarjetaRecibida = TarjetaRecibida;
    }

    public void setVuelto(Long vuelto) {
        this.vuelto = vuelto;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setCorreoEnviado(String CorreoEnviado) {
        this.correoEnviado = CorreoEnviado;
    }
    
    

}
