package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla PRODUCTO
 */
@Entity
@Table(name = "PRODUCTO", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "Producto.findAll", 
                query = "SELECT p FROM Producto p ORDER BY p.idGrupoProducto.ordenVisualizacion, p.nombre"),
    @NamedQuery(name = "Producto.findActivos", 
                query = "SELECT p FROM Producto p WHERE p.estado = 'A' ORDER BY p.idGrupoProducto.ordenVisualizacion, p.nombre"),
    @NamedQuery(name = "Producto.findById", 
                query = "SELECT p FROM Producto p WHERE p.idProducto = :idProducto"),
    @NamedQuery(name = "Producto.findByNombre", 
                query = "SELECT p FROM Producto p WHERE UPPER(p.nombre) = UPPER(:nombre)"),
    @NamedQuery(name = "Producto.findByNombreCorto", 
                query = "SELECT p FROM Producto p WHERE UPPER(p.nombreCorto) = UPPER(:nombreCorto)"),
    @NamedQuery(name = "Producto.findByGrupo", 
                query = "SELECT p FROM Producto p WHERE p.idGrupoProducto.idGrupoProducto = :idGrupo ORDER BY p.nombre"),
    @NamedQuery(name = "Producto.findByGrupoActivos", 
                query = "SELECT p FROM Producto p WHERE p.idGrupoProducto.idGrupoProducto = :idGrupo AND p.estado = 'A' ORDER BY p.nombre"),
    @NamedQuery(name = "Producto.findAccesoRapido", 
                query = "SELECT p FROM Producto p WHERE p.accesoRapido = 'S' AND p.estado = 'A' ORDER BY p.cantidadVendida DESC"),
    @NamedQuery(name = "Producto.findMasVendidos", 
                query = "SELECT p FROM Producto p WHERE p.estado = 'A' ORDER BY p.cantidadVendida DESC")
})
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "SEQ_PRODUCTO", allocationSize = 1)
    @Column(name = "ID_PRODUCTO")
    private Long idProducto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_GRUPO_PRODUCTO", referencedColumnName = "ID_GRUPO_PRODUCTO")
    @NotNull(message = "El grupo de producto es obligatorio")
    private GrupoProducto idGrupoProducto;

    @Column(name = "NOMBRE", length = 120, nullable = false, unique = true)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
    private String nombre;

    @Column(name = "NOMBRE_CORTO", length = 30, nullable = false, unique = true)
    @NotBlank(message = "El nombre corto es obligatorio")
    @Size(max = 30, message = "El nombre corto no puede exceder 30 caracteres")
    private String nombreCorto;

    @Column(name = "DESCRIPCION", length = 300)
    @Size(max = 300, message = "La descripción no puede exceder 300 caracteres")
    private String descripcion;

    @Column(name = "PRECIO", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Column(name = "ACCESO_RAPIDO", length = 1, nullable = false)
    @NotBlank(message = "El acceso rápido es obligatorio")
    @Pattern(regexp = "^[SN]$", message = "El acceso rápido debe ser S o N")
    private String accesoRapido;

    @Column(name = "CANTIDAD_VENDIDA", nullable = false)
    private Long cantidadVendida;

    @Column(name = "ESTADO", length = 1, nullable = false)
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^[AI]$", message = "El estado debe ser A (Activo) o I (Inactivo)")
    private String estado;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Constructores
    public Producto() {
        this.cantidadVendida = 0L;
        this.estado = "A";
        this.accesoRapido = "N";
    }

    public Producto(ProductoDto dto) {
        this();
        if (dto != null) {
            this.idProducto = dto.getIdProducto();
            this.nombre = dto.getNombre();
            this.nombreCorto = dto.getNombreCorto();
            this.descripcion = dto.getDescripcion();
            this.precio = dto.getPrecio();
            this.accesoRapido = dto.getAccesoRapido();
            this.cantidadVendida = dto.getCantidadVendida() != null ? dto.getCantidadVendida() : 0L;
            this.estado = dto.getEstado();
            // El grupo se debe setear por separado ya que requiere una instancia de GrupoProducto
        }
    }

    /**
     * Actualiza los datos de este producto desde un DTO
     */
    public void actualizar(ProductoDto dto) {
        if (dto != null) {
            this.nombre = dto.getNombre();
            this.nombreCorto = dto.getNombreCorto();
            this.descripcion = dto.getDescripcion();
            this.precio = dto.getPrecio();
            this.accesoRapido = dto.getAccesoRapido();
            this.estado = dto.getEstado();
            // La cantidad vendida se actualiza por triggers, no manualmente
            // El grupo no se actualiza por seguridad
        }
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (cantidadVendida == null) {
            cantidadVendida = 0L;
        }
    }

    // Métodos de utilidad
    public boolean isActivo() {
        return "A".equals(this.estado);
    }

    public boolean tieneAccesoRapido() {
        return "S".equals(this.accesoRapido);
    }

    // Getters y Setters
    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public GrupoProducto getIdGrupoProducto() {
        return idGrupoProducto;
    }

    public void setIdGrupoProducto(GrupoProducto idGrupoProducto) {
        this.idGrupoProducto = idGrupoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getAccesoRapido() {
        return accesoRapido;
    }

    public void setAccesoRapido(String accesoRapido) {
        this.accesoRapido = accesoRapido;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProducto != null ? idProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.idProducto == null && other.idProducto != null) || 
            (this.idProducto != null && !this.idProducto.equals(other.idProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", nombreCorto='" + nombreCorto + '\'' +
                ", precio=" + precio +
                ", estado='" + estado + '\'' +
                '}';
    }
}
