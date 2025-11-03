package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.ClienteDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio cliente para operaciones con clientes
 */
public class ClienteService {
    
    private static final Logger LOG = Logger.getLogger(ClienteService.class.getName());
    
    /**
     * Obtiene un cliente por ID
     */
    public Respuesta getCliente(Long id) {
        try {
            if (id == null || id <= 0) {
                return new Respuesta(false, "Debe especificar el ID del cliente", "id inválido");
            }
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            
            Request request = new Request("ClienteController/cliente", "/{id}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Cliente", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo cliente.", ex);
            return new Respuesta(false, "Error obteniendo el cliente", "getCliente " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene todos los clientes
     */
    public Respuesta getClientes() {
        try {
            Request request = new Request("ClienteController/clientes");
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Clientes", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo clientes.", ex);
            return new Respuesta(false, "Error obteniendo los clientes", "getClientes " + ex.getMessage());
        }
    }
    
    /**
     * Busca un cliente por cédula
     */
    public Respuesta getClientePorCedula(String cedula) {
        try {
            if (cedula == null || cedula.trim().isEmpty()) {
                return new Respuesta(false, "Debe especificar la cédula", "cédula vacía");
            }
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("cedula", cedula);
            
            Request request = new Request("ClienteController/cliente/cedula", "/{cedula}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Cliente", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo cliente por cédula.", ex);
            return new Respuesta(false, "Error obteniendo cliente", "getClientePorCedula " + ex.getMessage());
        }
    }
    
    /**
     * Guarda un cliente (crear o actualizar)
     */
    public Respuesta guardarCliente(ClienteDto clienteDto) {
        try {
            if (clienteDto == null) {
                return new Respuesta(false, "Los datos del cliente son requeridos", "clienteDto es null");
            }
            
            // Validaciones básicas
            if (clienteDto.getNombre() == null || clienteDto.getNombre().trim().isEmpty()) {
                return new Respuesta(false, "El nombre del cliente es requerido", "nombre vacío");
            }
            
            Request request = new Request("ClienteController/cliente");
            request.post(clienteDto);
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "Cliente guardado correctamente", "", "Cliente", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando cliente.", ex);
            return new Respuesta(false, "Error guardando el cliente", "guardarCliente " + ex.getMessage());
        }
    }
    
    /**
     * Elimina un cliente
     */
    public Respuesta eliminarCliente(Long id) {
        try {
            if (id == null || id <= 0) {
                return new Respuesta(false, "Debe especificar el ID del cliente", "id inválido");
            }
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            
            Request request = new Request("ClienteController/cliente", "/{id}", parametros);
            request.delete();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            return new Respuesta(true, "Cliente eliminado correctamente", "");
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando cliente.", ex);
            return new Respuesta(false, "Error eliminando el cliente", "eliminarCliente " + ex.getMessage());
        }
    }
}
