package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * Entidad JPA para secciones/salones del restaurante
 * Mapea la tabla SECCION
 */
@Entity
@Table(name = "SECCION")
@NamedQueries({
    @NamedQuery(name = "Seccion.findAll", query = "SELECT s FROM Seccion s ORDER BY s.nombre"),
    @NamedQuery(name = "Seccion.findActivas", query = "SELECT s FROM Seccion s WHERE s.estado = 'A' ORDER BY s.nombre"),
    @NamedQuery(name = "Seccion.findById", query = "SELECT s FROM Seccion s WHERE s.idSeccion = :idSeccion"),
    @NamedQuery(name = "Seccion.findByNombre", query = "SELECT s FROM Seccion s WHERE UPPER(s.nombre) LIKE UPPER(:nombre)"),
    @NamedQuery(name = "Seccion.findByTipo", query = "SELECT s FROM Seccion s WHERE s.tipo = :tipo AND s.estado = 'A'")
})
public class Seccion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seccion_seq")
    @SequenceGenerator(name = "seccion_seq", sequenceName = "SEQ_SECCION", allocationSize = 1)
    @Column(name = "ID_SECCION")
    private Long idSeccion;
    
    @NotNull(message = "El nombre de la sección es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    @Column(name = "NOMBRE", length = 80, nullable = false, unique = true)
    private String nombre;
    
    @NotNull(message = "El tipo de sección es obligatorio")
    @Pattern(regexp = "^(SALON|BARRA|TERRAZA)$", message = "El tipo debe ser SALON, BARRA o TERRAZA")
    @Column(name = "TIPO", length = 15, nullable = false)
    private String tipo;
    
    @NotNull(message = "Debe indicar si cobra impuesto")
    @Pattern(regexp = "^(S|N)$", message = "Cobra impuesto debe ser S o N")
    @Column(name = "COBRA_IMPUESTO", length = 1, nullable = false)
    private String cobraImpuesto;
    
    @NotNull(message = "El estado es obligatorio")
    @Pattern(regexp = "^(A|I)$", message = "El estado debe ser A (Activo) o I (Inactivo)")
    @Column(name = "ESTADO", length = 1, nullable = false)
    private String estado;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private Date fechaCreacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ARCHIVO_IMAGEN", referencedColumnName = "ID_ARCHIVO")
    private Archivo archivoImagen;
    
    // @Version - Comentado porque la columna VERSION no existe en la tabla SECCION
    // @Column(name = "VERSION")
    // private Long version;
    
    @Transient
    private Boolean modificado;
    
    public Seccion() {
        this.fechaCreacion = new Date();
        this.estado = "A";
        this.cobraImpuesto = "N";
        this.modificado = false;
    }
    
    public Seccion(SeccionDto dto) {
        this();
        this.idSeccion = dto.getIdSeccion();
        actualizar(dto);
    }
    
    public void actualizar(SeccionDto dto) {
        this.nombre = dto.getNombre();
        this.tipo = dto.getTipo();
        this.cobraImpuesto = dto.getCobraImpuesto();
        this.estado = dto.getEstado();
        
        // El archivo imagen se maneja por separado en el servicio
        this.modificado = true;
    }
    
    // Métodos de utilidad
    public boolean isActiva() {
        return "A".equals(this.estado);
    }
    
    public boolean cobraImpuesto() {
        return "S".equals(this.cobraImpuesto);
    }
    
    public boolean isSalon() {
        return "SALON".equals(this.tipo);
    }
    
    public boolean isBarra() {
        return "BARRA".equals(this.tipo);
    }

    // Getters y Setters
    public Long getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(Long idSeccion) {
        this.idSeccion = idSeccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCobraImpuesto() {
        return cobraImpuesto;
    }

    public void setCobraImpuesto(String cobraImpuesto) {
        this.cobraImpuesto = cobraImpuesto;
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

    public Archivo getArchivoImagen() {
        return archivoImagen;
    }

    public void setArchivoImagen(Archivo archivoImagen) {
        this.archivoImagen = archivoImagen;
    }

    // Getters/Setters de version comentados porque el campo no existe en BD
    // public Long getVersion() {
    //     return version;
    // }
    //
    // public void setVersion(Long version) {
    //     this.version = version;
    // }

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSeccion != null ? idSeccion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Seccion)) {
            return false;
        }
        Seccion other = (Seccion) object;
        return !((this.idSeccion == null && other.idSeccion != null) || 
                 (this.idSeccion != null && !this.idSeccion.equals(other.idSeccion)));
    }

    @Override
    public String toString() {
        return "Seccion[ idSeccion=" + idSeccion + ", nombre=" + nombre + ", tipo=" + tipo + " ]";
    }
}
