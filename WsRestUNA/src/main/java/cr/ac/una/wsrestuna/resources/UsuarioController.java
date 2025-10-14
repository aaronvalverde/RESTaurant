package cr.ac.una.wsrestuna.resources;

import cr.ac.una.wsrestuna.model.UsuarioDto;
import cr.ac.una.wsrestuna.service.UsuarioService;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador REST para la gestión de usuarios
 * Siguiendo el patrón de UNA Planilla con endpoints específicos
 */
@Path("UsuarioController")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioController {

    private static final Logger LOGGER = Logger.getLogger(UsuarioController.class.getName());

    @EJB
    private UsuarioService usuarioService;

    /**
     * GET /UsuarioController/usuario/{usuario}/{contrasena} - Autenticación siguiendo patrón UNA Planilla
     */
    @GET
    @Path("usuario/{usuario}/{contrasena}")
    public Response getUsuario(@PathParam("usuario") String usuario, @PathParam("contrasena") String contrasena) {
        try {
            Respuesta respuesta = usuarioService.autenticar(usuario, contrasena);
            return crearRespuestaHttp(respuesta);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al autenticar usuario", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                           "Error interno del servidor", e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /UsuarioController/usuario/{id} - Obtiene un usuario por ID
     */
    @GET
    @Path("usuario/{id}")
    public Response getUsuario(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = usuarioService.obtenerPorId(id);
            return crearRespuestaHttp(respuesta);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuario por ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                           "Error interno del servidor", e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /UsuarioController/usuarios/{nombre}/{apellidos}/{usuario}/{correo} - Búsqueda con filtros
     */
    @GET
    @Path("usuarios/{nombre}/{apellidos}/{usuario}/{correo}")
    public Response getUsuarios(@PathParam("nombre") String nombre, 
                               @PathParam("apellidos") String apellidos,
                               @PathParam("usuario") String usuario,
                               @PathParam("correo") String correo) {
        try {
            // Aquí implementaremos búsqueda con filtros cuando sea necesario
            Respuesta respuesta = usuarioService.obtenerTodos();
            return crearRespuestaHttp(respuesta);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios con filtros", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                           "Error interno del servidor", e.getMessage()))
                    .build();
        }
    }

    /**
     * POST /UsuarioController/usuario - Crea o actualiza un usuario
     */
    @POST
    @Path("usuario")
    public Response guardarUsuario(@Valid UsuarioDto usuarioDto) {
        try {
            // Validaciones básicas adicionales en el controlador
            if (usuarioDto != null && usuarioDto.getNuevaContrasena() != null && 
                usuarioDto.getNuevaContrasena().length() < 6) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                               "La contraseña debe tener al menos 6 caracteres", 
                               "Contraseña muy corta"))
                        .build();
            }

            Respuesta respuesta;
            if (usuarioDto.getIdUsuario() == null) {
                respuesta = usuarioService.crear(usuarioDto);
            } else {
                respuesta = usuarioService.actualizar(usuarioDto);
            }
            
            return crearRespuestaHttp(respuesta);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar usuario", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                           "Error interno del servidor", e.getMessage()))
                    .build();
        }
    }

    /**
     * GET /UsuarioController/usuarios - Obtiene todos los usuarios sin filtros
     */
    @GET
    @Path("usuarios")
    public Response obtenerTodosLosUsuarios() {
        try {
            Respuesta respuesta = usuarioService.obtenerTodos();
            return crearRespuestaHttp(respuesta);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                           "Error interno del servidor", e.getMessage()))
                    .build();
        }
    }

    /**
     * DELETE /UsuarioController/usuario/{id} - Elimina (desactiva) un usuario
     */
    @DELETE
    @Path("usuario/{id}")
    public Response eliminarUsuario(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = usuarioService.eliminar(id);
            return crearRespuestaHttp(respuesta);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario con ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                           "Error interno del servidor", e.getMessage()))
                    .build();
        }
    }

    /**
     * Convierte una respuesta del servicio a una respuesta HTTP
     */
    private Response crearRespuestaHttp(Respuesta respuesta) {
        if (respuesta.getEstado()) {
            // Respuesta exitosa
            return Response.ok(respuesta).build();
        } else {
            // Respuesta con error - mapear códigos de respuesta a códigos HTTP
            Response.Status status;
            switch (respuesta.getCodigoRespuesta()) {
                case ERROR_CLIENTE:
                    status = Response.Status.BAD_REQUEST;
                    break;
                case ERROR_NOENCONTRADO:
                    status = Response.Status.NOT_FOUND;
                    break;
                case ERROR_INTERNO:
                    status = Response.Status.INTERNAL_SERVER_ERROR;
                    break;
                case ERROR_PERMISOS:
                    status = Response.Status.UNAUTHORIZED;
                    break;
                case ERROR_SIN_CONTENIDO:
                    status = Response.Status.NO_CONTENT;
                    break;
                default:
                    status = Response.Status.INTERNAL_SERVER_ERROR;
                    break;
            }
            return Response.status(status).entity(respuesta).build();
        }
    }
}