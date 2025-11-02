package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Entidad JPA para grupos/categorías de productos
 * Mapea la tabla GRUPO_PRODUCTO
 */
@Entity
@Table(name = "GRUPO_PRODUCTO", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "GrupoProducto.findAll", query = "SELECT g FROM GrupoProducto g ORDER BY g.ordenVisualizacion, g.nombre"),
    @NamedQuery(name = "GrupoProducto.findActivos", query = "SELECT g FROM GrupoProducto g WHERE g.estado = 'A' ORDER BY g.ordenVisualizacion, g.nombre"),
    @NamedQuery(name = "GrupoProducto.findById", query = "SELECT g FROM GrupoProducto g WHERE g.idGrupoProducto = :idGrupoProducto"),
    @NamedQuery(name = "GrupoProducto.findByNombre", query = "SELECT g FROM GrupoProducto g WHERE UPPER(g.nombre) LIKE UPPER(:nombre)"),
    @NamedQuery(name = "GrupoProducto.findAccesoRapido", query = "SELECT g FROM GrupoProducto g WHERE g.accesoRapido = 'S' AND g.estado = 'A' ORDER BY g.ordenVisualizacion"),
    @NamedQuery(name = "GrupoProducto.findMasVendidos", query = "SELECT g FROM GrupoProducto g WHERE g.estado = 'A' ORDER BY g.cantidadVendida DESC")
})
public class GrupoProducto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grupo_producto_seq")
    @SequenceGenerator(name = "grupo_producto_seq", sequenceName = "SEQ_GRUPO_PRODUCTO", allocationSize = 1)
    @Column(name = "ID_GRUPO_PRODUCTO")
    private Long idGrupoProducto;
    
    @NotNull(message = "El nombre del grupo es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    @Column(name = "NOMBRE", length = 80, nullable = false, unique = true)
    private String nombre;
    
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;
    
    @NotNull(message = "Debe indicar si tiene acceso rápido")
    @Pattern(regexp = "^(S|N)$", message = "Acceso rápido debe ser S o N")
    @Column(name = "ACCESO_RAPIDO", length = 1, nullable = false)
    private String accesoRapido;
    
    @NotNull(message = "El orden de visualización es obligatorio")
    @Column(name = "ORDEN_VISUALIZACION", nullable = false)
    private Long ordenVisualizacion;
    
    @Column(name = "CANTIDAD_VENDIDA", nullable = false)
    private Long cantidadVendida;
    
    @NotNull(message = "El estado es obligatorio")
    @Pattern(regexp = "^(A|I)$", message = "El estado debe ser A (Activo) o I (Inactivo)")
    @Column(name = "ESTADO", length = 1, nullable = false)
    private String estado;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private Date fechaCreacion;
    
    @Transient
    private Boolean modificado;
    
    public GrupoProducto() {
        this.fechaCreacion = new Date();
        this.estado = "A";
        this.accesoRapido = "N";
        this.cantidadVendida = 0L;
        this.ordenVisualizacion = 0L;
        this.modificado = false;
    }
    
    public GrupoProducto(GrupoProductoDto dto) {
        this();
        this.idGrupoProducto = dto.getIdGrupoProducto();
        actualizar(dto);
    }
    
    public void actualizar(GrupoProductoDto dto) {
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.accesoRapido = dto.getAccesoRapido();
        this.ordenVisualizacion = dto.getOrdenVisualizacion();
        this.estado = dto.getEstado();
        this.modificado = true;
    }
    
    // Métodos de utilidad
    public boolean isActivo() {
        return "A".equals(this.estado);
    }
    
    public boolean tieneAccesoRapido() {
        return "S".equals(this.accesoRapido);
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

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idGrupoProducto != null ? idGrupoProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof GrupoProducto)) {
            return false;
        }
        GrupoProducto other = (GrupoProducto) object;
        return !((this.idGrupoProducto == null && other.idGrupoProducto != null) || 
                 (this.idGrupoProducto != null && !this.idGrupoProducto.equals(other.idGrupoProducto)));
    }

    @Override
    public String toString() {
        return "GrupoProducto[ idGrupoProducto=" + idGrupoProducto + ", nombre=" + nombre + " ]";
    }
}
