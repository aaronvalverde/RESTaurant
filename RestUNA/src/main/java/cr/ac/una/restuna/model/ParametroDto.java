package cr.ac.una.restuna.model;

import java.io.Serializable;
import java.time.LocalDate;


public class ParametroDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long idParametro;
    private Long idUsuario;
    private String clave;
    private String valor;
    private String descripcion;
    private String tipoDato;
    private LocalDate fechaModificacion;
    private Boolean modificado;
    
    public ParametroDto() {
        this.modificado = false;
    }
    
    public Long getIdParametro() {
        return idParametro;
    }
    
    public void setIdParametro(Long idParametro) {
        this.idParametro = idParametro;
    }
    
    public Long getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getClave() {
        return clave;
    }
    
    public void setClave(String clave) {
        this.clave = clave;
    }
    
    public String getValor() {
        return valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getTipoDato() {
        return tipoDato;
    }
    
    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }
    
    public LocalDate getFechaModificacion() {
        return fechaModificacion;
    }
    
    public void setFechaModificacion(LocalDate fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
    
    public Boolean getModificado() {
        return modificado;
    }
    
    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }
    
    
    public Integer getValorComoEntero() {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public Double getValorComoDecimal() {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public Boolean getValorComoBoolean() {
        if (valor == null || valor.isEmpty()) {
            return null;
        }
        return "S".equalsIgnoreCase(valor) || "true".equalsIgnoreCase(valor) || "1".equals(valor);
    }
    
    @Override
    public String toString() {
        return clave + " = " + valor + " (" + tipoDato + ")";
    }
}
