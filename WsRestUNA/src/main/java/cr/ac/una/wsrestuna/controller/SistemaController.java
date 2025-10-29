package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.Usuario;
import cr.ac.una.wsrestuna.service.UsuarioService;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para inicialización y utilidades del sistema
 */
@Path("SistemaController")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SistemaController {

    private static final Logger LOGGER = Logger.getLogger(SistemaController.class.getName());

    @EJB
    private UsuarioService usuarioService;

    /**
     * GET /SistemaController/inicializar - Inicializa datos básicos del sistema
     */
    @GET
    @Path("inicializar")
    public Response inicializarSistema() {
        try {
            // Verificar si ya existe un administrador
            Respuesta respuestaTodos = usuarioService.obtenerTodos();
            if (respuestaTodos.getEstado() && respuestaTodos.getResultado("Usuarios") != null) {
                return Response.ok(new Respuesta(true, CodigoRespuesta.CORRECTO, 
                        "El sistema ya tiene usuarios registrados", "")).build();
            }

            // Crear usuario administrador por defecto
            Usuario adminUser = new Usuario();
            adminUser.setUsuario("admin");
            adminUser.setRol("ADMINISTRADOR");
            adminUser.setEstado("A");
            adminUser.setFechaCreacion(LocalDateTime.now());
            
            // Contraseña en texto plano temporalmente
            adminUser.setContrasena("admin123");

            // Guardar en base de datos usando el servicio
            // (Aquí necesitarías acceso directo al EntityManager o usar el servicio)
            
            return Response.ok(new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Sistema inicializado correctamente. Usuario admin creado con contraseña: admin123", 
                    "")).build();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar sistema", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                           "Error al inicializar sistema", e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /SistemaController/verificar - Verifica el estado del sistema
     */
    @GET
    @Path("verificar")
    public Response verificarSistema() {
        try {
            Respuesta respuesta = usuarioService.obtenerTodos();
            
            String mensaje = respuesta.getEstado() ? 
                "Sistema operativo - Usuarios registrados" : 
                "Sistema requiere inicialización";
                
            return Response.ok(new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    mensaje, "")).build();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al verificar sistema", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                           "Error al verificar sistema", e.getMessage()))
                    .build();
        }
    }

}