package cr.ac.una.restuna.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.Serializable;
import java.time.LocalDateTime;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * DTO para Cliente del sistema RESTaurant
 */
public class ClienteDto extends RecursiveTreeObject<ClienteDto> implements Serializable {

    private LongProperty idCliente;
    private StringProperty nombre;
    private StringProperty apellidos;
    private StringProperty cedula;
    private StringProperty telefono;
    private StringProperty correo;
    private ObjectProperty<LocalDateTime> fechaCreacion;
    private ObjectProperty<LocalDateTime> fecha;
    private StringProperty modificado;

    public ClienteDto() {
        this.idCliente = new SimpleLongProperty();
        this.nombre = new SimpleStringProperty();
        this.apellidos = new SimpleStringProperty();
        this.cedula = new SimpleStringProperty();
        this.telefono = new SimpleStringProperty();
        this.correo = new SimpleStringProperty();
        this.fechaCreacion = new SimpleObjectProperty<>();
        this.fecha = new SimpleObjectProperty<>();
        this.modificado = new SimpleStringProperty("false");
    }

    // Properties
    public LongProperty idClienteProperty() {
        return idCliente;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty apellidosProperty() {
        return apellidos;
    }

    public StringProperty cedulaProperty() {
        return cedula;
    }

    public StringProperty telefonoProperty() {
        return telefono;
    }

    public StringProperty correoProperty() {
        return correo;
    }

    public ObjectProperty<LocalDateTime> fechaCreacionProperty() {
        return fechaCreacion;
    }

    public ObjectProperty<LocalDateTime> fechaProperty() {
        return fecha;
    }

    public StringProperty modificadoProperty() {
        return modificado;
    }

    // Getters
    public Long getIdCliente() {
        return idCliente.get();
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getApellidos() {
        return apellidos.get();
    }

    public String getCedula() {
        return cedula.get();
    }

    public String getTelefono() {
        return telefono.get();
    }

    public String getCorreo() {
        return correo.get();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion.get();
    }

    public LocalDateTime getFecha() {
        return fecha.get();
    }

    public Boolean getModificado() {
        return Boolean.parseBoolean(modificado.get());
    }

    // Setters
    public void setIdCliente(Long idCliente) {
        this.idCliente.set(idCliente);
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public void setApellidos(String apellidos) {
        this.apellidos.set(apellidos);
    }

    public void setCedula(String cedula) {
        this.cedula.set(cedula);
    }

    public void setTelefono(String telefono) {
        this.telefono.set(telefono);
    }

    public void setCorreo(String correo) {
        this.correo.set(correo);
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion.set(fechaCreacion);
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha.set(fecha);
    }

    public void setModificado(Boolean modificado) {
        this.modificado.set(String.valueOf(modificado));
    }

    /**
     * Obtiene el nombre completo
     */
    public String getNombreCompleto() {
        String n = nombre.get() != null ? nombre.get() : "";
        String a = apellidos.get() != null ? apellidos.get() : "";
        return (n + " " + a).trim();
    }

    @Override
    public String toString() {
        return "ClienteDto{" +
                "idCliente=" + getIdCliente() +
                ", nombre='" + getNombre() + '\'' +
                ", apellidos='" + getApellidos() + '\'' +
                ", cedula='" + getCedula() + '\'' +
                '}';
    }
}
