package cr.ac.una.wsrestuna.model;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO para transferir datos de secciones/salones
 * Incluye la imagen asociada opcionalmente
 */
public class SeccionDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long idSeccion;
    private String nombre;
    private String tipo;
    private String cobraImpuesto;
    private String estado;
    private Date fechaCreacion;
    private Long idArchivoImagen;
    private ArchivoDto imagen;
    private Boolean modificado;
    
    public SeccionDto() {
    }
    
    /**
     * Constructor desde entidad Seccion
     * @param seccion Entidad Seccion
     * @param incluirImagen Si se debe incluir los datos de la imagen
     */
    public SeccionDto(Seccion seccion, boolean incluirImagen) {
        this.idSeccion = seccion.getIdSeccion();
        this.nombre = seccion.getNombre();
        this.tipo = seccion.getTipo();
        this.cobraImpuesto = seccion.getCobraImpuesto();
        this.estado = seccion.getEstado();
        this.fechaCreacion = seccion.getFechaCreacion();
        this.modificado = seccion.getModificado();
        
        if (seccion.getArchivoImagen() != null) {
            this.idArchivoImagen = seccion.getArchivoImagen().getIdArchivo();
            
            // Solo incluir los datos completos de la imagen si se solicita
            if (incluirImagen) {
                this.imagen = new ArchivoDto(seccion.getArchivoImagen(), true);
            } else {
                // Solo incluir metadata sin el contenido para optimizar
                this.imagen = new ArchivoDto(seccion.getArchivoImagen(), false);
            }
        }
    }
    
    /**
     * Constructor para listados (sin contenido de imagen)
     */
    public SeccionDto(Seccion seccion) {
        this(seccion, false);
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

    public Long getIdArchivoImagen() {
        return idArchivoImagen;
    }

    public void setIdArchivoImagen(Long idArchivoImagen) {
        this.idArchivoImagen = idArchivoImagen;
    }

    public ArchivoDto getImagen() {
        return imagen;
    }

    public void setImagen(ArchivoDto imagen) {
        this.imagen = imagen;
    }

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

    @Override
    public String toString() {
        return "SeccionDto{" +
                "idSeccion=" + idSeccion +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", cobraImpuesto='" + cobraImpuesto + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
