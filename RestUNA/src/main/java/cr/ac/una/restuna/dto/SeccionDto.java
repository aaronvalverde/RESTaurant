/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author fonse
 */
public class SeccionDto implements Serializable {

    private String nombre;
    private String tipo;
    private String cobraImpuesto;
    private String estado;
    private Long idSeccion;
    private LocalDate fechaCreacion;
    private Long idArchivoImagen;

    public SeccionDto(String nombre, String tipo, String cobraImpuesto, String estado, Long idSeccion, LocalDate fechaCreacion, Long idArchivoImagen) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.cobraImpuesto = cobraImpuesto;
        this.estado = estado;
        this.idSeccion = idSeccion;
        this.fechaCreacion = fechaCreacion;
        this.idArchivoImagen = idArchivoImagen;
    }

    public SeccionDto() {
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCobraImpuesto(String cobraImpuesto) {
        this.cobraImpuesto = cobraImpuesto;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setIdSeccion(Long idSeccion) {
        this.idSeccion = idSeccion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setIdArchivoImagen(Long idArchivoImagen) {
        this.idArchivoImagen = idArchivoImagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCobraImpuesto() {
        return cobraImpuesto;
    }

    public String getEstado() {
        return estado;
    }

    public Long getIdSeccion() {
        return idSeccion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public Long getIdArchivoImagen() {
        return idArchivoImagen;
    }

    @Override
    public String toString() {
        return nombre + "(" + tipo + ")";
    }
}
