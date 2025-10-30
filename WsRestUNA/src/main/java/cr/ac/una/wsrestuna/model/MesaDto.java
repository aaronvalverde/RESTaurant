package cr.ac.una.wsrestuna.model;

import java.io.Serializable;

/**
 * DTO para transferencia de datos de mesas
 */
public class MesaDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long idMesa;
    private Long idSeccion;
    private String numeroMesa;
    private Integer capacidad;
    private Double posicionX;
    private Double posicionY;
    private String estado;
    private String nombreSeccion; // Para mostrar en cliente
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
