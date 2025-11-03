package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;


@Schema(description = "Grupo o categoría de productos del menú")
public class GrupoProductoDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "Identificador único del grupo", example = "1")
    private Long idGrupoProducto;
    @Schema(description = "Nombre del grupo", example = "Bebidas")
    private String nombre;
    @Schema(description = "Descripción del grupo", example = "Bebidas calientes y frías")
    private String descripcion;
    @Schema(description = "Indica si está disponible en acceso rápido", allowableValues = {"S", "N"}, example = "S")
    private String accesoRapido;
    @Schema(description = "Orden de visualización en el POS", example = "1")
    private Long ordenVisualizacion;
    @Schema(description = "Cantidad vendida acumulada", example = "120")
    private Long cantidadVendida;
    @Schema(description = "Estado del grupo", allowableValues = {"A", "I"}, example = "A")
    private String estado;
    @Schema(description = "Fecha de creación del grupo", example = "2024-05-30T00:00:00Z")
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
