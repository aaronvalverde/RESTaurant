package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.ArchivoDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ArchivoService {
    
    private static final Logger LOG = Logger.getLogger(ArchivoService.class.getName());
    
    
    public Respuesta getArchivo(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("ArchivoController/archivo", "/{id}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Archivo", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo archivo [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo el archivo.", "getArchivo " + ex.getMessage());
        }
    }
    
    
    public Respuesta getArchivos() {
        try {
            Request request = new Request("ArchivoController/archivos");
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Archivos", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo archivos", ex);
            return new Respuesta(false, "Error obteniendo archivos.", "getArchivos " + ex.getMessage());
        }
    }
    
    
    public Respuesta guardarArchivo(ArchivoDto archivoDto) {
        try {
            if (archivoDto == null) {
                return new Respuesta(false, "Datos de archivo inválidos", "ArchivoDto es null");
            }
            
            if (archivoDto.getNombreArchivo() == null || archivoDto.getNombreArchivo().trim().isEmpty()) {
                return new Respuesta(false, "El nombre del archivo es obligatorio", "Nombre vacío");
            }
            
            if (archivoDto.getContenidoBase64() == null || archivoDto.getContenidoBase64().isEmpty()) {
                return new Respuesta(false, "El contenido del archivo es obligatorio", "Contenido vacío");
            }
            
            System.out.println("Guardando archivo: " + archivoDto.getNombreArchivo());
            Request request = new Request("ArchivoController/archivo");
            request.post(archivoDto);
            
            if (request.isError()) {
                System.err.println("Error guardando archivo: " + request.getError());
                return new Respuesta(false, "Error guardando archivo", request.getError());
            }
            
            String responseJson = request.getResponseBody();
            System.out.println("Archivo guardado correctamente");
            
            return new Respuesta(true, "Archivo guardado correctamente", "", "Archivo", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando archivo", ex);
            return new Respuesta(false, "Error guardando archivo.", "guardarArchivo " + ex.getMessage());
        }
    }
    
    
    public Respuesta eliminarArchivo(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("ArchivoController/archivo", "/{id}", parametros);
            request.delete();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            return new Respuesta(true, "Archivo eliminado correctamente", "");
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando archivo [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando archivo.", "eliminarArchivo " + ex.getMessage());
        }
    }
}
