/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.dto;

import java.time.LocalDate;

/**
 *
 * @author fonse
 */
public class GrupoProductoDto {
    
    private String nombre;
    private String descripcion;
    private String accesoRapido;
    private String estado;
    private Long idGrupoProducto;
    private Integer ordenVisualizacion;
    private Integer cantidadVendida;
    private LocalDate fechaCreacion;

    public GrupoProductoDto(String nombre, String descripcion, String accesoRapido, String estado, Long idGrupoProducto, Integer ordenVisualizacion, Integer cantidadVendida, LocalDate fechaCreacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.accesoRapido = accesoRapido;
        this.estado = estado;
        this.idGrupoProducto = idGrupoProducto;
        this.ordenVisualizacion = ordenVisualizacion;
        this.cantidadVendida = cantidadVendida;
        this.fechaCreacion = fechaCreacion;
    }

    public GrupoProductoDto() {
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public void setIdGrupoProducto(Long idGrupoProducto) {
        this.idGrupoProducto = idGrupoProducto;
    }

    public void setOrdenVisualizacion(Integer ordenVisualizacion) {
        this.ordenVisualizacion = ordenVisualizacion;
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombre() {
        return nombre;
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

    public Long getIdGrupoProducto() {
        return idGrupoProducto;
    }

    public Integer getOrdenVisualizacion() {
        return ordenVisualizacion;
    }

    public Integer getCantidadVendida() {
        return cantidadVendida;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
    
    
}
