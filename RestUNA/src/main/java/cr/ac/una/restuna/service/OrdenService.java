package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.OrdenDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class OrdenService {
    
    private static final Logger LOG = Logger.getLogger(OrdenService.class.getName());
    
    
    public Respuesta getOrden(Long id) {
        try {
            if (id == null || id <= 0) {
                return new Respuesta(false, "Debe especificar el ID de la orden", "id inválido");
            }
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            
            Request request = new Request("OrdenController/orden", "/{id}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Orden", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo orden.", ex);
            return new Respuesta(false, "Error obteniendo la orden", "getOrden " + ex.getMessage());
        }
    }
    
    
    public Respuesta getOrdenes() {
        try {
            Request request = new Request("OrdenController/ordenes");
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Ordenes", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo órdenes.", ex);
            return new Respuesta(false, "Error obteniendo las órdenes", "getOrdenes " + ex.getMessage());
        }
    }
    
    
    public Respuesta getOrdenesPorEstado(String estado) {
        try {
            if (estado == null || estado.trim().isEmpty()) {
                return new Respuesta(false, "Debe especificar el estado", "estado vacío");
            }
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("estado", estado);
            
            Request request = new Request("OrdenController/ordenes/estado", "/{estado}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Ordenes", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo órdenes por estado.", ex);
            return new Respuesta(false, "Error obteniendo órdenes", "getOrdenesPorEstado " + ex.getMessage());
        }
    }
    
    
    public Respuesta getOrdenesPorMesa(Long idMesa) {
        try {
            if (idMesa == null || idMesa <= 0) {
                return new Respuesta(false, "Debe especificar la mesa", "idMesa inválido");
            }
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("idMesa", idMesa);
            
            Request request = new Request("OrdenController/ordenes/mesa", "/{idMesa}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Ordenes", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo órdenes por mesa.", ex);
            return new Respuesta(false, "Error obteniendo órdenes", "getOrdenesPorMesa " + ex.getMessage());
        }
    }
    
    
    public Respuesta guardarOrden(OrdenDto ordenDto) {
        try {
            if (ordenDto == null) {
                return new Respuesta(false, "Los datos de la orden son requeridos", "ordenDto es null");
            }
            
            
            if (ordenDto.getDetalles() == null || ordenDto.getDetalles().isEmpty()) {
                return new Respuesta(false, "La orden debe tener al menos un producto", "detalles vacíos");
            }
            
            Request request = new Request("OrdenController/orden");
            request.post(ordenDto);
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "Orden guardada correctamente", "", "Orden", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando orden.", ex);
            return new Respuesta(false, "Error guardando la orden", "guardarOrden " + ex.getMessage());
        }
    }
    
    
    public Respuesta cambiarEstadoOrden(Long id, String estado) {
        try {
            if (id == null || id <= 0) {
                return new Respuesta(false, "Debe especificar el ID de la orden", "id inválido");
            }
            
            if (estado == null || estado.trim().isEmpty()) {
                return new Respuesta(false, "Debe especificar el estado", "estado vacío");
            }
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            parametros.put("estado", estado);
            
            Request request = new Request("OrdenController/orden", "/{id}/estado/{estado}", parametros);
            request.put(null);
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "Estado actualizado correctamente", "", "Orden", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error cambiando estado de orden.", ex);
            return new Respuesta(false, "Error actualizando estado", "cambiarEstadoOrden " + ex.getMessage());
        }
    }
    
    
    public Respuesta eliminarOrden(Long id) {
        try {
            if (id == null || id <= 0) {
                return new Respuesta(false, "Debe especificar el ID de la orden", "id inválido");
            }
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            
            Request request = new Request("OrdenController/orden", "/{id}", parametros);
            request.delete();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            return new Respuesta(true, "Orden eliminada correctamente", "");
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando orden.", ex);
            return new Respuesta(false, "Error eliminando la orden", "eliminarOrden " + ex.getMessage());
        }
    }
}
