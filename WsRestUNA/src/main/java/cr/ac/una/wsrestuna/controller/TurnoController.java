package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.Turno;
import cr.ac.una.wsrestuna.model.TurnoDto;
import cr.ac.una.wsrestuna.model.UsuarioDto;
import cr.ac.una.wsrestuna.service.TurnoService;
import cr.ac.una.wsrestuna.util.Respuesta;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ejb.EJB;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("TurnoController")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Turnos", description = "Operaciones sobre turnos del sistema")
public class TurnoController {

    private static final Logger LOGGER = Logger.getLogger(TurnoController.class.getName());

    @EJB
    private TurnoService turnoService;

    @GET
    @Path("turno")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de usuarios",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = UsuarioDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getAllTurnos() {
        try {
            Respuesta respuesta = turnoService.getAllTurnos();
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(new GenericEntity<List<TurnoDto>>((List<TurnoDto>) respuesta.getResultado()) {
            }).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error obteniendo turnos").build();
        }
    }

    @GET
    @Path("turno/{id}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turno encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = TurnoDto.class))),
        @ApiResponse(responseCode = "404", description = "Turno no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getTurnoById(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = turnoService.getTurno(id);

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            TurnoDto turnoDto = (TurnoDto) respuesta.getResultado();

            return Response.ok(turnoDto).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error: " + ex.getMessage()).build();
        }
    }

    @GET
    @Path("turno/usuario/{usuarioId}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de turnos de un usuario",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = TurnoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getTurnosByUser(@PathParam("usuarioId") Long id) {
        try {
            Respuesta respuesta = turnoService.getTurnosByUsuario(id);
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(new GenericEntity<List<TurnoDto>>((List<TurnoDto>) respuesta.getResultado()) {
            }).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error obteniendo turnos del usuario").build();
        }
    }

    @GET
    @Path("turno/usuario/{usuarioId}/activo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turno activo por parte de un usuario",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = TurnoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getTurnoActivoByUser(@PathParam("usuarioId") Long id) {
        try {
            Respuesta respuesta = turnoService.getTurnoActivoByUsuario(id);
            TurnoDto turnoDto = (TurnoDto) respuesta.getResultado();
            return Response.ok(turnoDto).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error obteniendo turno activo").build();
        }
    }

    @POST
    @Path("turno")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Turno iniciado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = TurnoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response iniciarTurno(TurnoDto turnoDto) {
        try {
            Respuesta respuesta = turnoService.iniciarTurno(turnoDto);
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok((TurnoDto) respuesta.getResultado()).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error iniciando turno").build();
        }
    }

    @PUT
    @Path("turno/{id}")
    public Response finalizarTurno(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = turnoService.finalizarTurno(id);
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue()).entity(respuesta.getMensaje()).build();
            }
            Object raw = respuesta.getResultado();
            TurnoDto turnoDto = null;
            if (raw instanceof TurnoDto) {
                turnoDto = (TurnoDto) raw;
            } else if (raw instanceof Map) {
                Jsonb jsonb = JsonbBuilder.create();
                String json = jsonb.toJson(raw);
                turnoDto = jsonb.fromJson(json, TurnoDto.class);
            } else if (raw instanceof String) {
                try {
                    Jsonb jsonb = JsonbBuilder.create();
                    turnoDto = jsonb.fromJson((String) raw, TurnoDto.class);
                } catch (Exception ignored) {
                }
            }
            if (turnoDto == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Respuesta inválida del servicio").build();
            }
            return Response.ok(turnoDto).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error finalizando turno").build();
        }
    }
}
