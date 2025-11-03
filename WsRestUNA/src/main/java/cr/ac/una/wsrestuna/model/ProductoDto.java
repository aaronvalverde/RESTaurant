package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Schema(description = "Producto del menú del restaurante")
public class ProductoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Identificador del producto", example = "1")
    @JsonbProperty("idProducto")
    private Long idProducto;

    @NotNull(message = "El grupo de producto es obligatorio")
    @Schema(description = "Identificador del grupo al que pertenece", example = "1", required = true)
    @JsonbProperty("idGrupoProducto")
    private Long idGrupoProducto;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
    @Schema(description = "Nombre completo del producto", example = "Café Americano Grande", required = true)
    @JsonbProperty("nombre")
    private String nombre;

    @NotBlank(message = "El nombre corto es obligatorio")
    @Size(max = 30, message = "El nombre corto no puede exceder 30 caracteres")
    @Schema(description = "Nombre abreviado para tickets", example = "CAF AMER GDE", required = true)
    @JsonbProperty("nombreCorto")
    private String nombreCorto;

    @Size(max = 300, message = "La descripción no puede exceder 300 caracteres")
    @Schema(description = "Descripción detallada del producto", example = "Café americano de 12 oz con opción de leche")
    @JsonbProperty("descripcion")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio del producto en colones", example = "2500.00", required = true)
    @JsonbProperty("precio")
    private BigDecimal precio;

    @NotBlank(message = "El acceso rápido es obligatorio")
    @Pattern(regexp = "^[SN]$", message = "El acceso rápido debe ser S o N")
    @Schema(description = "Indica si aparece en acceso rápido", allowableValues = {"S", "N"}, example = "S", required = true)
    @JsonbProperty("accesoRapido")
    private String accesoRapido;

    @Schema(description = "Cantidad total vendida del producto", example = "150")
    @JsonbProperty("cantidadVendida")
    private Long cantidadVendida;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^[AI]$", message = "El estado debe ser A (Activo) o I (Inactivo)")
    @Schema(description = "Estado del producto", allowableValues = {"A", "I"}, example = "A", required = true)
    @JsonbProperty("estado")
    private String estado;

    @Schema(description = "Fecha de creación del producto", example = "2025-10-31T15:30:00")
    @JsonbProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;

    
    @Schema(description = "Nombre del grupo al que pertenece", example = "Bebidas Calientes")
    @JsonbProperty("nombreGrupo")
    private String nombreGrupo;

    
    public ProductoDto() {
        this.cantidadVendida = 0L;
        this.estado = "A";
        this.accesoRapido = "N";
    }

    public ProductoDto(Producto producto) {
        this();
        if (producto != null) {
            this.idProducto = producto.getIdProducto();
            this.nombre = producto.getNombre();
            this.nombreCorto = producto.getNombreCorto();
            this.descripcion = producto.getDescripcion();
            this.precio = producto.getPrecio();
            this.accesoRapido = producto.getAccesoRapido();
            this.cantidadVendida = producto.getCantidadVendida();
            this.estado = producto.getEstado();
            this.fechaCreacion = producto.getFechaCreacion();
            
            
            if (producto.getIdGrupoProducto() != null) {
                this.idGrupoProducto = producto.getIdGrupoProducto().getIdGrupoProducto();
                this.nombreGrupo = producto.getIdGrupoProducto().getNombre();
            }
        }
    }

    
    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
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

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    @Override
    public String toString() {
        return "ProductoDto{" +
                "idProducto=" + idProducto +
                ", idGrupoProducto=" + idGrupoProducto +
                ", nombre='" + nombre + '\'' +
                ", nombreCorto='" + nombreCorto + '\'' +
                ", precio=" + precio +
                ", estado='" + estado + '\'' +
                '}';
    }
}
