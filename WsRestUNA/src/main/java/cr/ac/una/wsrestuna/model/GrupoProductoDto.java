package cr.ac.una.wsrestuna.model;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO para transferencia de datos de GrupoProducto
 * POJO simple sin dependencias JavaFX
 */
public class GrupoProductoDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long idGrupoProducto;
    private String nombre;
    private String descripcion;
    private String accesoRapido;
    private Long ordenVisualizacion;
    private Long cantidadVendida;
    private String estado;
    private Date fechaCreacion;
    
    public GrupoProductoDto() {
    }
    
    public GrupoProductoDto(GrupoProducto grupo) {
        this.idGrupoProducto = grupo.getIdGrupoProducto();
        this.nombre = grupo.getNombre();
        this.descripcion = grupo.getDescripcion();
        this.accesoRapido = grupo.getAccesoRapido();
        this.ordenVisualizacion = grupo.getOrdenVisualizacion();
        this.cantidadVendida = grupo.getCantidadVendida();
        this.estado = grupo.getEstado();
        this.fechaCreacion = grupo.getFechaCreacion();
    }

    // Getters y Setters
    public Long getIdGrupoProducto() {
        return idGrupoProducto;
    }

    public void setIdGrupoProducto(Long idGrupoProducto) {
        this.idGrupoProducto = idGrupoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAccesoRapido() {
        return accesoRapido;
    }

    public void setAccesoRapido(String accesoRapido) {
        this.accesoRapido = accesoRapido;
    }

    public Long getOrdenVisualizacion() {
        return ordenVisualizacion;
    }

    public void setOrdenVisualizacion(Long ordenVisualizacion) {
        this.ordenVisualizacion = ordenVisualizacion;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "GrupoProductoDto{" +
                "idGrupoProducto=" + idGrupoProducto +
                ", nombre='" + nombre + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
