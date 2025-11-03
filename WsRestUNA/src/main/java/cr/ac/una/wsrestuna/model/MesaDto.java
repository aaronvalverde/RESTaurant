package cr.ac.una.wsrestuna.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * DTO para transferencia de datos de mesas
 */
@Schema(description = "Mesa dentro de una sección")
public class MesaDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "Identificador de la mesa", example = "15")
    private Long idMesa;
    @Schema(description = "Identificador de la sección a la que pertenece", example = "2")
    private Long idSeccion;
    @Schema(description = "Número o nombre de la mesa", example = "M01")
    private String numeroMesa;
    @Schema(description = "Capacidad de la mesa", example = "4")
    private Integer capacidad;
    @Schema(description = "Posición X en el plano", example = "120.5")
    private Double posicionX;
    @Schema(description = "Posición Y en el plano", example = "80.0")
    private Double posicionY;
    @Schema(description = "Estado de la mesa", example = "LIBRE")
    private String estado;
    @Schema(description = "Nombre de la sección para visualización", example = "Terraza")
    private String nombreSeccion; // Para mostrar en cliente
    @Schema(description = "Indica si el registro fue modificado", example = "false")
    private Boolean modificado;
    
    public MesaDto() {
        this.modificado = false;
    }
    
    public MesaDto(Mesa mesa) {
        this();
        this.idMesa = mesa.getIdMesa();
        this.numeroMesa = mesa.getNumeroMesa();
        this.capacidad = mesa.getCapacidad();
        this.estado = mesa.getEstado();
        
        if (mesa.getPosicionX() != null) {
            this.posicionX = mesa.getPosicionX().doubleValue();
        }
        if (mesa.getPosicionY() != null) {
            this.posicionY = mesa.getPosicionY().doubleValue();
        }
        
        if (mesa.getSeccion() != null) {
            this.idSeccion = mesa.getSeccion().getIdSeccion();
            this.nombreSeccion = mesa.getSeccion().getNombre();
        }
        
        this.modificado = mesa.getModificado();
    }

    // Getters y Setters
    public Long getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(Long idMesa) {
        this.idMesa = idMesa;
    }

    public Long getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(Long idSeccion) {
        this.idSeccion = idSeccion;
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

    public Double getPosicionX() {
        return posicionX;
    }

    public void setPosicionX(Double posicionX) {
        this.posicionX = posicionX;
    }

    public Double getPosicionY() {
        return posicionY;
    }

    public void setPosicionY(Double posicionY) {
        this.posicionY = posicionY;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreSeccion() {
        return nombreSeccion;
    }

    public void setNombreSeccion(String nombreSeccion) {
        this.nombreSeccion = nombreSeccion;
    }

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

    @Override
    public String toString() {
        return "MesaDto{" +
                "idMesa=" + idMesa +
                ", numeroMesa='" + numeroMesa + '\'' +
                ", seccion='" + nombreSeccion + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
