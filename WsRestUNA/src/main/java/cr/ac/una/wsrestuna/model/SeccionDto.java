package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

/**
 * DTO para transferir datos de secciones/salones
 * Incluye la imagen asociada opcionalmente
 */
@Schema(description = "Sección o salón del restaurante")
public class SeccionDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "Identificador de la sección", example = "1")
    private Long idSeccion;
    @Schema(description = "Nombre de la sección", example = "Salón Principal")
    private String nombre;
    @Schema(description = "Tipo de sección", example = "SALON")
    private String tipo;
    @Schema(description = "Indica si cobra impuesto", allowableValues = {"S", "N"}, example = "S")
    private String cobraImpuesto;
    @Schema(description = "Estado de la sección", allowableValues = {"A", "I"}, example = "A")
    private String estado;
    @Schema(description = "Fecha de creación", example = "2024-05-30T00:00:00Z")
    private Date fechaCreacion;
    @Schema(description = "ID del archivo de imagen asociado", example = "10")
    private Long idArchivoImagen;
    @Schema(description = "Archivo de imagen asociado")
    private ArchivoDto imagen;
    @Schema(description = "Indica si el registro fue modificado", example = "false")
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
