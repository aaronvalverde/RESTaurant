package cr.ac.una.shiftsws.util;

public enum CodigoRespuesta {
    
    CORRECTO(200),
    ERROR_NOENCONTRADO(404),
    ERROR_INTERNO(500),
    ERROR_CLIENTE(400),
    ERROR_PERMISOS(401),
    ERROR_SIN_CONTENIDO(204);
    
    private final int valor;
    
    private CodigoRespuesta(int valor) {
        this.valor = valor;
    }
    
    public int getValue() {
        return valor;
    }
}