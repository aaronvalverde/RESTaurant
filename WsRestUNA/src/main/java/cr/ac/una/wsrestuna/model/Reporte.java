
package cr.ac.una.wsrestuna.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "REPORTE", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "Reporte.findAll", query = "SELECT p FROM Reporte p ORDER BY p.fechaGenerado"),
    @NamedQuery(name = "Reporte.findByTipo", query = "SELECT p FROM Reporte p WHERE p.tipoReporte = :tipo ORDER BY p.fechaGenerado"),
    @NamedQuery(name = "Reporte.findByFecha", query = "SELECT p FROM Reporte p WHERE p.fechaGenerado BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaGenerado")
})
public class Reporte implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reporte_seq")
    @SequenceGenerator(name = "reporte_seq", sequenceName = "SEQ_REPORTE", allocationSize = 1)
    @Column(name = "ID_REPORTE")
    private Long idReporte;

    @Column(name = "TIPO_REPORTE", length = 50, nullable = false)
    private String tipoReporte;

    @Column(name = "NOMBRE_REPORTE", length = 255, nullable = false)
    private String nombreReporte;

    @Lob
    @Column(name = "PDF")
    private byte[] pdf;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_GENERADO", nullable = false)
    private Date fechaGenerado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    @Column(name = "PARAMETROS", length = 1000)
    private String parametros;

    public Reporte() {
        this.fechaGenerado = new Date();
    }

    public Reporte(ReporteDto reportDto) {
        this();
        this.idReporte = reportDto.getIdReporte();
        actualizar(reportDto);

    }

    public void actualizar(ReporteDto reportDto) {
        this.tipoReporte = reportDto.getTipoReporte();
        this.nombreReporte = reportDto.getNombreReporte();
        this.parametros = reportDto.getParametros();
        this.pdf = reportDto.getPdf();
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

    public Usuario getUsuario() {
        return usuario;
    }

    public String getParametros() {
        return parametros;
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

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

}
