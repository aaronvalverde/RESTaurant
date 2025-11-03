package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.ParametroDto;
import cr.ac.una.wsrestuna.service.ParametroService;
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


@Path("ParametroController")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Parámetros", description = "Operaciones sobre parámetros de configuración")
public class ParametroController {

    private static final Logger LOGGER = Logger.getLogger(ParametroController.class.getName());

    @EJB
    private ParametroService parametroService;

    
    @GET
    @Path("parametros/usuario/{idUsuario}")
    @Operation(summary = "Lista parámetros de un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de parámetros",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ParametroDto.class))),
        @ApiResponse(responseCode = "404", description = "Usuario sin parámetros",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getParametrosPorUsuario(
            @Parameter(description = "ID del usuario propietario", example = "1")
            @PathParam("idUsuario") Long idUsuario) {
        try {
            Respuesta respuesta = parametroService.getParametrosPorUsuario(idUsuario);
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(new GenericEntity<List<ParametroDto>>
                    ((List<ParametroDto>) respuesta.getResultado("Parametros")) {}).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener parámetros del usuario", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo los parámetros del usuario.").build();
        }
    }

    
    @GET
    @Path("parametro/usuario/{idUsuario}/clave/{clave}")
    @Operation(summary = "Obtiene un parámetro específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Parámetro encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ParametroDto.class))),
        @ApiResponse(responseCode = "404", description = "Parámetro no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getParametroPorUsuarioYClave(
            @Parameter(description = "ID del usuario", example = "1")
            @PathParam("idUsuario") Long idUsuario,
            @Parameter(description = "Clave del parámetro", example = "IMPUESTO_VENTA")
            @PathParam("clave") String clave) {
        try {
            Respuesta respuesta = parametroService.getParametroPorUsuarioYClave(idUsuario, clave);
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            ParametroDto parametroDto = (ParametroDto) respuesta.getResultado("Parametro");
            return Response.ok(parametroDto).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener parámetro", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo el parámetro.").build();
        }
    }

    
    @POST
    @Path("parametro")
    @Operation(summary = "Guarda un parámetro")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Parámetro guardado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ParametroDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response guardarParametro(
            @Parameter(description = "Información del parámetro") ParametroDto parametroDto) {
        try {
            Respuesta respuesta = parametroService.guardarParametro(parametroDto);
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            ParametroDto resultado = (ParametroDto) respuesta.getResultado("Parametro");
            return Response.ok(resultado).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar parámetro", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error guardando el parámetro.").build();
        }
    }

    
    @POST
    @Path("parametros")
    @Operation(summary = "Guarda múltiples parámetros")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Parámetros guardados",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ParametroDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response guardarParametros(
            @Parameter(description = "Listado de parámetros a guardar")
            List<ParametroDto> parametrosDto) {
        try {
            Respuesta respuesta = parametroService.guardarParametros(parametrosDto);
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(new GenericEntity<List<ParametroDto>>
                    ((List<ParametroDto>) respuesta.getResultado("Parametros")) {}).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar parámetros", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error guardando los parámetros.").build();
        }
    }

    
    @DELETE
    @Path("parametro/{id}")
    @Operation(summary = "Elimina un parámetro")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Parámetro eliminado"),
        @ApiResponse(responseCode = "404", description = "Parámetro no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response eliminarParametro(
            @Parameter(description = "ID del parámetro", example = "12")
            @PathParam("id") Long id) {
        try {
            Respuesta respuesta = parametroService.eliminarParametro(id);
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok().build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar parámetro", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error eliminando el parámetro.").build();
        }
    }
}
