package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.GrupoProductoDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GrupoProductoService {
    
    private static final Logger LOG = Logger.getLogger(GrupoProductoService.class.getName());
    
    
    public Respuesta getGrupoProducto(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("GrupoProductoController/grupo", "/{id}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "GrupoProducto", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupo de productos [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo el grupo.", "getGrupoProducto " + ex.getMessage());
        }
    }
    
    
    public Respuesta getGrupoProductos() {
        try {
            System.out.println("Iniciando solicitud para obtener todos los grupos de productos");
            Request request = new Request("GrupoProductoController/grupos");
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
            
            
            if (!responseJson.trim().startsWith("[")) {
                System.err.println("Formato de respuesta inesperado: " + responseJson);
                return new Respuesta(false, "Formato de respuesta no válido", "Esperaba un array de grupos");
            }
            
            return new Respuesta(true, "", "", "GrupoProductos", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos de productos", ex);
            ex.printStackTrace();
            return new Respuesta(false, "Error obteniendo grupos.", "getGrupoProductos " + ex.getMessage());
        }
    }
    
    
    public Respuesta getGrupoProductosActivos() {
        try {
            System.out.println("Iniciando solicitud para obtener grupos activos");
            Request request = new Request("GrupoProductoController/grupos/activos");
            request.get();
            
            if (request.isError()) {
                System.err.println("Error en la solicitud: " + request.getError());
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            
            if (responseJson == null || responseJson.trim().isEmpty()) {
                System.err.println("Respuesta vacía del servidor");
                return new Respuesta(false, "Respuesta vacía del servidor", "No se recibieron datos");
            }
            
            return new Respuesta(true, "", "", "GrupoProductos", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos activos", ex);
            return new Respuesta(false, "Error obteniendo grupos activos.", "getGrupoProductosActivos " + ex.getMessage());
        }
    }
    
    
    public Respuesta getGrupoProductosAccesoRapido() {
        try {
            Request request = new Request("GrupoProductoController/grupos/accesorapido");
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "GrupoProductos", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos de acceso rápido", ex);
            return new Respuesta(false, "Error obteniendo grupos de acceso rápido.", "getGrupoProductosAccesoRapido " + ex.getMessage());
        }
    }
    
    
    public Respuesta getGrupoProductosMasVendidos() {
        try {
            Request request = new Request("GrupoProductoController/grupos/masvendidos");
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "GrupoProductos", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos más vendidos", ex);
            return new Respuesta(false, "Error obteniendo grupos más vendidos.", "getGrupoProductosMasVendidos " + ex.getMessage());
        }
    }
    
    
    public Respuesta guardarGrupoProducto(cr.ac.una.restuna.model.GrupoProductoDto grupo) {
        try {
            System.out.println("Guardando grupo: " + grupo.getNombre());
            Request request = new Request("GrupoProductoController/grupo");
            request.post(grupo);
            
            if (request.isError()) {
                System.err.println("Error guardando grupo: " + request.getError());
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            System.out.println("Grupo guardado exitosamente");
            
            return new Respuesta(true, "", "", "GrupoProducto", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando grupo de productos", ex);
            ex.printStackTrace();
            return new Respuesta(false, "Error guardando el grupo.", "guardarGrupoProducto " + ex.getMessage());
        }
    }
    
    
    public Respuesta eliminarGrupoProducto(Long id) {
        try {
            System.out.println("Eliminando grupo con ID: " + id);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("GrupoProductoController/grupo", "/{id}", parametros);
            request.delete();
            
            if (request.isError()) {
                System.err.println("Error eliminando grupo: " + request.getError());
                return new Respuesta(false, request.getError(), "");
            }
            
            System.out.println("Grupo eliminado exitosamente");
            return new Respuesta(true, "", "");
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando grupo de productos [" + id + "]", ex);
            ex.printStackTrace();
            return new Respuesta(false, "Error eliminando el grupo.", "eliminarGrupoProducto " + ex.getMessage());
        }
    }
}
