package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.CierreCajaDto;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.Request;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para gestión de cierres de caja
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
public class CierreCajaService {

    private static final Logger LOGGER = Logger.getLogger(CierreCajaService.class.getName());

    public Respuesta obtenerTodos() {
        try {
            Request request = new Request("cierrecaja");
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "CierresCaja", responseJson);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error obteniendo todos los cierres de caja", ex);
            return new Respuesta(false, "Error obteniendo cierres de caja: " + ex.getMessage(), "");
        }
    }

    public Respuesta obtenerPorId(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("cierrecaja", "/{id}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "CierreCaja", responseJson);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error obteniendo cierre de caja por ID", ex);
            return new Respuesta(false, "Error obteniendo cierre de caja: " + ex.getMessage(), "");
        }
    }

    public Respuesta obtenerPorCajero(Long idCajero) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("idCajero", idCajero);
            Request request = new Request("cierrecaja/cajero", "/{idCajero}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "CierresCaja", responseJson);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error obteniendo cierres por cajero", ex);
            return new Respuesta(false, "Error obteniendo cierres de caja: " + ex.getMessage(), "");
        }
    }

    public Respuesta guardarCierreCaja(CierreCajaDto cierreDto) {
        try {
            Request request = new Request("cierrecaja");
            request.post(cierreDto);
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "CierreCaja", responseJson);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error guardando cierre de caja", ex);
            return new Respuesta(false, "Error guardando cierre de caja: " + ex.getMessage(), "");
        }
    }

    public Respuesta actualizarCierreCaja(CierreCajaDto cierreDto) {
        try {
            Request request = new Request("cierrecaja");
            request.post(cierreDto);
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "CierreCaja", responseJson);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error actualizando cierre de caja", ex);
            return new Respuesta(false, "Error actualizando cierre de caja: " + ex.getMessage(), "");
        }
    }

    public Respuesta eliminarCierreCaja(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("cierrecaja", "/{id}", parametros);
            request.delete();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "CierreCaja", responseJson);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error eliminando cierre de caja", ex);
            return new Respuesta(false, "Error eliminando cierre de caja: " + ex.getMessage(), "");
        }
    }
}
