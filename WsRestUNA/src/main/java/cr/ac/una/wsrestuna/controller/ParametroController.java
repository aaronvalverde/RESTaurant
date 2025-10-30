package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.ParametroDto;
import cr.ac.una.wsrestuna.service.ParametroService;
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
 * Controlador REST para la gestión de parámetros de configuración
 * Endpoints para CRUD de parámetros por usuario
 */
@Path("ParametroController")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParametroController {

    private static final Logger LOGGER = Logger.getLogger(ParametroController.class.getName());

    @EJB
    private ParametroService parametroService;

    /**
     * GET /ParametroController/parametros/usuario/{idUsuario} - Obtiene todos los parámetros de un usuario
     */
    @GET
    @Path("parametros/usuario/{idUsuario}")
    public Response getParametrosPorUsuario(@PathParam("idUsuario") Long idUsuario) {
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

    /**
     * GET /ParametroController/parametro/usuario/{idUsuario}/clave/{clave} - Obtiene un parámetro específico
     */
    @GET
    @Path("parametro/usuario/{idUsuario}/clave/{clave}")
    public Response getParametroPorUsuarioYClave(@PathParam("idUsuario") Long idUsuario, 
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

    /**
     * POST /ParametroController/parametro - Guarda un parámetro (crear o actualizar)
     */
    @POST
    @Path("parametro")
    public Response guardarParametro(ParametroDto parametroDto) {
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

    /**
     * POST /ParametroController/parametros - Guarda múltiples parámetros (batch)
     */
    @POST
    @Path("parametros")
    public Response guardarParametros(List<ParametroDto> parametrosDto) {
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

    /**
     * DELETE /ParametroController/parametro/{id} - Elimina un parámetro
     */
    @DELETE
    @Path("parametro/{id}")
    public Response eliminarParametro(@PathParam("id") Long id) {
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
