/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 *
 * @author fonse
 */
@Schema(description = "Reportes para consultas y la generacion ")
public class ReporteDto {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Id del Reporte", example = "1")
    private Long idReporte;
    @Schema(description = "Tipo de reporte", example = "CIERRE_DE_CAJA")
    private String tipoReporte;
    @Schema(description = "Nombre del Reporte", example = "SALESREPORT")
    private String nombreReporte;
    @Schema(description = "Contenido pdf")
    private byte[] pdf;
    @Schema(description = "Fecha Generado")
    private Date fechaGenerado;
    @Schema(description = "Id del Usuario", example = "3")
    private Long idUsuario;
    @Schema(description = "Parametros del Reporte", example = "fecha = 2025")
    private String parametros;
    @Schema(description = "Nombre del Usuario")
    private String nombreUsuario;
    @Schema(description = "Fecha Inicio")
    private Date fechaInicio;
    @Schema(description = "Fecha Fin")
    private Date fechaFin;
    @Schema(description = "Id del Cajero", example = "1")
    private Long idCajero;
    @Schema(description = "Id de la factura", example = "2")
    private Long idFactura;

    public ReporteDto() {
    }

    public ReporteDto(Reporte reporte) {

        this.idReporte = reporte.getIdReporte();
        this.tipoReporte = reporte.getTipoReporte();
        this.nombreReporte = reporte.getNombreReporte();
        this.pdf = reporte.getPdf();
        this.fechaGenerado = reporte.getFechaGenerado();
        this.parametros = reporte.getParametros();

        if (reporte.getUsuario() != null) {
            this.idUsuario = reporte.getUsuario().getIdUsuario();
            this.nombreUsuario = reporte.getUsuario().getNombre();
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
