package cr.ac.una.restuna.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de archivos/imágenes
 * Usa Base64 para transferir contenido binario
 */
public class ArchivoDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long idArchivo;
    private String nombreArchivo;
    private String tipoMime;
    private Long tamanio;
    private String contenidoBase64;
    private LocalDateTime fechaSubida;
    
    public ArchivoDto() {
    }
    
    public ArchivoDto(String nombreArchivo, String tipoMime, String contenidoBase64) {
        this.nombreArchivo = nombreArchivo;
        this.tipoMime = tipoMime;
        this.contenidoBase64 = contenidoBase64;
        this.tamanio = contenidoBase64 != null ? (long) contenidoBase64.length() : 0L;
    }
    
    // Métodos de utilidad
    public boolean esImagen() {
        return tipoMime != null && tipoMime.startsWith("image/");
    }
    
    public boolean tieneContenido() {
        return contenidoBase64 != null && !contenidoBase64.isEmpty();
    }
    
    public String getTamanioFormateado() {
        if (tamanio == null) return "0 B";
        
        if (tamanio < 1024) return tamanio + " B";
        if (tamanio < 1024 * 1024) return String.format("%.2f KB", tamanio / 1024.0);
        return String.format("%.2f MB", tamanio / (1024.0 * 1024.0));
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

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    @Override
    public String toString() {
        return "ArchivoDto{" +
                "idArchivo=" + idArchivo +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", tipoMime='" + tipoMime + '\'' +
                ", tamanio=" + getTamanioFormateado() +
                '}';
    }
}
