package cr.ac.una.restuna.util;

import cr.ac.una.restuna.model.ArchivoDto;
import javafx.scene.image.Image;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;


public class ImagenUtil {
    
    
    public static ArchivoDto fileToArchivoDto(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("El archivo no existe");
        }
        
        
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        
        
        String base64Content = Base64.getEncoder().encodeToString(fileBytes);
        
        
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            
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
        
        
        ArchivoDto dto = new ArchivoDto();
        dto.setNombreArchivo(file.getName());
        dto.setTipoMime(mimeType);
        dto.setContenidoBase64(base64Content);
        dto.setTamanio((long) base64Content.length());
        
        return dto;
    }
    
    
    public static Image base64ToImage(String base64Content) {
        if (base64Content == null || base64Content.isEmpty()) {
            return null;
        }
        
        try {
            
            byte[] imageBytes = Base64.getDecoder().decode(base64Content);
            
            
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            
            
            return new Image(bis);
            
        } catch (Exception e) {
            System.err.println("Error convirtiendo Base64 a imagen: " + e.getMessage());
            return null;
        }
    }
    
    
    public static Image archivoDtoToImage(ArchivoDto archivoDto) {
        if (archivoDto == null || !archivoDto.tieneContenido()) {
            return null;
        }
        
        return base64ToImage(archivoDto.getContenidoBase64());
    }
    
    
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
    
    
    public static boolean validarTamanio(File file, long maxBytes) {
        if (file == null || !file.exists()) {
            return false;
        }
        
        return file.length() <= maxBytes;
    }
    
    
    public static boolean validarTamanio(File file) {
        return validarTamanio(file, 5 * 1024 * 1024); 
    }
    
    
    public static String formatearTamanio(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
