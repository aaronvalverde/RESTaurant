package cr.ac.una.restuna.service;

import cr.ac.una.restuna.dto.UsuarioDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para operaciones con usuarios siguiendo patrón UNA Planilla
 */
public class UsuarioService {
    
    public Respuesta getUsuario(String usuario, String clave){
        try{
                Map<String,Object> parametros = new HashMap<>();
                parametros.put("usuario", usuario);
                parametros.put("contrasena", clave);
                Request request = new Request("UsuarioController/usuario","/{usuario}/{contrasena}", parametros);
                request.get();
                
                if(request.isError()){
                
                    return new Respuesta(false, request.getError(), "");
                
                }
                
                // Por simplicidad, por ahora retornamos la respuesta raw
                // En implementación completa necesitarías deserialización apropiada
                String responseJson = request.getResponseBody();
        
            return new Respuesta(true," ", " ", "Usuario", responseJson);
        } catch (Exception ex) {
            Logger.getLogger(UsuarioService.class.getName()).log(Level.SEVERE, "Error obteniendo el usuario [" + usuario + "]", ex);
            return new Respuesta(false, "Error obteniendo el usuario.", "getUsuario " + ex.getMessage());
        } 
    }
    
    public Respuesta getUsuario(Long id){
    try {   
        Map<String,Object> parametros = new HashMap<>();
        parametros.put("id", id);  
        Request request = new Request("UsuarioController/usuario", "/{id}", parametros);
        request.get();
  
        if(request.isError()){
            return new Respuesta(false, request.getError(), "");
        }

        String responseJson = request.getResponseBody();
        return new Respuesta(true, "", "", "Usuario", responseJson);

    } catch (Exception ex) {
        Logger.getLogger(UsuarioService.class.getName())
              .log(Level.SEVERE, "Error obteniendo el usuario [" + id + "]", ex);
        return new Respuesta(false, "Error obteniendo el usuario.", "getUsuario " + ex.getMessage());
    }
}
    
    public Respuesta renovarToken(){
    try {
        Request request = new Request("UsuarioController/renovar");
        request.getRenewal();
        
        if(request.isError()){
            return new Respuesta(false, request.getError(), "");
        }
        
        String token = request.getResponseBody();
        return new Respuesta(true, "", "", "Token", token);
    } catch (Exception ex) {
        Logger.getLogger(UsuarioService.class.getName())
              .log(Level.SEVERE, "Error renovando el token", ex);
        return new Respuesta(false, "Error renovando el token", "renovarToken " + ex.getMessage());
    }
}

    
    public Respuesta getUsuarios() {
        try {
            // Usar el endpoint simplificado sin parámetros
            Request request = new Request("UsuarioController/usuarios");
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Usuarios", responseJson);
            
        } catch (Exception ex) {
            Logger.getLogger(UsuarioService.class.getName()).log(Level.SEVERE, "Error obteniendo usuarios.", ex);
            return new Respuesta(false, "Error obteniendo usuarios.", "getUsuarios " + ex.getMessage());
        }
    }
    
    @Deprecated
    public Respuesta getUsuarios(String nombre, String apellidos, String usuario, String correo) {
        // Método mantenido por compatibilidad - redirige al nuevo método simplificado
        return getUsuarios();
    }
    
    public Respuesta guardarUsuario(UsuarioDto usuarioDto){
        try {        
        Request request = new Request("UsuarioController/usuario");
        request.post(usuarioDto);
       
        if(request.isError()){
            return new Respuesta(false, request.getError(), "");
        }
        
        String responseJson = request.getResponseBody();
        
        return new Respuesta(true, "", "", "Usuario", responseJson);

        } catch (Exception ex) {
            Logger.getLogger(UsuarioService.class.getName()).log(Level.SEVERE, "Ocurrio un error al guardar el usuario.", ex);
            return new Respuesta(false, "Ocurrio un error al guardar el usuario.", "guardarUsuario " + ex.getMessage());
        }
    }
    
    /**
     * Método simplificado para obtener todos los usuarios sin parámetros
     */
    public Respuesta obtenerTodosLosUsuarios() {
        try {
            // Usar endpoint simple sin parámetros
            Request request = new Request("UsuarioController/usuarios");
            request.get();
            
            if(request.isError()){
                return new Respuesta(false, request.getError(), "");
            }
            
            String responseJson = request.getResponseBody();
            return new Respuesta(true, "", "", "Usuarios", responseJson);
            
        } catch (Exception ex) {
            Logger.getLogger(UsuarioService.class.getName())
                  .log(Level.SEVERE, "Error obteniendo todos los usuarios.", ex);
            return new Respuesta(false, "Error obteniendo usuarios.", "obtenerTodosLosUsuarios " + ex.getMessage());
        }
    }
    
    public Respuesta eliminarUsuario(Long id){
        try{Map<String,Object> parametros = new HashMap<>();
        parametros.put("id", id);       
        Request request = new Request("UsuarioController/usuario", "/{id}", parametros);
        request.delete();
        
        if(request.isError()){
            return new Respuesta(false, request.getError(), "");
        }

        return new Respuesta(true, "", "");
        } catch (Exception ex) {
            Logger.getLogger(UsuarioService.class.getName()).log(Level.SEVERE, "Error eliminando el usuario", ex);
            return new Respuesta(false, "Error eliminando el usuario.", "eliminarUsuario " + ex.getMessage());
        }
    }
}