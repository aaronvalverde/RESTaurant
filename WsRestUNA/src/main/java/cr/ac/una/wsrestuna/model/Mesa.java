package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entidad JPA para mesas de los salones del restaurante
 * Mapea la tabla MESA
 */
@Entity
@Table(name = "MESA", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "Mesa.findAll", query = "SELECT m FROM Mesa m ORDER BY m.seccion.nombre, m.numeroMesa"),
    @NamedQuery(name = "Mesa.findBySeccion", query = "SELECT m FROM Mesa m WHERE m.seccion.idSeccion = :idSeccion ORDER BY m.numeroMesa"),
    @NamedQuery(name = "Mesa.findByEstado", query = "SELECT m FROM Mesa m WHERE m.estado = :estado ORDER BY m.seccion.nombre, m.numeroMesa"),
    @NamedQuery(name = "Mesa.findLibres", query = "SELECT m FROM Mesa m WHERE m.estado = 'LIBRE' ORDER BY m.seccion.nombre, m.numeroMesa"),
    @NamedQuery(name = "Mesa.findBySeccionEstado", query = "SELECT m FROM Mesa m WHERE m.seccion.idSeccion = :idSeccion AND m.estado = :estado ORDER BY m.numeroMesa")
})
public class Mesa implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mesa_seq")
    @SequenceGenerator(name = "mesa_seq", sequenceName = "SEQ_MESA", allocationSize = 1)
    @Column(name = "ID_MESA")
    private Long idMesa;
    
    @NotNull(message = "El número de mesa es obligatorio")
    @Size(min = 1, max = 10, message = "El número de mesa debe tener entre 1 y 10 caracteres")
    @Column(name = "NUMERO_MESA", length = 10, nullable = false)
    private String numeroMesa;
    
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es 1 persona")
    @Max(value = 20, message = "La capacidad máxima es 20 personas")
    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;
    
    @Column(name = "POSICION_X", precision = 5, scale = 2)
    private BigDecimal posicionX;
    
    @Column(name = "POSICION_Y", precision = 5, scale = 2)
    private BigDecimal posicionY;
    
    @NotNull(message = "El estado es obligatorio")
    @Pattern(regexp = "^(LIBRE|OCUPADA|RESERVADA|FUERA_SERVICIO)$", 
             message = "El estado debe ser LIBRE, OCUPADA, RESERVADA o FUERA_SERVICIO")
    @Column(name = "ESTADO", length = 20, nullable = false)
    private String estado;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION", nullable = false)
    private Date fechaCreacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SECCION", nullable = false)
    @NotNull(message = "La sección es obligatoria")
    private Seccion seccion;
    
    @Transient
    private Boolean modificado;
    
    public Mesa() {
        this.fechaCreacion = new Date();
        this.estado = "LIBRE";
        this.capacidad = 4; // Capacidad por defecto
        this.modificado = false;
    }
    
    public Mesa(MesaDto dto) {
        this();
        this.idMesa = dto.getIdMesa();
        actualizar(dto);
    }
    
    public void actualizar(MesaDto dto) {
        this.numeroMesa = dto.getNumeroMesa();
        this.capacidad = dto.getCapacidad();
        this.estado = dto.getEstado();
        
        if (dto.getPosicionX() != null) {
            this.posicionX = BigDecimal.valueOf(dto.getPosicionX());
        }
        if (dto.getPosicionY() != null) {
            this.posicionY = BigDecimal.valueOf(dto.getPosicionY());
        }
        
        this.modificado = true;
    }
    
    // Métodos de utilidad
    public boolean isLibre() {
        return "LIBRE".equals(this.estado);
    }
    
    public boolean isOcupada() {
        return "OCUPADA".equals(this.estado);
    }
    
    public boolean isReservada() {
        return "RESERVADA".equals(this.estado);
    }
    
    public boolean isFueraServicio() {
        return "FUERA_SERVICIO".equals(this.estado);
    }
    
    public boolean isDisponible() {
        return isLibre() || isReservada();
    }

    // Getters y Setters
    public Long getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(Long idMesa) {
        this.idMesa = idMesa;
    }

    public String getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(String numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public BigDecimal getPosicionX() {
        return posicionX;
    }

    public void setPosicionX(BigDecimal posicionX) {
        this.posicionX = posicionX;
    }

    public BigDecimal getPosicionY() {
        return posicionY;
    }

    public void setPosicionY(BigDecimal posicionY) {
        this.posicionY = posicionY;
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

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
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
        hash += (idMesa != null ? idMesa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Mesa)) {
            return false;
        }
        Mesa other = (Mesa) object;
        return !((this.idMesa == null && other.idMesa != null) || 
                 (this.idMesa != null && !this.idMesa.equals(other.idMesa)));
    }

    @Override
    public String toString() {
        return "Mesa[ idMesa=" + idMesa + ", numero=" + numeroMesa + 
               ", seccion=" + (seccion != null ? seccion.getNombre() : "null") + 
               ", estado=" + estado + " ]";
    }
}
