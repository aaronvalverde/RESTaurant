package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.OrdenDto;
import cr.ac.una.wsrestuna.service.OrdenService;
import cr.ac.una.wsrestuna.util.Respuesta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("/OrdenController")
@Tag(name = "Órdenes", description = "Operaciones sobre órdenes del restaurante")
public class OrdenController {
    
    private static final Logger LOG = Logger.getLogger(OrdenController.class.getName());
    
    @EJB
    OrdenService ordenService;
    
    @GET
    @Path("/ordenes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todas las órdenes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de órdenes",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = OrdenDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getOrdenes() {
        try {
            Respuesta res = ordenService.obtenerTodas();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<OrdenDto> ordenes = (List<OrdenDto>) res.getResultado("Ordenes");
            return Response.ok(ordenes).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo órdenes.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo órdenes: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/orden/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene una orden por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = OrdenDto.class))),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getOrden(@Parameter(description = "ID de la orden", example = "1")
                             @PathParam("id") Long id) {
        try {
            Respuesta res = ordenService.obtenerPorId(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((OrdenDto) res.getResultado("Orden")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo orden.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo orden: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/ordenes/estado/{estado}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista órdenes por estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de órdenes",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = OrdenDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getOrdenesPorEstado(@Parameter(description = "Estado de la orden", example = "PENDIENTE")
                                        @PathParam("estado") String estado) {
        try {
            Respuesta res = ordenService.obtenerPorEstado(estado);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<OrdenDto> ordenes = (List<OrdenDto>) res.getResultado("Ordenes");
            return Response.ok(ordenes).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo órdenes por estado.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo órdenes: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/ordenes/mesa/{idMesa}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todas las órdenes de una mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de órdenes",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = OrdenDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getAllOrdenesPorMesa(@Parameter(description = "ID de la mesa", example = "5")
                                      @PathParam("idMesa") Long idMesa) {
        try {
            
            Respuesta res = ordenService.obtenerPorMesa(idMesa, null);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<OrdenDto> ordenes = (List<OrdenDto>) res.getResultado("Ordenes");
            return Response.ok(ordenes).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo órdenes por mesa.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo órdenes: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/ordenes/mesa/{idMesa}/{estado}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista órdenes por mesa y estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de órdenes",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = OrdenDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getOrdenesPorMesaYEstado(@Parameter(description = "ID de la mesa", example = "5")
                                      @PathParam("idMesa") Long idMesa,
                                      @Parameter(description = "Estado de la orden", example = "ABIERTA")
                                      @PathParam("estado") String estado) {
        try {
            Respuesta res = ordenService.obtenerPorMesa(idMesa, estado);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<OrdenDto> ordenes = (List<OrdenDto>) res.getResultado("Ordenes");
            return Response.ok(ordenes).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo órdenes por mesa.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo órdenes: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/ordenes/seccion/{idSeccion}/{estado}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista órdenes por sección y estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de órdenes",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = OrdenDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getOrdenesPorSeccion(@Parameter(description = "ID de la sección", example = "2")
                                         @PathParam("idSeccion") Long idSeccion,
                                         @Parameter(description = "Estado de la orden", example = "PENDIENTE")
                                         @PathParam("estado") String estado) {
        try {
            Respuesta res = ordenService.obtenerPorSeccion(idSeccion, estado);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<OrdenDto> ordenes = (List<OrdenDto>) res.getResultado("Ordenes");
            return Response.ok(ordenes).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo órdenes por sección.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo órdenes: " + ex.getMessage())
                .build();
        }
    }
    
    @POST
    @Path("/orden")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Crea una nueva orden")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden creada exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = OrdenDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response crearOrden(@Parameter(description = "Datos de la orden a crear", required = true)
                               OrdenDto ordenDto) {
        try {
            Respuesta res = ordenService.crear(ordenDto);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(res.getMensaje())
                    .build();
            }
            return Response.ok((OrdenDto) res.getResultado("Orden")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creando orden.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error creando orden: " + ex.getMessage())
                .build();
        }
    }
    
    @PUT
    @Path("/orden")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Actualiza una orden existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden actualizada exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = OrdenDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response actualizarOrden(@Parameter(description = "Datos de la orden a actualizar", required = true)
                                    OrdenDto ordenDto) {
        try {
            Respuesta res = ordenService.actualizar(ordenDto);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(res.getMensaje())
                    .build();
            }
            return Response.ok((OrdenDto) res.getResultado("Orden")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error actualizando orden.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error actualizando orden: " + ex.getMessage())
                .build();
        }
    }
    
    @PUT
    @Path("/orden/{id}/estado/{estado}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cambia el estado de una orden")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = OrdenDto.class))),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response cambiarEstado(@Parameter(description = "ID de la orden", example = "1")
                                  @PathParam("id") Long id,
                                  @Parameter(description = "Nuevo estado", example = "EN_PREPARACION")
                                  @PathParam("estado") String estado) {
        try {
            Respuesta res = ordenService.cambiarEstado(id, estado);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((OrdenDto) res.getResultado("Orden")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error cambiando estado de orden.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error cambiando estado: " + ex.getMessage())
                .build();
        }
    }
    
    @DELETE
    @Path("/orden/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Elimina una orden por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden eliminada exitosamente",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response eliminarOrden(@Parameter(description = "ID de la orden", example = "1")
                                  @PathParam("id") Long id) {
        try {
            Respuesta res = ordenService.eliminar(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok(res).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando orden.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error eliminando orden: " + ex.getMessage())
                .build();
        }
    }
}
