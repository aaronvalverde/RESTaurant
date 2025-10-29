/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.Serializable;
import java.time.LocalDate;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author fonse
 */
public class SeccionDto extends RecursiveTreeObject<SeccionDto> implements Serializable {

    private StringProperty nombre;
    private StringProperty tipo;
    private StringProperty cobraImpuesto;
    private StringProperty estado;
    private LongProperty idSeccion;
    private ObjectProperty<LocalDate> fechaCreacion;
    private LongProperty idArchivoImagen;

    public SeccionDto() {
        this.idSeccion = new SimpleLongProperty();
        this.idArchivoImagen = new SimpleLongProperty();
        this.nombre = new SimpleStringProperty();
        this.estado = new SimpleStringProperty();
        this.tipo = new SimpleStringProperty();
        this.cobraImpuesto = new SimpleStringProperty();
        this.fechaCreacion = new SimpleObjectProperty<>();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public void setTipo(String tipo) {
        this.tipo.set(tipo);
    }

    public void setCobraImpuesto(String cobraImpuesto) {
        this.cobraImpuesto.set(cobraImpuesto);
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public void setIdSeccion(Long idSeccion) {
        this.idSeccion.set(idSeccion);
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion.set(fechaCreacion);
    }

    public void setIdArchivoImagen(Long idArchivoImagen) {
        this.idArchivoImagen.set(idArchivoImagen);
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getTipo() {
        return tipo.get();
    }

    public String getCobraImpuesto() {
        return cobraImpuesto.get();
    }

    public String getEstado() {
        return estado.get();
    }

    public Long getIdSeccion() {
        return idSeccion.get();
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion.get();
    }

    public Long getIdArchivoImagen() {
        return idArchivoImagen.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty tipoProperty() {
        return tipo;
    }

    public StringProperty cobraImpuestoProperty() {
        return cobraImpuesto;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public LongProperty idSeccionProperty() {
        return idSeccion;
    }

    public ObjectProperty<LocalDate> fechaCreacionProperty() {
        return fechaCreacion;
    }

    public LongProperty idArchivoImagenProperty() {
        return idArchivoImagen;
    }
    
    @Override
    public String toString() {
        return nombre + "(" + tipo + ")";
    }
}
