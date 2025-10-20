package cr.ac.una.wsrestuna.model;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Usuario
 * Excluye información sensible como contraseñas y tokens
 */
public class UsuarioDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonbProperty("idUsuario")
    private Long idUsuario;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 50, message = "El usuario no puede exceder 50 caracteres")
    @JsonbProperty("usuario")
    private String usuario;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @JsonbProperty("nombre")
    private String nombre;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(ADMINISTRADOR|CAJERO|SALONERO)$", message = "El rol debe ser ADMINISTRADOR, CAJERO o SALONERO")
    @JsonbProperty("rol")
    private String rol;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^[AI]$", message = "El estado debe ser A (Activo) o I (Inactivo)")
    @JsonbProperty("estado")
    private String estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoAcceso;

    // Campo para nueva contraseña (solo para creación/actualización)
    @Size(min = 6, max = 255, message = "La contraseña debe tener entre 6 y 255 caracteres")
    private String nuevaContrasena;

    // Campos de control de modificación (siguiendo patrón plantilla)
    private Boolean modificado;
    private LocalDateTime fecha;
    private String token;

    // Constructores
    public UsuarioDto() {
        this.modificado = false;
        this.fecha = LocalDateTime.now();
    }

    public UsuarioDto(Usuario usuario) {
        this();
        if (usuario != null) {
            this.idUsuario = usuario.getIdUsuario();
            this.usuario = usuario.getUsuario();
            this.nombre = usuario.getNombre();  // Agregar el nombre
            this.rol = usuario.getRol();
            this.estado = usuario.getEstado();
            this.fechaCreacion = usuario.getFechaCreacion();
            this.fechaUltimoAcceso = usuario.getFechaUltimoAcceso();
            // NO incluimos la contraseña por seguridad
        }
    }

    // Métodos de conveniencia
    public boolean isActivo() {
        return "A".equals(estado);
    }

    public void setActivo(boolean activo) {
        this.estado = activo ? "A" : "I";
    }

    public boolean tieneRol(String rol) {
        return this.rol != null && this.rol.equals(rol);
    }

    public boolean isAdministrador() {
        return tieneRol("ADMINISTRADOR");
    }

    public boolean isCajero() {
        return tieneRol("CAJERO");
    }

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
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
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



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UsuarioDto{" +
                "idUsuario=" + idUsuario +
                ", usuario='" + usuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", rol='" + rol + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}