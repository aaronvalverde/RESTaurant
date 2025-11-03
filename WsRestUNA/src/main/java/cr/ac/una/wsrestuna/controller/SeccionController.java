package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.SeccionDto;
import cr.ac.una.wsrestuna.service.SeccionService;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("/SeccionController")
@Tag(name = "Secciones", description = "Operaciones sobre salones y secciones del restaurante")
public class SeccionController {
    
    private static final Logger LOG = Logger.getLogger(SeccionController.class.getName());
    
    @EJB
    SeccionService seccionService;
    
    
    @GET
    @Path("/seccion/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene una sección por ID", description = "Devuelve la sección solicitada sin incluir imagen.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sección encontrada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = SeccionDto.class))),
        @ApiResponse(responseCode = "404", description = "Sección no encontrada",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getSeccion(@Parameter(description = "ID de la sección", example = "1")
                               @PathParam("id") Long id) {
        try {
            Respuesta res = seccionService.getSeccion(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((SeccionDto) res.getResultado("Seccion")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo sección.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo sección: " + ex.getMessage())
                .build();
        }
    }
    
    
    @GET
    @Path("/seccion/{id}/conimagen")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene una sección con imagen", description = "Devuelve la sección con la imagen base64 incluida.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sección encontrada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = SeccionDto.class))),
        @ApiResponse(responseCode = "404", description = "Sección no encontrada",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getSeccionConImagen(@Parameter(description = "ID de la sección", example = "1")
                                        @PathParam("id") Long id) {
        try {
            Respuesta res = seccionService.getSeccionConImagen(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((SeccionDto) res.getResultado("Seccion")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo sección con imagen.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo sección: " + ex.getMessage())
                .build();
        }
    }
    
    
    @GET
    @Path("/secciones")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene todas las secciones", description = "Retorna el listado completo de secciones.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = SeccionDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getSecciones() {
        try {
            Respuesta res = seccionService.getSecciones();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<SeccionDto> secciones = (List<SeccionDto>) res.getResultado("Secciones");
            return Response.ok(secciones).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo secciones.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo secciones: " + ex.getMessage())
                .build();
        }
    }
    
    
    @GET
    @Path("/secciones/activas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene secciones activas", description = "Retorna solo las secciones habilitadas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = SeccionDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getSeccionesActivas() {
        try {
            Respuesta res = seccionService.getSeccionesActivas();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<SeccionDto> secciones = (List<SeccionDto>) res.getResultado("Secciones");
            return Response.ok(secciones).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo secciones activas.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo secciones: " + ex.getMessage())
                .build();
        }
    }
    
    
    @POST
    @Path("/seccion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Guarda una sección", description = "Crea o actualiza una sección.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sección guardada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = SeccionDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response guardarSeccion(@Parameter(description = "Información de la sección") SeccionDto seccion) {
        try {
            Respuesta res = seccionService.guardarSeccion(seccion);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            return Response.ok((SeccionDto) res.getResultado("Seccion")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando sección.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error guardando sección: " + ex.getMessage())
                .build();
        }
    }
    
    
    @DELETE
    @Path("/seccion/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Elimina una sección", description = "Elimina la sección indicada por ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sección eliminada"),
        @ApiResponse(responseCode = "400", description = "No se pudo eliminar",
                content = @Content(mediaType = MediaType.APPLICATION_JSON)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response eliminarSeccion(@Parameter(description = "ID de la sección a eliminar", example = "2")
                                    @PathParam("id") Long id) {
        try {
            Respuesta res = seccionService.eliminarSeccion(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok((Long) res.getResultado("Id")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando sección.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error eliminando sección: " + ex.getMessage())
                .build();
        }
    }
}
