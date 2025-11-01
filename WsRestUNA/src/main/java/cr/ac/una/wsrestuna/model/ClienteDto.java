package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Cliente
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Schema(description = "Cliente del sistema RESTaurant")
public class ClienteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador del cliente", example = "1")
    @JsonbProperty("idCliente")
    private Long idCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    @Schema(description = "Nombre del cliente", example = "Juan", required = true)
    @JsonbProperty("nombre")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 1, max = 100, message = "Los apellidos deben tener entre 1 y 100 caracteres")
    @Schema(description = "Apellidos del cliente", example = "Pérez Rodríguez", required = true)
    @JsonbProperty("apellidos")
    private String apellidos;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(min = 9, max = 20, message = "La cédula debe tener entre 9 y 20 caracteres")
    @Schema(description = "Cédula o identificación del cliente", example = "1-1234-5678", required = true)
    @JsonbProperty("cedula")
    private String cedula;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Schema(description = "Teléfono del cliente", example = "8888-8888")
    @JsonbProperty("telefono")
    private String telefono;

    @Email(message = "El correo debe ser válido")
    @Size(max = 100, message = "El correo no puede exceder 100 caracteres")
    @Schema(description = "Correo electrónico del cliente", example = "juan.perez@email.com")
    @JsonbProperty("correo")
    private String correo;

    @Schema(description = "Fecha de creación del cliente", example = "2024-05-30T08:30:00")
    @JsonbProperty("fechaCreacion")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    // Campos de control
    @Schema(description = "Indica si el registro fue modificado", example = "false")
    @JsonbProperty("modificado")
    private Boolean modificado;

    @Schema(description = "Marca de tiempo de modificación", example = "2024-06-01T12:15:00")
    @JsonbProperty("fecha")
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fecha;

    // Constructores
    public ClienteDto() {
        this.modificado = false;
        this.fecha = LocalDateTime.now();
    }

    public ClienteDto(Cliente cliente) {
        this();
        if (cliente != null) {
            this.idCliente = cliente.getIdCliente();
            this.nombre = cliente.getNombre();
            this.apellidos = cliente.getApellidos();
            this.cedula = cliente.getCedula();
            this.telefono = cliente.getTelefono();
            this.correo = cliente.getCorreo();
            this.fechaCreacion = cliente.getFechaCreacion();
        }
    }

    // Métodos de conveniencia
    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    // Getters y Setters
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
                ", apellidos='" + apellidos + '\'' +
                ", cedula='" + cedula + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
