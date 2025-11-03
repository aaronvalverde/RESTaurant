package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.UsuarioDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


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
        
        System.out.println("AVISO: Usando método getUsuarios() obsoleto. Use obtenerTodosLosUsuarios()");
        return obtenerTodosLosUsuarios();
    }
    
    
    public Respuesta guardarUsuario(UsuarioDto usuarioDto){
        try {        
            
            if (usuarioDto == null) {
                return new Respuesta(false, "Datos de usuario inválidos", "UsuarioDto es null");
            }
            
            if (usuarioDto.getUsuario() == null || usuarioDto.getUsuario().trim().isEmpty()) {
                return new Respuesta(false, "El nombre de usuario es obligatorio", "Nombre de usuario vacío");
            }
            
            
            if (usuarioDto.getIdUsuario() == null && 
                (usuarioDto.getNuevaContrasena() == null || usuarioDto.getNuevaContrasena().trim().isEmpty())) {
                return new Respuesta(false, "La contraseña es obligatoria para usuarios nuevos", "Contraseña vacía");
            }
            
            
            if (usuarioDto.getNombre() == null || usuarioDto.getNombre().trim().isEmpty()) {
                usuarioDto.setNombre(usuarioDto.getUsuario()); 
            }
            
            
            if (usuarioDto.getEstado() == null) {
                usuarioDto.setEstado("A"); 
            }
            
            System.out.println("Guardando usuario: " + usuarioDto.getUsuario());
            Request request = new Request("UsuarioController/usuario");
            request.post(usuarioDto);
           
            if(request.isError()){
                System.err.println("Error guardando usuario: " + request.getError());
                
                String errorMsg = "Error de comunicación con el servidor";
                
                if (request.getError().contains("HTTP 400")) {
                    errorMsg = "Datos de usuario incorrectos o incompletos";
                } else if (request.getError().contains("HTTP 401") || request.getError().contains("HTTP 403")) {
                    errorMsg = "No tiene permisos para realizar esta operación";
                } else if (request.getError().contains("HTTP 404")) {
                    errorMsg = "Servicio no disponible actualmente";
                } else if (request.getError().contains("HTTP 500")) {
                    errorMsg = "Error interno del servidor";
                }
                
                return new Respuesta(false, errorMsg, "Error en la comunicación con el servidor");
            }
            
            String responseJson = request.getResponseBody();
            System.out.println("Respuesta recibida: " + responseJson);
            
            
            if (responseJson != null && responseJson.contains("\"estado\":false")) {
                
                int inicioMsg = responseJson.indexOf("\"mensaje\":");
                if (inicioMsg > 0) {
                    inicioMsg += 11; 
                    int finMsg = responseJson.indexOf("\"", inicioMsg);
                    if (finMsg > inicioMsg) {
                        String mensajeError = responseJson.substring(inicioMsg, finMsg);
                        return new Respuesta(false, mensajeError, "Error reportado por el servidor");
                    }
                }
                return new Respuesta(false, "Error al guardar el usuario", "Respuesta con estado falso");
            }
            
            return new Respuesta(true, "Usuario guardado correctamente", "", "Usuario", responseJson);

        } catch (Exception ex) {
            Logger.getLogger(UsuarioService.class.getName()).log(Level.SEVERE, "Ocurrió un error al guardar el usuario.", ex);
            ex.printStackTrace();
            return new Respuesta(false, "Ocurrió un error al guardar el usuario", 
                                "guardarUsuario " + ex.getMessage());
        }
    }
    
    
    public Respuesta obtenerTodosLosUsuarios() {
        try {
            
            System.out.println("Iniciando solicitud para obtener todos los usuarios");
            Request request = new Request("UsuarioController/usuarios");
            request.get();
            
            if(request.isError()){
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
                System.err.println("Formato de respuesta inesperado (esperaba array): " + responseJson);
                return new Respuesta(false, "Formato de respuesta no válido", "Esperaba un array de usuarios");
            }
            
            
            return new Respuesta(true, "", "", "Usuarios", responseJson);
            
        } catch (Exception ex) {
            Logger.getLogger(UsuarioService.class.getName())
                  .log(Level.SEVERE, "Error obteniendo todos los usuarios.", ex);
            ex.printStackTrace();
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