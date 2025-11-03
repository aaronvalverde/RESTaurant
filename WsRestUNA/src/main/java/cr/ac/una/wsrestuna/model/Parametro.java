package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "PARAMETRO", schema = "RESTUNA")
@NamedQueries({
    @NamedQuery(name = "Parametro.findAll", query = "SELECT p FROM Parametro p ORDER BY p.clave"),
    @NamedQuery(name = "Parametro.findByUsuario", 
                query = "SELECT p FROM Parametro p WHERE p.usuario.idUsuario = :idUsuario ORDER BY p.clave"),
    @NamedQuery(name = "Parametro.findByUsuarioAndClave", 
                query = "SELECT p FROM Parametro p WHERE p.usuario.idUsuario = :idUsuario AND p.clave = :clave")
})
public class Parametro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "PARAMETRO_ID_GENERATOR", sequenceName = "SEQ_PARAMETRO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARAMETRO_ID_GENERATOR")
    @Basic(optional = false)
    @Column(name = "ID_PARAMETRO")
    private Long idParametro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false, foreignKey = @ForeignKey(name = "FK_PARAMETRO_USUARIO"))
    private Usuario usuario;

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 50, message = "La clave no puede exceder 50 caracteres")
    @Basic(optional = false)
    @Column(name = "CLAVE", nullable = false, length = 50)
    private String clave;

    @NotBlank(message = "El valor es obligatorio")
    @Size(max = 200, message = "El valor no puede exceder 200 caracteres")
    @Basic(optional = false)
    @Column(name = "VALOR", nullable = false, length = 200)
    private String valor;

    @Size(max = 300, message = "La descripción no puede exceder 300 caracteres")
    @Column(name = "DESCRIPCION", length = 300)
    private String descripcion;

    @Pattern(regexp = "^(STRING|NUMBER|BOOLEAN|DATE)$", message = "El tipo de dato debe ser STRING, NUMBER, BOOLEAN o DATE")
    @Basic(optional = false)
    @Column(name = "TIPO_DATO", nullable = false, length = 20)
    private String tipoDato;

    @Basic(optional = false)
    @Column(name = "FECHA_MODIFICACION", nullable = false)
    private LocalDate fechaModificacion;

    
    public Parametro() {
        this.fechaModificacion = LocalDate.now();
        this.tipoDato = "STRING"; 
    }

    public Parametro(Long id) {
        this();
        this.idParametro = id;
    }

    public Parametro(ParametroDto parametroDto) {
        this();
        this.idParametro = parametroDto.getIdParametro();
        actualizar(parametroDto);
    }

    public void actualizar(ParametroDto parametroDto) {
        this.clave = parametroDto.getClave();
        this.valor = parametroDto.getValor();
        this.descripcion = parametroDto.getDescripcion();
        this.tipoDato = parametroDto.getTipoDato() != null ? parametroDto.getTipoDato() : "STRING";
        this.fechaModificacion = LocalDate.now();
        
        
        if (parametroDto.getIdUsuario() != null && this.usuario == null) {
            this.usuario = new Usuario(parametroDto.getIdUsuario());
        }
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDate.now();
        if (this.tipoDato == null) {
            this.tipoDato = "STRING";
        }
    }

    
    public Long getIdParametro() {
        return idParametro;
    }

    public void setIdParametro(Long idParametro) {
        this.idParametro = idParametro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public LocalDate getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDate fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idParametro != null ? idParametro.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Parametro)) {
            return false;
        }
        Parametro other = (Parametro) object;
        return (this.idParametro != null || other.idParametro == null) && 
               (this.idParametro == null || this.idParametro.equals(other.idParametro));
    }

    @Override
    public String toString() {
        return "Parametro{" +
                "idParametro=" + idParametro +
                ", clave='" + clave + '\'' +
                ", valor='" + valor + '\'' +
                ", tipoDato='" + tipoDato + '\'' +
                '}';
    }
}
