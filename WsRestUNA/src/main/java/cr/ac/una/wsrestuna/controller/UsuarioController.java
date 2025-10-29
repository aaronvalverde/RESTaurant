package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.UsuarioDto;
import cr.ac.una.wsrestuna.service.UsuarioService;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
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
    public Response validarUsuario(@PathParam("usuario") String usuario, @PathParam("contrasena") String contrasena) {
        try {
            Respuesta respuesta = usuarioService.validarUsuario(usuario, contrasena);
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                       .entity(respuesta.getMensaje()).build();
            }
            UsuarioDto usuarioDto = (UsuarioDto)respuesta.getResultado("Usuario");
            return Response.ok(usuarioDto).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al autenticar usuario", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo el usuario.").build();
        }
    }

    /**
     * GET /UsuarioController/usuario/{id} - Obtiene un usuario por ID
     */
    @GET
    @Path("usuario/{id}")
    public Response getUsuario(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = usuarioService.getUsuario(id);
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                       .entity(respuesta.getMensaje()).build();
            }
            UsuarioDto usuarioDto = (UsuarioDto)respuesta.getResultado("Usuario");
            return Response.ok(usuarioDto).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuario", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo el usuario.").build();
        }
    }

    /**
     * GET /UsuarioController/usuarios/{nombre}/{usuario}/{rol}/{estado} - Búsqueda con filtros
     */
    @GET
    @Path("usuarios/{nombre}/{usuario}/{rol}/{estado}")
    public Response getUsuarios(@PathParam("nombre") String nombre, 
                               @PathParam("usuario") String usuario,
                               @PathParam("rol") String rol,
                               @PathParam("estado") String estado) {
        try {
            Respuesta respuesta = usuarioService.getUsuarios(nombre, usuario, rol, estado);
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                       .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(new GenericEntity<List<UsuarioDto>>
                ((List<UsuarioDto>)respuesta.getResultado("Usuarios")){}).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo los usuarios.").build();
        }
    }

    /**
     * POST /UsuarioController/usuario - Crea o actualiza un usuario
     */
    @POST
    @Path("usuario")
    public Response guardarUsuario(UsuarioDto usuarioDto) {
        try {
            Respuesta respuesta = usuarioService.guardarUsuario(usuarioDto);
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                       .entity(respuesta.getMensaje()).build();
            }
            return Response.ok((UsuarioDto)respuesta.getResultado("Usuario")).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar usuario", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error guardando el usuario.").build();
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
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                       .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(new GenericEntity<List<UsuarioDto>>
                ((List<UsuarioDto>)respuesta.getResultado("Usuarios")){}).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo los usuarios.").build();
        }
    }

    /**
     * DELETE /UsuarioController/usuario/{id} - Elimina (desactiva) un usuario
     */
    @DELETE
    @Path("usuario/{id}")
    public Response eliminarUsuario(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = usuarioService.eliminarUsuario(id);
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                       .entity(respuesta.getMensaje()).build();
            }
            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error eliminando el usuario.").build();
        }
    }
}