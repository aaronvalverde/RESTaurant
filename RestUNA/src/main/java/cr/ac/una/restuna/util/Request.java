package cr.ac.una.restuna.util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase utilitaria para realizar peticiones HTTP siguiendo patrón UNA Planilla
 * Versión simplificada sin Jackson para compatibilidad
 */
public class Request {
    
    private static final Logger LOGGER = Logger.getLogger(Request.class.getName());
    private static final String BASE_URL = "http://localhost:8080/WsRestUNA/resources/"; // Asegúrate que este puerto coincide con tu servidor
    private static final String CONTENT_TYPE = "application/json";
    
    private String endpoint;
    private String pathTemplate;
    private Map<String, Object> parametros;
    private String error;
    private boolean isError;
    private String responseBody;
    
    public Request(String endpoint) {
        this.endpoint = endpoint;
        this.isError = false;
    }
    
    public Request(String endpoint, String pathTemplate, Map<String, Object> parametros) {
        this(endpoint);
        this.pathTemplate = pathTemplate;
        this.parametros = parametros;
    }
    
    /**
     * Realiza una petición GET
     */
    public void get() {
        HttpURLConnection connection = null;
        try {
            String url = buildUrl();
            System.out.println("Realizando GET a: " + url);
            connection = createConnection(url, "GET");
            connection.setConnectTimeout(10000); // 10 segundos
            connection.setReadTimeout(15000);    // 15 segundos
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error en petición GET", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Realiza una petición POST
     */
    public void post(Object body) {
        HttpURLConnection connection = null;
        try {
            String url = BASE_URL + endpoint;
            System.out.println("Realizando POST a: " + url);
            connection = createConnection(url, "POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000); // 10 segundos
            connection.setReadTimeout(15000);    // 15 segundos
            
            if (body != null) {
                // Convertir el objeto a JSON manualmente
                String jsonBody = convertirObjetoAJson(body);
                System.out.println("Enviando JSON: " + jsonBody);
                
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error en petición POST", e);
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Convierte un objeto a formato JSON de manera simple
     * Para objetos complejos, se debería usar una biblioteca como Jackson
     */
    private String convertirObjetoAJson(Object objeto) {
        if (objeto == null) return "{}";
        
        // Si es un DTO, construimos un JSON básico con sus propiedades
        if (objeto instanceof cr.ac.una.restuna.dto.UsuarioDto) {
            cr.ac.una.restuna.dto.UsuarioDto usuarioDto = (cr.ac.una.restuna.dto.UsuarioDto) objeto;
            StringBuilder jsonBuilder = new StringBuilder("{");
            
            // Añadir ID si existe
            if (usuarioDto.getIdUsuario() != null) {
                jsonBuilder.append("\"idUsuario\":").append(usuarioDto.getIdUsuario()).append(",");
            }
            
            // Añadir propiedades obligatorias
            jsonBuilder.append("\"usuario\":\"").append(escaparJson(usuarioDto.getUsuario())).append("\"");
            
            // Añadir nombre si existe - asegurarse de que siempre se envía
            if (usuarioDto.getNombre() != null && !usuarioDto.getNombre().isEmpty()) {
                jsonBuilder.append(",\"nombre\":\"").append(escaparJson(usuarioDto.getNombre())).append("\"");
            } else {
                jsonBuilder.append(",\"nombre\":\"").append(escaparJson(usuarioDto.getUsuario())).append("\"");
            }
            
            // Añadir resto de propiedades
            jsonBuilder.append(",\"rol\":\"").append(escaparJson(usuarioDto.getRol())).append("\"");
            jsonBuilder.append(",\"estado\":\"").append(escaparJson(usuarioDto.getEstado())).append("\"");
            
            // Añadir contraseña si existe
            if (usuarioDto.getNuevaContrasena() != null && !usuarioDto.getNuevaContrasena().isEmpty()) {
                jsonBuilder.append(",\"nuevaContrasena\":\"").append(escaparJson(usuarioDto.getNuevaContrasena())).append("\"");
            }
            
            jsonBuilder.append("}");
            return jsonBuilder.toString();
        }
        
        // Para otros tipos de objetos, usar toString como fallback
        return objeto.toString();
    }
    
    /**
     * Escapa caracteres especiales en strings para JSON
     */
    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    /**
     * Realiza una petición DELETE
     */
    public void delete() {
        try {
            String url = buildUrl();
            HttpURLConnection connection = createConnection(url, "DELETE");
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error en petición DELETE", e);
        }
    }
    
    /**
     * Método para autenticación con token
     */
    public void getToken() {
        try {
            String url = buildUrl();
            HttpURLConnection connection = createConnection(url, "GET");
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error obteniendo token", e);
        }
    }
    
    /**
     * Método para renovar token
     */
    public void getRenewal() {
        try {
            String url = BASE_URL + endpoint;
            HttpURLConnection connection = createConnection(url, "GET");
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error renovando token", e);
        }
    }
    
    /**
     * Lee la respuesta como una entidad específica
     * Versión simplificada que retorna el JSON como string
     */
    public <T> T readEntity(Class<T> entityClass) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return null;
            }
            // Por simplicidad, retornamos el JSON como String si es String.class
            if (entityClass == String.class) {
                @SuppressWarnings("unchecked")
                T result = (T) responseBody;
                return result;
            }
            // Para otros tipos necesitarías deserialización manual o Jackson
            return null;
        } catch (Exception e) {
            handleError("Error leyendo respuesta", e);
            return null;
        }
    }
    
    /**
     * Lee la respuesta como una lista usando GenericType simulado
     */
    public <T> T readEntity(GenericType<T> genericType) {
        // Por simplicidad, retornamos null
        // En implementación real necesitarías deserialización apropiada
        return null;
    }
    
    private String buildUrl() {
        String url = BASE_URL + endpoint;
        if (pathTemplate != null && parametros != null) {
            String path = pathTemplate;
            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                path = path.replace("{" + entry.getKey() + "}", 
                                  entry.getValue() != null ? entry.getValue().toString() : "");
            }
            url += path;
        }
        return url;
    }
    
    private HttpURLConnection createConnection(String urlString, String method) throws Exception {
        URL url = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", CONTENT_TYPE);
        connection.setRequestProperty("Accept", CONTENT_TYPE);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);
        return connection;
    }
    
    private void processResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        System.out.println("HTTP Response Code: " + responseCode + ", URL: " + connection.getURL());
        
        InputStream inputStream = null;
        try {
            // Intentar obtener el stream adecuado según el código de respuesta
            inputStream = (responseCode >= 200 && responseCode < 300) 
                    ? connection.getInputStream() 
                    : connection.getErrorStream();
            
            // Si ambos son nulos (raro pero posible), manejar el caso
            if (inputStream == null) {
                isError = true;
                error = "Error: No se pudo leer la respuesta del servidor";
                return;
            }
            
            // Leer la respuesta
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            responseBody = response.toString();
            reader.close();
            
            // Manejar códigos de error HTTP
            if (responseCode < 200 || responseCode >= 300) {
                isError = true;
                
                // Filtrar contenido HTML para que no se muestre al usuario final
                String errorBody = responseBody;
                
                // Verificar si la respuesta contiene HTML y eliminarla
                if (errorBody != null && (errorBody.contains("<html") || errorBody.contains("<!DOCTYPE"))) {
                    errorBody = "Respuesta con formato HTML no mostrable";
                }
                
                // Guardar error completo en logs pero no mostrarlo al usuario
                System.err.println("Error HTTP: HTTP " + responseCode + ": " + errorBody);
                
                // Generar mensaje amigable según el código de error
                switch (responseCode) {
                    case 400:
                        error = "Error 400: Los datos enviados son incorrectos o incompletos";
                        break;
                    case 401:
                        error = "Error 401: No está autorizado para esta operación";
                        break;
                    case 403:
                        error = "Error 403: No tiene permisos para acceder a este recurso";
                        break;
                    case 404:
                        error = "Error 404: El recurso solicitado no existe";
                        break;
                    case 500:
                        error = "Error 500: Error interno del servidor";
                        break;
                    default:
                        error = "Error " + responseCode + ": No se pudo completar la solicitud";
                }
            } else {
                System.out.println("Respuesta recibida correctamente, longitud: " + 
                                  (responseBody != null ? responseBody.length() : 0));
            }
        } catch (Exception e) {
            isError = true;
            error = "Error de comunicación con el servidor";
            System.err.println("Error técnico completo: " + e.getMessage());
            e.printStackTrace(); // Solo para log, no para usuario
            throw e;
        } finally {
            // Cerrar el stream si existe
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.err.println("Error cerrando stream: " + e.getMessage());
                }
            }
        }
    }
    
    private void handleError(String message, Exception e) {
        isError = true;
        error = message + ": " + e.getMessage();
        LOGGER.log(Level.SEVERE, message, e);
    }
    
    public boolean isError() {
        return isError;
    }
    
    public String getError() {
        return error;
    }
    
    public String getResponseBody() {
        return responseBody;
    }
    
    /**
     * Clase interna para simular GenericType
     */
    public static class GenericType<T> {
        // Implementación básica para compatibilidad
    }
}