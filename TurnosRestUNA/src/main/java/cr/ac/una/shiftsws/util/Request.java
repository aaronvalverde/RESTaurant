package cr.ac.una.shiftsws.util;

import cr.ac.una.shiftsws.model.UsuarioDto;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase utilitaria simplificada para manejar peticiones HTTP relacionadas con login.
 */
public class Request {

    private static final Logger LOGGER = Logger.getLogger(Request.class.getName());
    private static final String BASE_URL = ApplicationProperties.getRestBaseUrl();
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
     * Realiza una petición POST (usada para login).
     */
    public void post(Object body) {
        HttpURLConnection connection = null;
        try {
            String url = BASE_URL + endpoint;
            System.out.println("Realizando POST a: " + url);
            connection = createConnection(url, "POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);

            if (body != null) {
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
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    /**
     * Convierte un UsuarioDto a JSON (para login).
     */
    private String convertirObjetoAJson(Object objeto) {
        if (objeto == null) return "{}";
        if (objeto instanceof UsuarioDto usuario) {
            StringBuilder json = new StringBuilder("{");
            if (usuario.getUsuario() != null)
                json.append("\"usuario\":\"").append(escaparJson(usuario.getUsuario())).append("\"");
            if (usuario.getNuevaContrasena() != null)
                json.append(",\"contrasena\":\"").append(escaparJson(usuario.getNuevaContrasena())).append("\"");
            json.append("}");
            return json.toString();
        }
        return "{}";
    }

    private String escaparJson(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Procesa la respuesta del servidor.
     */
    private void processResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        System.out.println("HTTP Response Code: " + responseCode + ", URL: " + connection.getURL());

        InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();

        if (inputStream == null) {
            isError = true;
            error = "No se pudo leer la respuesta del servidor";
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                response.append(line);
            responseBody = response.toString();
        }

        if (responseCode < 200 || responseCode >= 300) {
            isError = true;
            switch (responseCode) {
                case 400 -> error = "Error 400: Datos incorrectos";
                case 401 -> error = "Error 401: Credenciales inválidas";
                case 404 -> error = "Error 404: Recurso no encontrado";
                case 500 -> error = "Error 500: Error interno del servidor";
                default -> error = "Error " + responseCode + ": No se pudo completar la solicitud";
            }
        }
    }

    private HttpURLConnection createConnection(String urlString, String method) throws Exception {
        URL url = URI.create(urlString).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", CONTENT_TYPE);
        connection.setRequestProperty("Accept", CONTENT_TYPE);
        return connection;
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

    public void getRenewal() {
        try {
            String url = BASE_URL + endpoint;
            HttpURLConnection connection = createConnection(url, "GET");
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error renovando token", e);
        }
    }

    public void delete() {
        try {
            String url = buildUrl();
            HttpURLConnection connection = createConnection(url, "DELETE");
            processResponse(connection);
        } catch (Exception e) {
            handleError("Error en petición DELETE", e);
        }
    }
}
