
package cr.ac.una.restuna.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class GrupoProductoDto extends RecursiveTreeObject<GrupoProductoDto> implements Serializable {

    private StringProperty nombre;
    private StringProperty descripcion;
    private StringProperty accesoRapido;
    private StringProperty estado;
    private LongProperty idGrupoProducto;
    private IntegerProperty ordenVisualizacion;
    private IntegerProperty cantidadVendida;
    private ObjectProperty<LocalDate> fechaCreacion;

    private List<ProductoDto> product = new ArrayList<>();
    
    public GrupoProductoDto() {

        this.idGrupoProducto = new SimpleLongProperty();
        this.nombre = new SimpleStringProperty();
        this.descripcion = new SimpleStringProperty();
        this.accesoRapido = new SimpleStringProperty();
        this.estado = new SimpleStringProperty();
        this.ordenVisualizacion = new SimpleIntegerProperty();
        this.cantidadVendida = new SimpleIntegerProperty();
        this.fechaCreacion = new SimpleObjectProperty<>();
    }

    public LongProperty idGrupoProductoProperty() {
        return idGrupoProducto;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public StringProperty accesoRapidoProperty() {
        return accesoRapido;
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public IntegerProperty ordenVisualizacionProperty() {
        return ordenVisualizacion;
    }

    public IntegerProperty cantidadVendidaProperty() {
        return cantidadVendida;
    }

    public ObjectProperty<LocalDate> fechaCreacionProperty() {
        return fechaCreacion;
    }

    public Long getIdGrupoProducto() {
        return idGrupoProducto.get();
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public String getAccesoRapido() {
        return accesoRapido.get();
    }

    public String getEstado() {
        return estado.get();
    }

    public Integer getOrdenVisualizacion() {
        return ordenVisualizacion.get();
    }

    public Integer getCantidadVendida() {
        return cantidadVendida.get();
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion.get();
    }

    public List<ProductoDto> getProduct(){
        return product;
        
    }
    
    public void setIdGrupoProducto(Long idGrupoProducto) {
        this.idGrupoProducto.set(idGrupoProducto);
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public void setAccesoRapido(String accesoRapido) {
        this.accesoRapido.set(accesoRapido);
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public void setOrdenVisualizacion(Integer ordenVisualizacion) {
        this.ordenVisualizacion.set(ordenVisualizacion);
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida.set(cantidadVendida);
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion.set(fechaCreacion);
    }
    
   public void setProductos(List<ProductoDto> product) {
    this.product = product;
}

}
