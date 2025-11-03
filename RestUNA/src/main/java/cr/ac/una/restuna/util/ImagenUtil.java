package cr.ac.una.restuna.util;

import cr.ac.una.restuna.model.ArchivoDto;
import javafx.scene.image.Image;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

/**
 * Utilidad para manejar conversiones de archivos e imágenes
 * Maneja Base64 encoding/decoding para transferencia de archivos
 */
public class ImagenUtil {
    
    /**
     * Convierte un archivo a Base64 y crea un ArchivoDto
     */
    public static ArchivoDto fileToArchivoDto(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("El archivo no existe");
        }
        
        // Leer el archivo como bytes
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        
        // Convertir a Base64
        String base64Content = Base64.getEncoder().encodeToString(fileBytes);
        
        // Detectar el tipo MIME
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            // Fallback basado en extensión
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                mimeType = "image/jpeg";
            } else if (fileName.endsWith(".png")) {
                mimeType = "image/png";
            } else if (fileName.endsWith(".gif")) {
                mimeType = "image/gif";
            } else if (fileName.endsWith(".bmp")) {
                mimeType = "image/bmp";
            } else {
                mimeType = "application/octet-stream";
            }
        }
        
        // Crear el DTO
        ArchivoDto dto = new ArchivoDto();
        dto.setNombreArchivo(file.getName());
        dto.setTipoMime(mimeType);
        dto.setContenidoBase64(base64Content);
        dto.setTamanio((long) base64Content.length());
        
        return dto;
    }
    
    /**
     * Convierte Base64 a Image de JavaFX
     */
    public static Image base64ToImage(String base64Content) {
        if (base64Content == null || base64Content.isEmpty()) {
            return null;
        }
        
        try {
            // Decodificar Base64 a bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Content);
            
            // Crear InputStream desde los bytes
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            
            // Crear y retornar la imagen
            return new Image(bis);
            
        } catch (Exception e) {
            System.err.println("Error convirtiendo Base64 a imagen: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Convierte un ArchivoDto a Image de JavaFX
     */
    public static Image archivoDtoToImage(ArchivoDto archivoDto) {
        if (archivoDto == null || !archivoDto.tieneContenido()) {
            return null;
        }
        
        return base64ToImage(archivoDto.getContenidoBase64());
    }
    
    /**
     * Valida si un archivo es una imagen
     */
    public static boolean esImagen(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || 
               fileName.endsWith(".jpeg") || 
               fileName.endsWith(".png") || 
               fileName.endsWith(".gif") || 
               fileName.endsWith(".bmp");
    }
    
    /**
     * Valida el tamaño del archivo (máximo 5MB recomendado)
     */
    public static boolean validarTamanio(File file, long maxBytes) {
        if (file == null || !file.exists()) {
            return false;
        }
        
        return file.length() <= maxBytes;
    }
    
    /**
     * Valida el tamaño del archivo con límite por defecto de 5MB
     */
    public static boolean validarTamanio(File file) {
        return validarTamanio(file, 5 * 1024 * 1024); // 5MB
    }
    
    /**
     * Formatea el tamaño en bytes a una cadena legible
     */
    public static String formatearTamanio(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
