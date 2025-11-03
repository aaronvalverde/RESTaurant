package cr.ac.una.restuna.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.Serializable;
import javafx.beans.property.*;


public class MesaDto extends RecursiveTreeObject<MesaDto> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private LongProperty idMesa;
    private LongProperty idSeccion;
    private StringProperty numeroMesa;
    private IntegerProperty capacidad;
    private DoubleProperty posicionX;
    private DoubleProperty posicionY;
    private StringProperty estado;
    private StringProperty nombreSeccion;
    private Boolean modificado;

    public MesaDto() {
        this.idMesa = new SimpleLongProperty();
        this.idSeccion = new SimpleLongProperty();
        this.numeroMesa = new SimpleStringProperty();
        this.capacidad = new SimpleIntegerProperty(4);
        this.posicionX = new SimpleDoubleProperty(0.0);
        this.posicionY = new SimpleDoubleProperty(0.0);
        this.estado = new SimpleStringProperty("LIBRE");
        this.nombreSeccion = new SimpleStringProperty();
        this.modificado = false;
    }

    
    public void setIdMesa(Long idMesa) {
        this.idMesa.set(idMesa);
    }

    public void setIdSeccion(Long idSeccion) {
        this.idSeccion.set(idSeccion);
    }

    public void setNumeroMesa(String numeroMesa) {
        this.numeroMesa.set(numeroMesa);
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad.set(capacidad);
    }

    public void setPosicionX(Double posicionX) {
        this.posicionX.set(posicionX);
    }

    public void setPosicionY(Double posicionY) {
        this.posicionY.set(posicionY);
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public void setNombreSeccion(String nombreSeccion) {
        this.nombreSeccion.set(nombreSeccion);
    }

    
    public Long getIdMesa() {
        return idMesa.get();
    }

    public Long getIdSeccion() {
        return idSeccion.get();
    }

    public String getNumeroMesa() {
        return numeroMesa.get();
    }

    public Integer getCapacidad() {
        return capacidad.get();
    }

    public Double getPosicionX() {
        return posicionX.get();
    }

    public Double getPosicionY() {
        return posicionY.get();
    }

    public String getEstado() {
        return estado.get();
    }

    public String getNombreSeccion() {
        return nombreSeccion.get();
    }

    
    public LongProperty idMesaProperty() {
        return idMesa;
    }

    public LongProperty idSeccionProperty() {
        return idSeccion;
    }

    public StringProperty numeroMesaProperty() {
        return numeroMesa;
    }

    public IntegerProperty capacidadProperty() {
        return capacidad;
    }

    public DoubleProperty posicionXProperty() {
        return posicionX;
    }

    public DoubleProperty posicionYProperty() {
        return posicionY;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public StringProperty nombreSeccionProperty() {
        return nombreSeccion;
    }
    
    public Boolean getModificado() {
        return modificado;
    }
    
    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }
    
    
    public boolean isLibre() {
        return "LIBRE".equals(getEstado());
    }
    
    public boolean isOcupada() {
        return "OCUPADA".equals(getEstado());
    }
    
    public boolean isReservada() {
        return "RESERVADA".equals(getEstado());
    }
    
    public boolean isFueraServicio() {
        return "FUERA_SERVICIO".equals(getEstado());
    }
    
    public boolean isDisponible() {
        return isLibre() || isReservada();
    }
    
    @Override
    public String toString() {
        return numeroMesa.get() + " - " + nombreSeccion.get() + " (" + estado.get() + ")";
    }
}
