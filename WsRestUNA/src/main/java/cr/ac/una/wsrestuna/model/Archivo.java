package cr.ac.una.wsrestuna.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Entidad JPA para almacenar archivos (imágenes) en la base de datos
 * Mapea la tabla ARCHIVO
 */
@Entity
@Table(name = "ARCHIVO")
@NamedQueries({
    @NamedQuery(name = "Archivo.findAll", query = "SELECT a FROM Archivo a"),
    @NamedQuery(name = "Archivo.findById", query = "SELECT a FROM Archivo a WHERE a.idArchivo = :idArchivo")
})
public class Archivo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "archivo_seq")
    @SequenceGenerator(name = "archivo_seq", sequenceName = "SEQ_ARCHIVO", allocationSize = 1)
    @Column(name = "ID_ARCHIVO")
    private Long idArchivo;
    
    @Column(name = "NOMBRE_ARCHIVO", length = 200, nullable = false)
    private String nombreArchivo;
    
    @Column(name = "TIPO_MIME", length = 100, nullable = false)
    private String tipoMime;
    
    @Column(name = "TAMANIO", nullable = false)
    private Long tamanio;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "CONTENIDO", nullable = false)
    private byte[] contenido;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_SUBIDA", nullable = false)
    private Date fechaSubida;
    
    // @Version - Comentado porque la tabla ARCHIVO no tiene columna VERSION en la BD
    // @Column(name = "VERSION")
    // private Long version;
    
    public Archivo() {
        this.fechaSubida = new Date();
    }
    
    public Archivo(ArchivoDto dto) {
        this.idArchivo = dto.getIdArchivo();
        actualizar(dto);
    }
    
    public void actualizar(ArchivoDto dto) {
        this.nombreArchivo = dto.getNombreArchivo();
        this.tipoMime = dto.getTipoMime();
        this.tamanio = dto.getTamanio();
        if (dto.getContenidoBase64() != null && !dto.getContenidoBase64().isEmpty()) {
            this.contenido = java.util.Base64.getDecoder().decode(dto.getContenidoBase64());
        }
        if (dto.getFechaSubida() != null) {
            this.fechaSubida = dto.getFechaSubida();
        } else if (this.fechaSubida == null) {
            // Asegurar que siempre tenga fecha
            this.fechaSubida = new Date();
        }
    }

    // Getters y Setters
    public Long getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(Long idArchivo) {
        this.idArchivo = idArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Long getTamanio() {
        return tamanio;
    }

    public void setTamanio(Long tamanio) {
        this.tamanio = tamanio;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
        if (contenido != null) {
            this.tamanio = (long) contenido.length;
        }
    }

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    // Getters/Setters de VERSION comentados - la columna no existe en la BD
    // public Long getVersion() {
    //     return version;
    // }

    // public void setVersion(Long version) {
    //     this.version = version;
    // }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idArchivo != null ? idArchivo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Archivo)) {
            return false;
        }
        Archivo other = (Archivo) object;
        return !((this.idArchivo == null && other.idArchivo != null) || 
                 (this.idArchivo != null && !this.idArchivo.equals(other.idArchivo)));
    }

    @Override
    public String toString() {
        return "Archivo[ idArchivo=" + idArchivo + ", nombre=" + nombreArchivo + " ]";
    }
}
