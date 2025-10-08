package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla USUARIO
 * Representa usuarios del sistema: administradores, cajeros y saloneros
 */
@Entity
@Table(name = "USUARIO")
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u ORDER BY u.usuario"),
    @NamedQuery(name = "Usuario.findByUsuario", query = "SELECT u FROM Usuario u WHERE u.usuario = :usuario"),
    @NamedQuery(name = "Usuario.findByRol", query = "SELECT u FROM Usuario u WHERE u.rol = :rol ORDER BY u.usuario"),
    @NamedQuery(name = "Usuario.findActivos", query = "SELECT u FROM Usuario u WHERE u.estado = 'A' ORDER BY u.usuario"),
    @NamedQuery(name = "Usuario.findByUsuClave", query = "SELECT u FROM Usuario u WHERE u.usuario = :usuario and u.contrasena = :clave", 
                hints = @QueryHint(name = "eclipselink.refresh", value = "true"))
})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "USUARIO_ID_GENERATOR", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USUARIO_ID_GENERATOR")
    @Basic(optional = false)
    @Column(name = "ID_USUARIO")
    private Long idUsuario;



    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 50, message = "El usuario no puede exceder 50 caracteres")
    @Basic(optional = false)
    @Column(name = "USUARIO", nullable = false, length = 50, unique = true)
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 255, message = "La contraseña no puede exceder 255 caracteres")
    @Basic(optional = false)
    @Column(name = "CONTRASENA", nullable = false, length = 255)
    private String contrasena;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(ADMINISTRADOR|CAJERO|SALONERO)$", message = "El rol debe ser ADMINISTRADOR, CAJERO o SALONERO")
    @Basic(optional = false)
    @Column(name = "ROL", nullable = false, length = 20)
    private String rol;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^[AI]$", message = "El estado debe ser A (Activo) o I (Inactivo)")
    @Basic(optional = false)
    @Column(name = "ESTADO", nullable = false, length = 1)
    private String estado;

    @Basic(optional = false)
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_ULTIMO_ACCESO")
    private LocalDateTime fechaUltimoAcceso;

    @Column(name = "TOKEN_SESION", length = 500)
    private String tokenSesion;



    // Constructores
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = "A"; // Activo por defecto
    }

    public Usuario(Long id) {
        this();
        this.idUsuario = id;
    }
    
    public Usuario(UsuarioDto usuarioDto) {
        this();
        this.idUsuario = usuarioDto.getIdUsuario();
        actualizar(usuarioDto);
    }

    public void actualizar(UsuarioDto usuarioDto) {
        this.usuario = usuarioDto.getUsuario();
        this.rol = usuarioDto.getRol();
        this.estado = usuarioDto.isActivo() ? "A" : "I";
        
        // Solo actualizar contraseña si se proporciona una nueva
        if (usuarioDto.getNuevaContrasena() != null && !usuarioDto.getNuevaContrasena().trim().isEmpty()) {
            this.contrasena = usuarioDto.getNuevaContrasena();
        }
    }

    // Métodos de conveniencia
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "A";
        }
    }

    /**
     * Verifica si el usuario está activo
     */
    public boolean isActivo() {
        return "A".equals(estado);
    }

    /**
     * Activa o desactiva el usuario
     */
    public void setActivo(boolean activo) {
        this.estado = activo ? "A" : "I";
    }



    /**
     * Verifica si el usuario tiene el rol especificado
     */
    public boolean tieneRol(String rol) {
        return this.rol != null && this.rol.equals(rol);
    }

    /**
     * Verifica si el usuario es administrador
     */
    public boolean isAdministrador() {
        return tieneRol("ADMINISTRADOR");
    }

    /**
     * Verifica si el usuario es cajero
     */
    public boolean isCajero() {
        return tieneRol("CAJERO");
    }

    /**
     * Verifica si el usuario es salonero
     */
    public boolean isSalonero() {
        return tieneRol("SALONERO");
    }

    // Getters y Setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }



    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaUltimoAcceso() {
        return fechaUltimoAcceso;
    }

    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }

    public String getTokenSesion() {
        return tokenSesion;
    }

    public void setTokenSesion(String tokenSesion) {
        this.tokenSesion = tokenSesion;
    }



    // hashCode, equals y toString
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsuario != null ? idUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        return (this.idUsuario != null || other.idUsuario == null) && 
               (this.idUsuario == null || this.idUsuario.equals(other.idUsuario));
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", usuario='" + usuario + '\'' +
                ", rol='" + rol + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}