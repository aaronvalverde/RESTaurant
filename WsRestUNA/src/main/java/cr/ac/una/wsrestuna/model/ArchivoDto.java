package cr.ac.una.wsrestuna.model;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO para transferir datos de archivos entre cliente y servidor
 * Utiliza Base64 para el contenido binario
 */
public class ArchivoDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long idArchivo;
    private String nombreArchivo;
    private String tipoMime;
    private Long tamanio;
    private String contenidoBase64; // Contenido en Base64 para JSON
    private Date fechaSubida;
    
    public ArchivoDto() {
    }
    
    public ArchivoDto(Archivo archivo) {
        this.idArchivo = archivo.getIdArchivo();
        this.nombreArchivo = archivo.getNombreArchivo();
        this.tipoMime = archivo.getTipoMime();
        this.tamanio = archivo.getTamanio();
        this.fechaSubida = archivo.getFechaSubida();
        
        // Convertir bytes a Base64 solo si se solicita
        if (archivo.getContenido() != null) {
            this.contenidoBase64 = java.util.Base64.getEncoder().encodeToString(archivo.getContenido());
        }
    }
    
    /**
     * Constructor sin contenido (para listados donde no se necesita la imagen completa)
     */
    public ArchivoDto(Archivo archivo, boolean incluirContenido) {
        this.idArchivo = archivo.getIdArchivo();
        this.nombreArchivo = archivo.getNombreArchivo();
        this.tipoMime = archivo.getTipoMime();
        this.tamanio = archivo.getTamanio();
        this.fechaSubida = archivo.getFechaSubida();
        
        if (incluirContenido && archivo.getContenido() != null) {
            this.contenidoBase64 = java.util.Base64.getEncoder().encodeToString(archivo.getContenido());
        }
    }

    // Getters y Setters
    public Long getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(Long idArchivo) {
        this.idArchivo = idArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Long getTamanio() {
        return tamanio;
    }

    public void setTamanio(Long tamanio) {
        this.tamanio = tamanio;
    }

    public String getContenidoBase64() {
        return contenidoBase64;
    }

    public void setContenidoBase64(String contenidoBase64) {
        this.contenidoBase64 = contenidoBase64;
    }

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    @Override
    public String toString() {
        return "ArchivoDto{" +
                "idArchivo=" + idArchivo +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", tipoMime='" + tipoMime + '\'' +
                ", tamanio=" + tamanio +
                '}';
    }
}
