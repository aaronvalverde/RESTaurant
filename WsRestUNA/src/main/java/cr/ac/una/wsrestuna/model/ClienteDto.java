package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Schema(description = "Cliente del sistema RESTaurant")
public class ClienteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador del cliente", example = "1")
    @JsonbProperty("idCliente")
    private Long idCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    @Schema(description = "Nombre completo del cliente", example = "Juan Pérez", required = true)
    @JsonbProperty("nombre")
    private String nombre;

    @Email(message = "El correo debe ser válido")
    @Size(max = 120, message = "El correo no puede exceder 120 caracteres")
    @Schema(description = "Correo electrónico del cliente para envío de facturas", example = "juan.perez@email.com")
    @JsonbProperty("correo")
    private String correo;

    @Schema(description = "Fecha de creación del cliente", example = "2024-05-30T08:30:00")
    @JsonbProperty("fechaCreacion")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    
    @Schema(description = "Indica si el registro fue modificado", example = "false")
    @JsonbProperty("modificado")
    private Boolean modificado;

    @Schema(description = "Marca de tiempo de modificación", example = "2024-06-01T12:15:00")
    @JsonbProperty("fecha")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fecha;

    
    public ClienteDto() {
        this.modificado = false;
        this.fecha = LocalDateTime.now();
    }

    public ClienteDto(Cliente cliente) {
        this();
        if (cliente != null) {
            this.idCliente = cliente.getIdCliente();
            this.nombre = cliente.getNombre();
            this.correo = cliente.getCorreo();
            this.fechaCreacion = cliente.getFechaCreacion();
        }
    }

    
    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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
        return "ClienteDto{" +
                "idCliente=" + idCliente +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
