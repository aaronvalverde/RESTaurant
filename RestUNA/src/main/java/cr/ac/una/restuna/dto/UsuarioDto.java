package cr.ac.una.restuna.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Usuario
 * Replica la estructura del DTO del servidor
 */
public class UsuarioDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUsuario;
    private String usuario;
    private String nombre;
    private String rol;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoAcceso;
    private String nuevaContrasena;

    // Constructores
    public UsuarioDto() {
    }

    public UsuarioDto(String usuario, String rol) {
        this.usuario = usuario;
        this.rol = rol;
        this.estado = "A"; // Activo por defecto
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

    @Override
    public String toString() {
        return "UsuarioDto{" +
                "idUsuario=" + idUsuario +
                ", usuario='" + usuario + '\'' +
                ", rol='" + rol + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}