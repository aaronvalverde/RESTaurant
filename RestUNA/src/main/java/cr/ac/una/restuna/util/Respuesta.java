package cr.ac.una.restuna.util;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Clase para estandarizar respuestas del sistema
 * Permite encapsular el estado, código de respuesta, mensajes y resultados
 */
public class Respuesta implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Boolean estado;
    private String codigoRespuesta;   
    private String mensaje;    
    private String mensajeInterno;
    private HashMap<String, Object> resultado; 

    public Respuesta() {
        this.resultado = new HashMap<>();
    }

    public Respuesta(Boolean estado, String mensaje, String mensajeInterno) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.mensajeInterno = mensajeInterno;
        this.resultado = new HashMap<>();
    }
    
    public Respuesta(Boolean estado, String mensaje, String mensajeInterno, String nombre, Object resultado) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.mensajeInterno = mensajeInterno;
        this.resultado = new HashMap<>();
        this.resultado.put(nombre, resultado);
    }

    public Respuesta(Boolean estado, String mensaje, String mensajeInterno, Object resultado) {
        this.estado = estado;
        this.mensaje = mensaje;
        this.mensajeInterno = mensajeInterno;
        this.resultado = new HashMap<>();
        this.resultado.put("[Objeto]", resultado);
    }
    
    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensajeInterno() {
        return mensajeInterno;
    }

    public void setMensajeInterno(String mensajeInterno) {
        this.mensajeInterno = mensajeInterno;
    }
    
    public Object getResultado(String nombre) {
        return resultado.get(nombre);
    }

    public void setResultado(String nombre, Object resultado) {
        this.resultado.put(nombre, resultado);
    }
    
    public Object getResultado() {
        return resultado.get("[Objeto]");
    }

    public void setResultado(Object resultado) {
        this.resultado.put("[Objeto]", resultado);
    }
}