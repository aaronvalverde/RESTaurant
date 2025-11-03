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
public class ReporteDto implements Serializable{
    
    private Long idReporte;
    private String tipoReporte;
    private String nombreReporte;
    private byte[] pdf;
    private Date fechaGenerado;
    private Long idUsuario;
    private String parametros;
    private String nombreUsuario;
    private Date fechaInicio;
    private Date fechaFin;
    private Long idCajero;
    private Long idFactura;

    public ReporteDto() {
    }

    public Long getIdReporte() {
        return idReporte;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public String getNombreReporte() {
        return nombreReporte;
    }

    public byte[] getPdf() {
        return pdf;
    }

    public Date getFechaGenerado() {
        return fechaGenerado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getParametros() {
        return parametros;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public Long getIdCajero() {
        return idCajero;
    }

    public Long getIdFactura() {
        return idFactura;
    }

    public void setIdReporte(Long idReporte) {
        this.idReporte = idReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public void setNombreReporte(String nombreReporte) {
        this.nombreReporte = nombreReporte;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    public void setFechaGenerado(Date fechaGenerado) {
        this.fechaGenerado = fechaGenerado;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setIdCajero(Long idCajero) {
        this.idCajero = idCajero;
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura = idFactura;
    }
    
    
    
}
