package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Schema(description = "Cierre de caja del sistema RESTaurant")
public class CierreCajaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador del cierre", example = "1")
    @JsonbProperty("idCierreCaja")
    private Long idCierreCaja;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Schema(description = "Fecha y hora del cierre", example = "2024-05-30T18:00:00", required = true)
    @JsonbProperty("fechaHora")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHora;

    @NotNull(message = "El efectivo inicial es obligatorio")
    @Schema(description = "Efectivo inicial en caja", example = "10000.00", required = true)
    @JsonbProperty("efectivoInicial")
    private BigDecimal efectivoInicial;

    @NotNull(message = "El efectivo del sistema es obligatorio")
    @Schema(description = "Efectivo según el sistema", example = "45000.00", required = true)
    @JsonbProperty("efectivoSistema")
    private BigDecimal efectivoSistema;

    @NotNull(message = "El efectivo declarado es obligatorio")
    @Schema(description = "Efectivo declarado por el cajero", example = "55000.00", required = true)
    @JsonbProperty("efectivoDeclarado")
    private BigDecimal efectivoDeclarado;

    @Schema(description = "Diferencia en efectivo", example = "0.00")
    @JsonbProperty("efectivoDiferencia")
    private BigDecimal efectivoDiferencia;

    @NotNull(message = "La tarjeta del sistema es obligatoria")
    @Schema(description = "Pagos con tarjeta según sistema", example = "30000.00", required = true)
    @JsonbProperty("tarjetaSistema")
    private BigDecimal tarjetaSistema;

    @NotNull(message = "La tarjeta declarada es obligatoria")
    @Schema(description = "Pagos con tarjeta declarados", example = "30000.00", required = true)
    @JsonbProperty("tarjetaDeclarado")
    private BigDecimal tarjetaDeclarado;

    @Schema(description = "Diferencia en tarjeta", example = "0.00")
    @JsonbProperty("tarjetaDiferencia")
    private BigDecimal tarjetaDiferencia;

    @NotNull(message = "El total de facturas es obligatorio")
    @Min(value = 0, message = "El total de facturas debe ser mayor o igual a 0")
    @Schema(description = "Cantidad de facturas generadas", example = "25", required = true)
    @JsonbProperty("totalFacturas")
    private Integer totalFacturas;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Schema(description = "Observaciones del cierre", example = "Cierre normal sin novedades")
    @JsonbProperty("observaciones")
    private String observaciones;

    
    @NotNull(message = "El cajero es obligatorio")
    @Schema(description = "ID del cajero que cierra", example = "2", required = true)
    @JsonbProperty("idCajero")
    private Long idCajero;

    
    @Schema(description = "Nombre del cajero", example = "María García")
    @JsonbProperty("nombreCajero")
    private String nombreCajero;

    
    @Schema(description = "Total del sistema (efectivo + tarjeta)", example = "75000.00")
    @JsonbProperty("totalSistema")
    private BigDecimal totalSistema;

    @Schema(description = "Total declarado (efectivo + tarjeta)", example = "85000.00")
    @JsonbProperty("totalDeclarado")
    private BigDecimal totalDeclarado;

    @Schema(description = "Diferencia total", example = "0.00")
    @JsonbProperty("totalDiferencia")
    private BigDecimal totalDiferencia;

    @Schema(description = "Indica si hay diferencias", example = "false")
    @JsonbProperty("tieneDiferencias")
    private Boolean tieneDiferencias;

    
    @Schema(description = "Indica si el registro fue modificado", example = "false")
    @JsonbProperty("modificado")
    private Boolean modificado;

    @Schema(description = "Marca de tiempo de modificación", example = "2024-06-01T12:15:00")
    @JsonbProperty("fecha")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fecha;

    
    public CierreCajaDto() {
        this.modificado = false;
        this.fecha = LocalDateTime.now();
        this.fechaHora = LocalDateTime.now();
        this.efectivoInicial = BigDecimal.ZERO;
        this.efectivoSistema = BigDecimal.ZERO;
        this.efectivoDeclarado = BigDecimal.ZERO;
        this.efectivoDiferencia = BigDecimal.ZERO;
        this.tarjetaSistema = BigDecimal.ZERO;
        this.tarjetaDeclarado = BigDecimal.ZERO;
        this.tarjetaDiferencia = BigDecimal.ZERO;
        this.totalFacturas = 0;
        this.totalSistema = BigDecimal.ZERO;
        this.totalDeclarado = BigDecimal.ZERO;
        this.totalDiferencia = BigDecimal.ZERO;
        this.tieneDiferencias = false;
    }

    public CierreCajaDto(CierreCaja cierre) {
        this();
        if (cierre != null) {
            this.idCierreCaja = cierre.getIdCierreCaja();
            this.fechaHora = cierre.getFechaHora();
            this.efectivoInicial = cierre.getEfectivoInicial();
            this.efectivoSistema = cierre.getEfectivoSistema();
            this.efectivoDeclarado = cierre.getEfectivoDeclarado();
            this.efectivoDiferencia = cierre.getEfectivoDiferencia();
            this.tarjetaSistema = cierre.getTarjetaSistema();
            this.tarjetaDeclarado = cierre.getTarjetaDeclarado();
            this.tarjetaDiferencia = cierre.getTarjetaDiferencia();
            this.totalFacturas = cierre.getTotalFacturas();
            this.observaciones = cierre.getObservaciones();

            if (cierre.getCajero() != null) {
                this.idCajero = cierre.getCajero().getIdUsuario();
                this.nombreCajero = cierre.getCajero().getNombre();
            }

            
            this.totalSistema = cierre.getTotalSistema();
            this.totalDeclarado = cierre.getTotalDeclarado();
            this.totalDiferencia = cierre.getTotalDiferencia();
            this.tieneDiferencias = cierre.tieneDiferencias();
        }
    }

    
    public Long getIdCierreCaja() {
        return idCierreCaja;
    }

    public void setIdCierreCaja(Long idCierreCaja) {
        this.idCierreCaja = idCierreCaja;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getEfectivoInicial() {
        return efectivoInicial;
    }

    public void setEfectivoInicial(BigDecimal efectivoInicial) {
        this.efectivoInicial = efectivoInicial;
    }

    public BigDecimal getEfectivoSistema() {
        return efectivoSistema;
    }

    public void setEfectivoSistema(BigDecimal efectivoSistema) {
        this.efectivoSistema = efectivoSistema;
    }

    public BigDecimal getEfectivoDeclarado() {
        return efectivoDeclarado;
    }

    public void setEfectivoDeclarado(BigDecimal efectivoDeclarado) {
        this.efectivoDeclarado = efectivoDeclarado;
    }

    public BigDecimal getEfectivoDiferencia() {
        return efectivoDiferencia;
    }

    public void setEfectivoDiferencia(BigDecimal efectivoDiferencia) {
        this.efectivoDiferencia = efectivoDiferencia;
    }

    public BigDecimal getTarjetaSistema() {
        return tarjetaSistema;
    }

    public void setTarjetaSistema(BigDecimal tarjetaSistema) {
        this.tarjetaSistema = tarjetaSistema;
    }

    public BigDecimal getTarjetaDeclarado() {
        return tarjetaDeclarado;
    }

    public void setTarjetaDeclarado(BigDecimal tarjetaDeclarado) {
        this.tarjetaDeclarado = tarjetaDeclarado;
    }

    public BigDecimal getTarjetaDiferencia() {
        return tarjetaDiferencia;
    }

    public void setTarjetaDiferencia(BigDecimal tarjetaDiferencia) {
        this.tarjetaDiferencia = tarjetaDiferencia;
    }

    public Integer getTotalFacturas() {
        return totalFacturas;
    }

    public void setTotalFacturas(Integer totalFacturas) {
        this.totalFacturas = totalFacturas;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getIdCajero() {
        return idCajero;
    }

    public void setIdCajero(Long idCajero) {
        this.idCajero = idCajero;
    }

    public String getNombreCajero() {
        return nombreCajero;
    }

    public void setNombreCajero(String nombreCajero) {
        this.nombreCajero = nombreCajero;
    }

    public BigDecimal getTotalSistema() {
        return totalSistema;
    }

    public void setTotalSistema(BigDecimal totalSistema) {
        this.totalSistema = totalSistema;
    }

    public BigDecimal getTotalDeclarado() {
        return totalDeclarado;
    }

    public void setTotalDeclarado(BigDecimal totalDeclarado) {
        this.totalDeclarado = totalDeclarado;
    }

    public BigDecimal getTotalDiferencia() {
        return totalDiferencia;
    }

    public void setTotalDiferencia(BigDecimal totalDiferencia) {
        this.totalDiferencia = totalDiferencia;
    }

    public Boolean getTieneDiferencias() {
        return tieneDiferencias;
    }

    public void setTieneDiferencias(Boolean tieneDiferencias) {
        this.tieneDiferencias = tieneDiferencias;
    }

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "CierreCajaDto{" +
                "idCierreCaja=" + idCierreCaja +
                ", fechaHora=" + fechaHora +
                ", cajero='" + nombreCajero + '\'' +
                ", totalFacturas=" + totalFacturas +
                ", totalSistema=" + totalSistema +
                ", totalDeclarado=" + totalDeclarado +
                ", tieneDiferencias=" + tieneDiferencias +
                '}';
    }
}
