package cr.ac.una.shiftsws.service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import cr.ac.una.shiftsws.util.Respuesta;
import cr.ac.una.shiftsws.util.Request;

public class AuthenticationService {
    private String endPoint = "http://localhost:8080/WsRestUNA/";

    public Respuesta getUsuario(String usuario, String clave) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("usuario", usuario);
            parametros.put("contrasena", clave);
            Request request = new Request("UsuarioController/usuario", "/{usuario}/{contrasena}", parametros);
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            // Por simplicidad, por ahora retornamos la respuesta raw
            // En implementación completa necesitarías deserialización apropiada
            String responseJson = request.getResponseBody();

            return new Respuesta(true, " ", " ", "Usuario", responseJson);
        } catch (Exception ex) {
            Logger.getLogger(UsuarioService.class.getName()).log(Level.SEVERE, "Error obteniendo el usuario [" + usuario + "]", ex);
            return new Respuesta(false, "Error obteniendo el usuario.", "getUsuario " + ex.getMessage());
        }
    }
}
