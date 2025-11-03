package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.UsuarioDto;
import cr.ac.una.wsrestuna.service.UsuarioService;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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
@Tag(name = "Usuarios", description = "Operaciones sobre usuarios del sistema")
public class UsuarioController {

    private static final Logger LOGGER = Logger.getLogger(UsuarioController.class.getName());

    @EJB
    private UsuarioService usuarioService;

    /**
     * GET /UsuarioController/usuario/{usuario}/{contrasena} - Autenticación siguiendo patrón UNA Planilla
     */
    @GET
    @Path("usuario/{usuario}/{contrasena}")
    @Operation(summary = "Autentica un usuario", description = "Valida las credenciales proporcionadas y retorna el usuario.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario autenticado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response validarUsuario(
            @Parameter(description = "Código de usuario", example = "admin")
            @PathParam("usuario") String usuario,
            @Parameter(description = "Contraseña del usuario", example = "1234")
            @PathParam("contrasena") String contrasena) {
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
    @Operation(summary = "Obtiene un usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getUsuario(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathParam("id") Long id) {
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
    @Operation(summary = "Busca usuarios con filtros", description = "Permite filtrar por nombre, usuario, rol y estado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de usuarios",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getUsuarios(
            @Parameter(description = "Filtro de nombre (use '-' para ignorar)", example = "-")
            @PathParam("nombre") String nombre,
            @Parameter(description = "Filtro de usuario (use '-' para ignorar)", example = "-")
            @PathParam("usuario") String usuario,
            @Parameter(description = "Filtro de rol (ADMINISTRADOR/CAJERO/SALONERO o '-')", example = "-")
            @PathParam("rol") String rol,
            @Parameter(description = "Filtro de estado (A/I o '-')", example = "-")
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
    @Operation(summary = "Crea o actualiza un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario guardado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response guardarUsuario(
            @Parameter(description = "Información del usuario") UsuarioDto usuarioDto) {
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
    @Operation(summary = "Obtiene todos los usuarios", description = "Retorna la lista completa de usuarios sin filtros.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de usuarios",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
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
    @Operation(summary = "Elimina un usuario (inactiva)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response eliminarUsuario(
            @Parameter(description = "Identificador del usuario", example = "4")
            @PathParam("id") Long id) {
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
