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
    private static final String BASE_URL = "http://localhost:8080/WsRestUNA/resources/";
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
        try {
            String url = buildUrl();
            System.out.println("DEBUG - Request GET URL: " + url);
            HttpURLConnection connection = createConnection(url, "GET");
            processResponse(connection);
            System.out.println("DEBUG - Response received: " + responseBody);
        } catch (Exception e) {
            handleError("Error en petición GET", e);
        }
    }
    
    /**
     * Realiza una petición POST
     */
    public void post(Object body) {
        try {
            String url = BASE_URL + endpoint;
            HttpURLConnection connection = createConnection(url, "POST");
            connection.setDoOutput(true);
            
            if (body != null) {
                // Por simplicidad, usamos toString del objeto
                // En una implementación real usarías Jackson o similar
                String jsonBody = body.toString();
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error en petición POST", e);
        }
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
        
        try (InputStream inputStream = (responseCode >= 200 && responseCode < 300) 
                ? connection.getInputStream() 
                : connection.getErrorStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            responseBody = response.toString();
            
            if (responseCode < 200 || responseCode >= 300) {
                isError = true;
                error = "HTTP " + responseCode + ": " + responseBody;
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