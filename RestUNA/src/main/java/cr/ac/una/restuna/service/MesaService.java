package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.MesaDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MesaService {
    
    private static final Logger LOG = Logger.getLogger(MesaService.class.getName());
    
    
    public Respuesta getMesa(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("MesaController/mesa", "/{id}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Mesa", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesa [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo la mesa.", "getMesa " + ex.getMessage());
        }
    }
    
    
    public Respuesta getMesas() {
        try {
            System.out.println("Iniciando solicitud para obtener todas las mesas");
            Request request = new Request("MesaController/mesas");
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
                return new Respuesta(false, "Formato de respuesta no válido", "Esperaba un array de mesas");
            }
            
            return new Respuesta(true, "", "", "Mesas", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas", ex);
            ex.printStackTrace();
            return new Respuesta(false, "Error obteniendo mesas.", "getMesas " + ex.getMessage());
        }
    }
    
    
    public Respuesta getMesasPorSeccion(Long idSeccion) {
        try {
            System.out.println("Obteniendo mesas de sección: " + idSeccion);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("idSeccion", idSeccion);
            Request request = new Request("MesaController/mesas/seccion", "/{idSeccion}", parametros);
            request.get();
            
            if (request.isError()) {
                System.err.println("Error: " + request.getError());
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            
            if (responseJson == null || responseJson.trim().isEmpty()) {
                return new Respuesta(false, "Respuesta vacía del servidor", "No se recibieron datos");
            }
            
            if (!responseJson.trim().startsWith("[")) {
                return new Respuesta(false, "Formato de respuesta no válido", "Esperaba un array de mesas");
            }
            
            
            System.out.println("JSON de mesas recibido: " + responseJson);
            
            return new Respuesta(true, "", "", "Mesas", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas de sección: " + idSeccion, ex);
            return new Respuesta(false, "Error obteniendo mesas.", "getMesasPorSeccion " + ex.getMessage());
        }
    }
    
    
    public Respuesta getMesasPorEstado(String estado) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("estado", estado);
            Request request = new Request("MesaController/mesas/estado", "/{estado}", parametros);
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            
            if (responseJson == null || responseJson.trim().isEmpty()) {
                return new Respuesta(false, "Respuesta vacía del servidor", "No se recibieron datos");
            }
            
            return new Respuesta(true, "", "", "Mesas", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas por estado: " + estado, ex);
            return new Respuesta(false, "Error obteniendo mesas.", "getMesasPorEstado " + ex.getMessage());
        }
    }
    
    
    public Respuesta getMesasLibres() {
        try {
            Request request = new Request("MesaController/mesas/libres");
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            
            if (responseJson == null || responseJson.trim().isEmpty()) {
                return new Respuesta(false, "Respuesta vacía del servidor", "No se recibieron datos");
            }
            
            return new Respuesta(true, "", "", "Mesas", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas libres", ex);
            return new Respuesta(false, "Error obteniendo mesas libres.", "getMesasLibres " + ex.getMessage());
        }
    }
    
    
    public Respuesta guardarMesa(MesaDto mesaDto) {
        try {
            if (mesaDto == null) {
                return new Respuesta(false, "Datos de mesa inválidos", "MesaDto es null");
            }
            
            if (mesaDto.getNumeroMesa() == null || mesaDto.getNumeroMesa().trim().isEmpty()) {
                return new Respuesta(false, "El número de mesa es obligatorio", "Número de mesa vacío");
            }
            
            if (mesaDto.getIdSeccion() == null || mesaDto.getIdSeccion() <= 0) {
                return new Respuesta(false, "La sección es obligatoria", "ID de sección inválido");
            }
            
            if (mesaDto.getEstado() == null) {
                mesaDto.setEstado("LIBRE");
            }
            
            System.out.println("Guardando mesa: " + mesaDto.getNumeroMesa() + " en sección: " + mesaDto.getIdSeccion());
            Request request = new Request("MesaController/mesa");
            request.post(mesaDto);
            
            if (request.isError()) {
                System.err.println("Error guardando mesa: " + request.getError());
                return new Respuesta(false, "Error guardando mesa", request.getError());
            }
            
            String responseJson = request.getResponseBody();
            System.out.println("Mesa guardada correctamente");
            
            return new Respuesta(true, "Mesa guardada correctamente", "", "Mesa", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando mesa", ex);
            ex.printStackTrace();
            return new Respuesta(false, "Error guardando mesa.", "guardarMesa " + ex.getMessage());
        }
    }
    
    
    public Respuesta guardarMesas(List<MesaDto> mesas) {
        try {
            System.out.println("Guardando " + mesas.size() + " mesas");
            Request request = new Request("MesaController/mesas");
            request.post(mesas);
            
            if (request.isError()) {
                System.err.println("Error guardando mesas: " + request.getError());
                return new Respuesta(false, "Error guardando mesas", request.getError());
            }
            
            String responseJson = request.getResponseBody();
            System.out.println("Mesas guardadas correctamente");
            
            return new Respuesta(true, "Mesas guardadas correctamente", "", "Mesas", responseJson);
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando mesas batch", ex);
            return new Respuesta(false, "Error guardando mesas.", "guardarMesas " + ex.getMessage());
        }
    }
    
    
    public Respuesta actualizarEstadoMesa(Long idMesa, String estado) {
        try {
            if (idMesa == null || idMesa <= 0) {
                return new Respuesta(false, "Debe especificar la mesa a actualizar", "idMesa inválido");
            }

            if (estado == null || estado.trim().isEmpty()) {
                return new Respuesta(false, "Debe indicar el nuevo estado de la mesa", "estado vacío");
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", idMesa);
            parametros.put("estado", estado);

            Request request = new Request("MesaController/mesa", "/{id}/estado/{estado}", parametros);
            request.put(null);

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String responseJson = request.getResponseBody();
            return new Respuesta(true, "Estado de mesa actualizado correctamente", "", "Mesa", responseJson);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error actualizando estado de mesa [" + idMesa + "]", ex);
            return new Respuesta(false, "Error actualizando estado.", "actualizarEstadoMesa " + ex.getMessage());
        }
    }
    
    
    public Respuesta eliminarMesa(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("MesaController/mesa", "/{id}", parametros);
            request.delete();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            return new Respuesta(true, "Mesa eliminada correctamente", "");
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando mesa [" + id + "]", ex);
            return new Respuesta(false, "Error eliminando mesa.", "eliminarMesa " + ex.getMessage());
        }
    }
}
