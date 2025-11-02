package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "TURNO")
@NamedQueries({
        @NamedQuery(name = "Turno.findAll", query = "SELECT t FROM Turno t ORDER BY t.usuarioId"),
        @NamedQuery(name = "Turno.findByUser", query = "SELECT t FROM Turno t WHERE t.usuarioId = :usuario")
})

public class Turno implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "TURNO_ID_GENERSTOR", sequenceName = "SEQ_TURNO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TURNO_ID_GENERSTOR")
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;

    @Basic(optional = false)
    @Column(name = "USUARIO_ID", nullable = false)
    private long usuarioId;

    @Basic(optional = false)
    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "FECHA_FINAL")
    private LocalDateTime fechaFinal;

    @Column(name = "DURACION")
    private Integer duracion;

    @Basic(optional = false)
    @Column(name = "ESTADO", nullable = false)
    private String estado;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "USUARIO_ID", insertable = false, updatable = false)
    private Usuario usuario;

    public Turno() {
        fechaInicio = LocalDateTime.now();
        estado = "A";
        fechaFinal = null;
        duracion = null;
    }

    public Turno(Long id) {
        this();
        this.id = id;
    }

    public Turno(Long id, Long usuarioId) {
        this();
        this.id = id;
        this.usuarioId = usuarioId;
    }

    public Turno(TurnoDto turnoDto) {
        this();
        this.id = turnoDto.getId();
        this.usuarioId = turnoDto.getUsuarioId();
        this.fechaInicio = turnoDto.getFechaInicio();
        this.fechaFinal = turnoDto.getFechaFinal();
        this.duracion = turnoDto.getDuracion();
        this.estado = turnoDto.getEstado();
        this.version = turnoDto.getVersion();
    }


    public Boolean isActivo() {
        return "A".equals(estado);
    }

    public void finalizarTurno() {
        this.fechaFinal = LocalDateTime.now();
        this.estado = "F";
        calcularDuracion();
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void calcularDuracion() {
        if (fechaInicio != null && fechaFinal != null) {
            if (fechaInicio.isAfter(fechaFinal)) {
                return;
            }
            this.duracion = (int) java.time.Duration.between(fechaInicio, fechaFinal).toMinutes();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(LocalDateTime fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Turno)) {
            return false;
        }
        Turno other = (Turno) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Turno{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", fechaInicio=" + fechaInicio +
                ", fechaFinal=" + fechaFinal +
                ", duracion=" + duracion +
                ", estado='" + estado + '\'' +
                ", version=" + version +
                '}';
    }
}
