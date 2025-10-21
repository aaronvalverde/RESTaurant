/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.dto;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author fonse
 */
public class ProductoDto extends RecursiveTreeObject<ProductoDto> implements Serializable {
    
    private Long idProducto;
    private Long idGrupoProducto;
    private String nombre;
    private String nombreCorto;
    private String descripcion;
    private String accesoRapido;
    private String estado;
    private Double precio;
    private Integer cantidadVendida;
    private LocalDate fechaCreacion;

    private GrupoProductoDto grupoProducto;
    
    public ProductoDto(Long idProducto, Long idGrupoProducto, String nombre, String nombreCorto, String descripcion, String accesoRapido, String estado, Double precio, Integer cantidadVendida, LocalDate fechaCreacion) {
        this.idProducto = idProducto;
        this.idGrupoProducto = idGrupoProducto;
        this.nombre = nombre;
        this.nombreCorto = nombreCorto;
        this.descripcion = descripcion;
        this.accesoRapido = accesoRapido;
        this.estado = estado;
        this.precio = precio;
        this.cantidadVendida = cantidadVendida;
        this.fechaCreacion = fechaCreacion;
    }

    public ProductoDto() {
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public void setIdGrupoProducto(Long idGrupoProducto) {
        this.idGrupoProducto = idGrupoProducto;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setAccesoRapido(String accesoRapido) {
        this.accesoRapido = accesoRapido;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public Long getIdGrupoProducto() {
        return idGrupoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getAccesoRapido() {
        return accesoRapido;
    }

    public String getEstado() {
        return estado;
    }

    public Double getPrecio() {
        return precio;
    }

    public Integer getCantidadVendida() {
        return cantidadVendida;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    
    
}

