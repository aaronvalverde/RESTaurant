package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla CLIENTE
 * Representa información de clientes para facturación y envío de correos
 * 
 * @author gambo
 */
@Entity
@Table(name = "CLIENTE", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "Cliente.findAll", query = "SELECT c FROM Cliente c ORDER BY c.nombre"),
    @NamedQuery(name = "Cliente.findByCorreo", query = "SELECT c FROM Cliente c WHERE c.correo = :correo")
})
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "CLIENTE_ID_GENERATOR", sequenceName = "SEQ_CLIENTE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIENTE_ID_GENERATOR")
    @Basic(optional = false)
    @Column(name = "ID_CLIENTE")
    private Long idCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "NOMBRE", length = 100, nullable = false)
    private String nombre;

    @Email(message = "Debe proporcionar un correo electrónico válido")
    @Size(max = 120, message = "El correo no puede exceder 120 caracteres")
    @Column(name = "CORREO", length = 120, unique = true)
    private String correo;

    @Basic(optional = false)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    // Constructores
    public Cliente() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Cliente(ClienteDto clienteDto) {
        this();
        actualizarDesdeDto(clienteDto);
    }

    // Método para actualizar desde DTO
    public void actualizarDesdeDto(ClienteDto dto) {
        if (dto.getIdCliente() != null) {
            this.idCliente = dto.getIdCliente();
        }
        this.nombre = dto.getNombre();
        this.correo = dto.getCorreo();
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCliente != null ? idCliente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Cliente)) {
            return false;
        }
        Cliente other = (Cliente) object;
        return !((this.idCliente == null && other.idCliente != null) || 
                (this.idCliente != null && !this.idCliente.equals(other.idCliente)));
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
