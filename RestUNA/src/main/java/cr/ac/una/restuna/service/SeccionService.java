package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.SeccionDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para operaciones con secciones/salones del restaurante
 */
public class SeccionService {
    
    private static final Logger LOG = Logger.getLogger(SeccionService.class.getName());
    
    /**
     * Obtiene una sección por ID (sin contenido de imagen)
     */
    public Respuesta getSeccion(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("SeccionController/seccion", "/{id}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Seccion", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo sección [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo la sección.", "getSeccion " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene una sección por ID con el contenido completo de su imagen
     */
    public Respuesta getSeccionConImagen(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("SeccionController/seccion", "/{id}/conimagen", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Seccion", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo sección con imagen [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo la sección.", "getSeccionConImagen " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene todas las secciones
     */
    public Respuesta getSecciones() {
        try {
            System.out.println("Iniciando solicitud para obtener todas las secciones");
            Request request = new Request("SeccionController/secciones");
            request.get();
            
            if (request.isError()) {
                System.err.println("Error en la solicitud: " + request.getError());
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            System.out.println("Respuesta recibida de longitud: " + 
                              (responseJson != null ? responseJson.length() : 0));
            
            if (responseJson == null || responseJson.trim().isEmpty()) {
                System.err.println("Respuesta vacía del servidor");
                return new Respuesta(false, "Respuesta vacía del servidor", "No se recibieron datos");
            }
            
            // Verificar que sea un array válido
            if (!responseJson.trim().startsWith("[")) {
                System.err.println("Formato de respuesta inesperado: " + responseJson);
                return new Respuesta(false, "Formato de respuesta no válido", "Esperaba un array de secciones");
            }
            
            return new Respuesta(true, "", "", "Secciones", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo secciones", ex);
            ex.printStackTrace();
            return new Respuesta(false, "Error obteniendo secciones.", "getSecciones " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene solo las secciones activas
     */
    public Respuesta getSeccionesActivas() {
        try {
            Request request = new Request("SeccionController/secciones/activas");
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            
            if (responseJson == null || responseJson.trim().isEmpty()) {
                return new Respuesta(false, "Respuesta vacía del servidor", "No se recibieron datos");
            }
            
            if (!responseJson.trim().startsWith("[")) {
                return new Respuesta(false, "Formato de respuesta no válido", "Esperaba un array de secciones");
            }
            
            return new Respuesta(true, "", "", "Secciones", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo secciones activas", ex);
            return new Respuesta(false, "Error obteniendo secciones activas.", "getSeccionesActivas " + ex.getMessage());
        }
    }
    
    /**
     * Guarda una sección (crear o actualizar)
     */
    public Respuesta guardarSeccion(SeccionDto seccionDto) {
        try {
            if (seccionDto == null) {
                return new Respuesta(false, "Datos de sección inválidos", "SeccionDto es null");
            }
            
            if (seccionDto.getNombre() == null || seccionDto.getNombre().trim().isEmpty()) {
                return new Respuesta(false, "El nombre de la sección es obligatorio", "Nombre vacío");
            }
            
            if (seccionDto.getTipo() == null || seccionDto.getTipo().trim().isEmpty()) {
                return new Respuesta(false, "El tipo de sección es obligatorio", "Tipo vacío");
            }
            
            // Asegurar valores por defecto
            if (seccionDto.getEstado() == null) {
                seccionDto.setEstado("A");
            }
            
            if (seccionDto.getCobraImpuesto() == null) {
                seccionDto.setCobraImpuesto("N");
            }
            
            System.out.println("Guardando sección: " + seccionDto.getNombre());
            Request request = new Request("SeccionController/seccion");
            request.post(seccionDto);
            
            if (request.isError()) {
                System.err.println("Error guardando sección: " + request.getError());
                return new Respuesta(false, "Error guardando sección", request.getError());
            }
            
            String responseJson = request.getResponseBody();
            System.out.println("Sección guardada correctamente");
            
            return new Respuesta(true, "Sección guardada correctamente", "", "Seccion", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando sección", ex);
            ex.printStackTrace();
            return new Respuesta(false, "Error guardando sección.", "guardarSeccion " + ex.getMessage());
        }
    }
    
    /**
     * Elimina una sección por ID
     */
    public Respuesta eliminarSeccion(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("SeccionController/seccion", "/{id}", parametros);
            request.delete();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            return new Respuesta(true, "Sección eliminada correctamente", "");
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando sección [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando sección.", "eliminarSeccion " + ex.getMessage());
        }
    }
}
