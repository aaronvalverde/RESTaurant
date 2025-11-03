package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un cierre de caja en el sistema.
 * Registra los montos de efectivo y tarjeta al cerrar turno del cajero.
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Entity
@Table(name = "CIERRE_CAJA", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "CierreCaja.findAll", query = "SELECT c FROM CierreCaja c ORDER BY c.fechaHora DESC"),
    @NamedQuery(name = "CierreCaja.findByCajero", query = "SELECT c FROM CierreCaja c WHERE c.cajero.idUsuario = :idUsuario ORDER BY c.fechaHora DESC"),
    @NamedQuery(name = "CierreCaja.findByFecha", query = "SELECT c FROM CierreCaja c WHERE c.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY c.fechaHora DESC")
})
public class CierreCaja implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SEQ_CIERRE_CAJA", sequenceName = "SEQ_CIERRE_CAJA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CIERRE_CAJA")
    @Column(name = "ID_CIERRE_CAJA")
    private Long idCierreCaja;

    @NotNull
    @Column(name = "FECHA_HORA", nullable = false)
    private LocalDateTime fechaHora;

    @NotNull
    @Column(name = "EFECTIVO_INICIAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal efectivoInicial;

    @NotNull
    @Column(name = "EFECTIVO_SISTEMA", nullable = false, precision = 10, scale = 2)
    private BigDecimal efectivoSistema;

    @NotNull
    @Column(name = "EFECTIVO_DECLARADO", nullable = false, precision = 10, scale = 2)
    private BigDecimal efectivoDeclarado;

    @NotNull
    @Column(name = "EFECTIVO_DIFERENCIA", nullable = false, precision = 10, scale = 2)
    private BigDecimal efectivoDiferencia;

    @NotNull
    @Column(name = "TARJETA_SISTEMA", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarjetaSistema;

    @NotNull
    @Column(name = "TARJETA_DECLARADO", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarjetaDeclarado;

    @NotNull
    @Column(name = "TARJETA_DIFERENCIA", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarjetaDiferencia;

    @NotNull
    @Column(name = "TOTAL_FACTURAS", nullable = false)
    private Integer totalFacturas;

    @Size(max = 500)
    @Column(name = "OBSERVACIONES", length = 500)
    private String observaciones;

    // Relaciones
    @NotNull
    @ManyToOne
    @JoinColumn(name = "ID_CAJERO", referencedColumnName = "ID_USUARIO", nullable = false)
    private Usuario cajero;

    // Constructores
    public CierreCaja() {
        this.fechaHora = LocalDateTime.now();
        this.efectivoInicial = BigDecimal.ZERO;
        this.efectivoSistema = BigDecimal.ZERO;
        this.efectivoDeclarado = BigDecimal.ZERO;
        this.efectivoDiferencia = BigDecimal.ZERO;
        this.tarjetaSistema = BigDecimal.ZERO;
        this.tarjetaDeclarado = BigDecimal.ZERO;
        this.tarjetaDiferencia = BigDecimal.ZERO;
        this.totalFacturas = 0;
    }

    // Métodos de negocio
    public void calcularDiferencias() {
        // Diferencia efectivo = declarado - (inicial + sistema)
        BigDecimal efectivoEsperado = this.efectivoInicial.add(this.efectivoSistema);
        this.efectivoDiferencia = this.efectivoDeclarado.subtract(efectivoEsperado);
        
        // Diferencia tarjeta = declarado - sistema
        this.tarjetaDiferencia = this.tarjetaDeclarado.subtract(this.tarjetaSistema);
    }

    public BigDecimal getTotalSistema() {
        return this.efectivoSistema.add(this.tarjetaSistema);
    }

    public BigDecimal getTotalDeclarado() {
        return this.efectivoDeclarado.add(this.tarjetaDeclarado);
    }

    public BigDecimal getTotalDiferencia() {
        return this.efectivoDiferencia.add(this.tarjetaDiferencia);
    }

    public boolean tieneDiferencias() {
        return this.efectivoDiferencia.compareTo(BigDecimal.ZERO) != 0 || 
               this.tarjetaDiferencia.compareTo(BigDecimal.ZERO) != 0;
    }

    // Getters y Setters
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
        calcularDiferencias();
    }

    public BigDecimal getEfectivoSistema() {
        return efectivoSistema;
    }

    public void setEfectivoSistema(BigDecimal efectivoSistema) {
        this.efectivoSistema = efectivoSistema;
        calcularDiferencias();
    }

    public BigDecimal getEfectivoDeclarado() {
        return efectivoDeclarado;
    }

    public void setEfectivoDeclarado(BigDecimal efectivoDeclarado) {
        this.efectivoDeclarado = efectivoDeclarado;
        calcularDiferencias();
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
        calcularDiferencias();
    }

    public BigDecimal getTarjetaDeclarado() {
        return tarjetaDeclarado;
    }

    public void setTarjetaDeclarado(BigDecimal tarjetaDeclarado) {
        this.tarjetaDeclarado = tarjetaDeclarado;
        calcularDiferencias();
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

    public Usuario getCajero() {
        return cajero;
    }

    public void setCajero(Usuario cajero) {
        this.cajero = cajero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCierreCaja);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CierreCaja other = (CierreCaja) obj;
        return Objects.equals(idCierreCaja, other.idCierreCaja);
    }

    @Override
    public String toString() {
        return "CierreCaja{" +
                "idCierreCaja=" + idCierreCaja +
                ", fechaHora=" + fechaHora +
                ", cajero=" + (cajero != null ? cajero.getNombre() : "N/A") +
                ", totalFacturas=" + totalFacturas +
                ", efectivoDiferencia=" + efectivoDiferencia +
                ", tarjetaDiferencia=" + tarjetaDiferencia +
                '}';
    }
}
