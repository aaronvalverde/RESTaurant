package cr.ac.una.restuna.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for reading application level configuration from config/properties.ini.
 */
public final class ApplicationProperties {

    private static final Logger LOGGER = Logger.getLogger(ApplicationProperties.class.getName());
    private static final String CONFIG_FILE = "config/properties.ini";
    private static final String DEFAULT_REST_BASE_URL = "http://localhost:8080/WsRestUNA/ws/";
    private static final Properties PROPERTIES = loadProperties();

    private ApplicationProperties() {
        // Utility class
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        Path configPath = resolveConfigPath();

        try (InputStream input = openInputStream(configPath)) {
            if (input != null) {
                properties.load(input);
            } else {
                LOGGER.log(Level.WARNING,
                        "No se encontró el archivo {0}; se usarán valores por defecto.",
                        CONFIG_FILE);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,
                    "Error cargando las propiedades desde {0}: {1}",
                    new Object[]{CONFIG_FILE, e.getMessage()});
        }

        return properties;
    }

    private static Path resolveConfigPath() {
        Path directPath = Paths.get(CONFIG_FILE);
        if (Files.exists(directPath)) {
            return directPath;
        }

        Path workingDirPath = Paths.get(System.getProperty("user.dir", ".")).resolve(CONFIG_FILE);
        if (Files.exists(workingDirPath)) {
            return workingDirPath;
        }

        return workingDirPath;
    }

    private static InputStream openInputStream(Path configPath) throws IOException {
        if (configPath != null && Files.exists(configPath)) {
            return Files.newInputStream(configPath);
        }

        // Fallback: try to read it from the classpath (useful if bundled with resources)
        InputStream classpathStream = ApplicationProperties.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE.replace("\\", "/"));
        if (classpathStream == null) {
            LOGGER.log(Level.FINE,
                    "No se encontró {0} en el sistema de archivos ni en el classpath.",
                    CONFIG_FILE);
        }
        return classpathStream;
    }

    public static String getRestBaseUrl() {
        String url = PROPERTIES.getProperty("rest.url", DEFAULT_REST_BASE_URL);
        url = url.trim();
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        return url;
    }

    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }
}
