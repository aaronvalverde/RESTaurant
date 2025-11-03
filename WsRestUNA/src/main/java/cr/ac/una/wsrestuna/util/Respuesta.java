package cr.ac.una.wsrestuna.util;

import jakarta.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.HashMap;


public class Respuesta implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @JsonbProperty("estado")
    private Boolean estado;
    
    @JsonbProperty("codigoRespuesta")
    private CodigoRespuesta codigoRespuesta;   
    
    @JsonbProperty("mensaje")
    private String mensaje;    
    
    @JsonbProperty("mensajeInterno")
    private String mensajeInterno;
    
    @JsonbProperty("resultado")
    private HashMap<String, Object> resultado; 

    public Respuesta() {
        this.resultado = new HashMap<>();
    }

    public Respuesta(Boolean estado, CodigoRespuesta codigoRespuesta, String mensaje, String mensajeInterno) {
        this.estado = estado;
        this.codigoRespuesta = codigoRespuesta;
        this.mensaje = mensaje;
        this.mensajeInterno = mensajeInterno;
        this.resultado = new HashMap<>();
    }
    
    public Respuesta(Boolean estado, CodigoRespuesta codigoRespuesta, String mensaje, String mensajeInterno, String nombre, Object resultado) {
        this.estado = estado;
        this.codigoRespuesta = codigoRespuesta;
        this.mensaje = mensaje;
        this.mensajeInterno = mensajeInterno;
        this.resultado = new HashMap<>();
        this.resultado.put(nombre, resultado);
    }

    public Respuesta(Boolean estado, CodigoRespuesta codigoRespuesta, String mensaje, String mensajeInterno, Object resultado) {
        this.estado = estado;
        this.codigoRespuesta = codigoRespuesta;
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

    public CodigoRespuesta getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(CodigoRespuesta codigoRespuesta) {
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
    
    
    public HashMap<String, Object> getResultados() {
        return resultado;
    }
    
    public void setResultados(HashMap<String, Object> resultado) {
        this.resultado = resultado;
    }
}