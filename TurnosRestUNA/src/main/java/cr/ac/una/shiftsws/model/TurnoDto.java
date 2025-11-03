package cr.ac.una.shiftsws.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TurnoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long usuarioId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private Integer duracion;
    private Long version;
    private UsuarioDto usuarioDto;
    private String estado;

    public TurnoDto() {
    }

    public Boolean isActivo() {
        return "A".equals(estado);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public UsuarioDto getUsuarioDto() {
        return usuarioDto;
    }

    public void setUsuarioDto(UsuarioDto usuarioDto) {
        this.usuarioDto = usuarioDto;
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
        if (!(object instanceof TurnoDto)) {
            return false;
        }
        TurnoDto other = (TurnoDto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TurnoDto{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", fechaInicio=" + fechaInicio +
                ", fechaFinal=" + fechaFinal +
                ", duracion=" + duracion +
                ", version=" + version +
                ", usuarioDto=" + usuarioDto +
                ", estado='" + estado + '\'' +
                '}';
    }
}