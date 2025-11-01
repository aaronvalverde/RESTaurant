package cr.ac.una.wsrestuna;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Configures Jakarta RESTful Web Services for the application.
 * @author Juneau
 */
@ApplicationPath("ws")
public class JakartaRestConfiguration extends ResourceConfig {
    
    public JakartaRestConfiguration() {
        super();
        packages("cr.ac.una.wsrestuna.controller",
                 "io.swagger.v3.jaxrs2.integration.resources");
    }
}
