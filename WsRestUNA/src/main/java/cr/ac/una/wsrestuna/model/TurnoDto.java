package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "Turno del usuario registrado en el sistema RESTuna")
public class TurnoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador del turno", example = "1")
    @JsonbProperty("id")
    private Long id;

    @Schema(description = "Identificador del usuario relacionado con el turno", example = "admin")
    @JsonbProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Fecha de inicio del turno", example = "2024-05-30T08:30:00")
    private LocalDateTime fechaInicio;

    @Schema(description = "Fecha del final del turno", example = "2024-06-01T12:15:00")
    private LocalDateTime fechaFinal;

    @Schema(description = "Duracion del turno enn minutos", example = "320")
    private Integer duracion;

    @Schema(description = "Optimistic Locking JPA", example = "1")
    private Long version;

    @Schema(description = "Entidad del usuario al cual se relaciona el turno", example = "admin")
    private UsuarioDto usuarioDto;

    @Pattern(regexp = "^[AF]$", message = "El estado debe ser A (Activo) o F (Finalizado)")
    @Schema(description = "Estado del turno por parte del usuario", example = "A")
    private String estado;

    public TurnoDto() {
        this.fechaInicio = LocalDateTime.now();
        this.estado = "A";
        this.fechaFinal = null;
        this.duracion = null;
    }

    public TurnoDto(Long id) {
        this();
        this.id = id;
    }

    public TurnoDto(Long id, Long usuarioId) {
        this();
        this.id = id;
        this.usuarioId = usuarioId;
    }

    public TurnoDto(Turno turno){
        this();
        this.id = turno.getId();
        this.usuarioId = turno.getUsuarioId();
        this.fechaInicio = turno.getFechaInicio();
        this.fechaFinal = turno.getFechaFinal();
        this.duracion = turno.getDuracion();
        this.estado = turno.getEstado();
        this.version = turno.getVersion();
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
