package cr.ac.una.restuna.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;
import java.io.Serializable;
import java.time.LocalDate;

public class ProductoDto extends RecursiveTreeObject<ProductoDto> implements Serializable {

    private LongProperty idProducto;
    private LongProperty idGrupoProducto;
    private StringProperty nombre;
    private StringProperty nombreCorto;
    private StringProperty descripcion;
    private StringProperty accesoRapido;
    private StringProperty estado;
    private DoubleProperty precio;
    private IntegerProperty cantidadVendida;
    private ObjectProperty<LocalDate> fechaCreacion;

    private GrupoProductoDto grupoProducto;

    public ProductoDto() {
        this.idProducto = new SimpleLongProperty();
        this.idGrupoProducto = new SimpleLongProperty();
        this.nombre = new SimpleStringProperty();
        this.nombreCorto = new SimpleStringProperty();
        this.descripcion = new SimpleStringProperty();
        this.accesoRapido = new SimpleStringProperty();
        this.estado = new SimpleStringProperty();
        this.precio = new SimpleDoubleProperty();
        this.cantidadVendida = new SimpleIntegerProperty();
        this.fechaCreacion = new SimpleObjectProperty<>();
    }

   
    public Long getIdProducto() {
        return idProducto.get();
    }

    public Long getIdGrupoProducto() {
        return idGrupoProducto.get();
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getNombreCorto() {
        return nombreCorto.get();
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

    public Double getPrecio() {
        return precio.get();
    }

    public Integer getCantidadVendida() {
        return cantidadVendida.get();
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion.get();
    }

    public GrupoProductoDto getGrupoProducto() {
        return grupoProducto;
    }


    public void setIdProducto(Long idProducto) {
        this.idProducto.set(idProducto);
    }

    public void setIdGrupoProducto(Long idGrupoProducto) {
        this.idGrupoProducto.set(idGrupoProducto);
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto.set(nombreCorto);
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

    public void setPrecio(Double precio) {
        this.precio.set(precio);
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida.set(cantidadVendida);
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion.set(fechaCreacion);
    }

    public void setGrupoProducto(GrupoProductoDto grupoProducto) {
        this.grupoProducto = grupoProducto;
    }

  
    public LongProperty idProductoProperty() {
        return idProducto;
    }

    public LongProperty idGrupoProductoProperty() {
        return idGrupoProducto;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty nombreCortoProperty() {
        return nombreCorto;
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

    public DoubleProperty precioProperty() {
        return precio;
    }

    public IntegerProperty cantidadVendidaProperty() {
        return cantidadVendida;
    }

    public ObjectProperty<LocalDate> fechaCreacionProperty() {
        return fechaCreacion;
    }

    @Override
    public String toString() {
        return "ProductoDto{"
                + "idProducto=" + getIdProducto()
                + ", nombre='" + getNombre() + '\''
                + ", nombreCorto='" + getNombreCorto() + '\''
                + ", precio=" + getPrecio()
                + ", estado='" + getEstado() + '\''
                + '}';
    }
}
