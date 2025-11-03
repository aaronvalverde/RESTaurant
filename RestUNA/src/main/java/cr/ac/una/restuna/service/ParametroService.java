package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.ParametroDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para operaciones con parámetros de configuración
 * Maneja llamadas HTTP al servidor para gestionar parámetros por usuario
 */
public class ParametroService {

    private static final Logger LOGGER = Logger.getLogger(ParametroService.class.getName());

    /**
     * Obtiene todos los parámetros de un usuario
     * 
     * @param idUsuario ID del usuario
     * @return Respuesta con la lista de parámetros en JSON
     */
    public Respuesta getParametrosPorUsuario(Long idUsuario) {
        try {
            if (idUsuario == null) {
                return new Respuesta(false, "El ID del usuario es requerido", "idUsuario es null");
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("idUsuario", idUsuario);
            
            Request request = new Request("ParametroController/parametros/usuario", "/{idUsuario}", parametros);
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Parametros", responseJson);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error obteniendo parámetros del usuario [" + idUsuario + "]", ex);
            return new Respuesta(false, "Error obteniendo los parámetros.", 
                    "getParametrosPorUsuario " + ex.getMessage());
        }
    }

    /**
     * Obtiene un parámetro específico por clave y usuario
     * 
     * @param idUsuario ID del usuario
     * @param clave Clave del parámetro
     * @return Respuesta con el parámetro en JSON
     */
    public Respuesta getParametroPorUsuarioYClave(Long idUsuario, String clave) {
        try {
            if (idUsuario == null) {
                return new Respuesta(false, "El ID del usuario es requerido", "idUsuario es null");
            }

            if (clave == null || clave.trim().isEmpty()) {
                return new Respuesta(false, "La clave del parámetro es requerida", "clave vacía");
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("idUsuario", idUsuario);
            parametros.put("clave", clave);
            
            Request request = new Request("ParametroController/parametro/usuario", 
                    "/{idUsuario}/clave/{clave}", parametros);
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Parametro", responseJson);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error obteniendo parámetro [" + clave + "] del usuario [" + idUsuario + "]", ex);
            return new Respuesta(false, "Error obteniendo el parámetro.", 
                    "getParametroPorUsuarioYClave " + ex.getMessage());
        }
    }

    /**
     * Guarda un parámetro (crear o actualizar)
     * 
     * @param parametroDto DTO con los datos del parámetro
     * @return Respuesta con el parámetro guardado en JSON
     */
    public Respuesta guardarParametro(ParametroDto parametroDto) {
        try {
            if (parametroDto == null) {
                return new Respuesta(false, "Los datos del parámetro son requeridos", "parametroDto es null");
            }

            if (parametroDto.getIdUsuario() == null) {
                return new Respuesta(false, "El ID del usuario es requerido", "idUsuario es null");
            }

            if (parametroDto.getClave() == null || parametroDto.getClave().trim().isEmpty()) {
                return new Respuesta(false, "La clave del parámetro es requerida", "clave vacía");
            }

            Request request = new Request("ParametroController/parametro");
            request.post(parametroDto);

            if (request.isError()) {
                LOGGER.severe("Error guardando parámetro: " + request.getError());
                String errorMsg = "Error de comunicación con el servidor";

                if (request.getError().contains("HTTP 400")) {
                    errorMsg = "Datos del parámetro incorrectos o incompletos";
                } else if (request.getError().contains("HTTP 401") || request.getError().contains("HTTP 403")) {
                    errorMsg = "No tiene permisos para realizar esta operación";
                } else if (request.getError().contains("HTTP 404")) {
                    errorMsg = "Usuario no encontrado";
                } else if (request.getError().contains("HTTP 500")) {
                    errorMsg = "Error interno del servidor";
                }

                return new Respuesta(false, errorMsg, request.getError());
            }

            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Parametro", responseJson);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error guardando parámetro", ex);
            return new Respuesta(false, "Error guardando el parámetro.", 
                    "guardarParametro " + ex.getMessage());
        }
    }

    /**
     * Guarda múltiples parámetros (batch save)
     * 
     * @param parametrosDto Lista de DTOs con los datos de los parámetros
     * @return Respuesta con los parámetros guardados en JSON
     */
    public Respuesta guardarParametros(List<ParametroDto> parametrosDto) {
        try {
            if (parametrosDto == null || parametrosDto.isEmpty()) {
                return new Respuesta(false, "La lista de parámetros es requerida", "lista vacía");
            }

            Request request = new Request("ParametroController/parametros");
            request.post(parametrosDto);

            if (request.isError()) {
                LOGGER.severe("Error guardando parámetros: " + request.getError());
                String errorMsg = "Error de comunicación con el servidor";

                if (request.getError().contains("HTTP 400")) {
                    errorMsg = "Datos de parámetros incorrectos o incompletos";
                } else if (request.getError().contains("HTTP 401") || request.getError().contains("HTTP 403")) {
                    errorMsg = "No tiene permisos para realizar esta operación";
                } else if (request.getError().contains("HTTP 404")) {
                    errorMsg = "Usuario no encontrado";
                } else if (request.getError().contains("HTTP 500")) {
                    errorMsg = "Error interno del servidor";
                }

                return new Respuesta(false, errorMsg, request.getError());
            }

            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Parametros", responseJson);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error guardando parámetros", ex);
            return new Respuesta(false, "Error guardando los parámetros.", 
                    "guardarParametros " + ex.getMessage());
        }
    }

    /**
     * Elimina un parámetro
     * 
     * @param id ID del parámetro a eliminar
     * @return Respuesta indicando si se eliminó correctamente
     */
    public Respuesta eliminarParametro(Long id) {
        try {
            if (id == null || id <= 0) {
                return new Respuesta(false, "Debe especificar el parámetro a eliminar", "id inválido");
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            
            Request request = new Request("ParametroController/parametro", "/{id}", parametros);
            request.delete();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            return new Respuesta(true, "Parámetro eliminado correctamente", "");

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error eliminando parámetro [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando el parámetro.", 
                    "eliminarParametro " + ex.getMessage());
        }
    }
}
