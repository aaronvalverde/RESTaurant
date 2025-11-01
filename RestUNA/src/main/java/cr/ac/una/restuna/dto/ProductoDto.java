package cr.ac.una.restuna.dto;

import javafx.beans.property.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO de Producto para el cliente JavaFX
 */
public class ProductoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private SimpleObjectProperty<Long> idProducto;
    private SimpleObjectProperty<Long> idGrupoProducto;
    private SimpleStringProperty nombreGrupo;
    private SimpleStringProperty nombre;
    private SimpleStringProperty nombreCorto;
    private SimpleStringProperty descripcion;
    private SimpleObjectProperty<BigDecimal> precio;
    private SimpleStringProperty accesoRapido;
    private SimpleObjectProperty<Long> cantidadVendida;
    private SimpleStringProperty estado;
    private SimpleStringProperty fechaCreacion;

    public ProductoDto() {
        this.idProducto = new SimpleObjectProperty<>();
        this.idGrupoProducto = new SimpleObjectProperty<>();
        this.nombreGrupo = new SimpleStringProperty("");
        this.nombre = new SimpleStringProperty("");
        this.nombreCorto = new SimpleStringProperty("");
        this.descripcion = new SimpleStringProperty("");
        this.precio = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.accesoRapido = new SimpleStringProperty("N");
        this.cantidadVendida = new SimpleObjectProperty<>(0L);
        this.estado = new SimpleStringProperty("A");
        this.fechaCreacion = new SimpleStringProperty("");
    }

    // Constructor desde JSON del servidor
    public ProductoDto(String json) {
        this();
        
        try {
            json = json.replaceAll("[{}]", "");
            String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");

                    switch (key) {
                        case "idProducto":
                            if (!value.equals("null")) {
                                setIdProducto(Long.parseLong(value));
                            }
                            break;
                        case "idGrupoProducto":
                            if (!value.equals("null")) {
                                setIdGrupoProducto(Long.parseLong(value));
                            }
                            break;
                        case "nombreGrupo":
                            if (!value.equals("null")) {
                                setNombreGrupo(value);
                            }
                            break;
                        case "nombre":
                            if (!value.equals("null")) {
                                setNombre(value);
                            }
                            break;
                        case "nombreCorto":
                            if (!value.equals("null")) {
                                setNombreCorto(value);
                            }
                            break;
                        case "descripcion":
                            if (!value.equals("null")) {
                                setDescripcion(value);
                            }
                            break;
                        case "precio":
                            if (!value.equals("null")) {
                                setPrecio(new BigDecimal(value));
                            }
                            break;
                        case "accesoRapido":
                            if (!value.equals("null")) {
                                setAccesoRapido(value);
                            }
                            break;
                        case "cantidadVendida":
                            if (!value.equals("null")) {
                                setCantidadVendida(Long.parseLong(value));
                            }
                            break;
                        case "estado":
                            if (!value.equals("null")) {
                                setEstado(value);
                            }
                            break;
                        case "fechaCreacion":
                            if (!value.equals("null")) {
                                setFechaCreacion(value);
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parseando JSON en ProductoDto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Getters y Setters

    public Long getIdProducto() {
        return idProducto.get();
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto.set(idProducto);
    }

    public SimpleObjectProperty<Long> idProductoProperty() {
        return idProducto;
    }

    public Long getIdGrupoProducto() {
        return idGrupoProducto.get();
    }

    public void setIdGrupoProducto(Long idGrupoProducto) {
        this.idGrupoProducto.set(idGrupoProducto);
    }

    public SimpleObjectProperty<Long> idGrupoProductoProperty() {
        return idGrupoProducto;
    }

    public String getNombreGrupo() {
        return nombreGrupo.get();
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo.set(nombreGrupo);
    }

    public SimpleStringProperty nombreGrupoProperty() {
        return nombreGrupo;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public String getNombreCorto() {
        return nombreCorto.get();
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto.set(nombreCorto);
    }

    public SimpleStringProperty nombreCortoProperty() {
        return nombreCorto;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public SimpleStringProperty descripcionProperty() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio.get();
    }

    public void setPrecio(BigDecimal precio) {
        this.precio.set(precio);
    }

    public SimpleObjectProperty<BigDecimal> precioProperty() {
        return precio;
    }

    public String getAccesoRapido() {
        return accesoRapido.get();
    }

    public void setAccesoRapido(String accesoRapido) {
        this.accesoRapido.set(accesoRapido);
    }

    public SimpleStringProperty accesoRapidoProperty() {
        return accesoRapido;
    }

    public Long getCantidadVendida() {
        return cantidadVendida.get();
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida.set(cantidadVendida);
    }

    public SimpleObjectProperty<Long> cantidadVendidaProperty() {
        return cantidadVendida;
    }

    public String getEstado() {
        return estado.get();
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    public SimpleStringProperty estadoProperty() {
        return estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion.get();
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion.set(fechaCreacion);
    }

    public SimpleStringProperty fechaCreacionProperty() {
        return fechaCreacion;
    }

    // Métodos de utilidad

    public boolean isActivo() {
        return "A".equals(getEstado());
    }

    public boolean tieneAccesoRapido() {
        return "S".equals(getAccesoRapido());
    }

    @Override
    public String toString() {
        return "ProductoDto{" +
                "idProducto=" + getIdProducto() +
                ", idGrupoProducto=" + getIdGrupoProducto() +
                ", nombreGrupo='" + getNombreGrupo() + '\'' +
                ", nombre='" + getNombre() + '\'' +
                ", nombreCorto='" + getNombreCorto() + '\'' +
                ", descripcion='" + getDescripcion() + '\'' +
                ", precio=" + getPrecio() +
                ", accesoRapido='" + getAccesoRapido() + '\'' +
                ", cantidadVendida=" + getCantidadVendida() +
                ", estado='" + getEstado() + '\'' +
                ", fechaCreacion='" + getFechaCreacion() + '\'' +
                '}';
    }
}
