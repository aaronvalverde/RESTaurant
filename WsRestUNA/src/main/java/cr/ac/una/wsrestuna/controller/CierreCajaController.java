package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.CierreCajaDto;
import cr.ac.una.wsrestuna.service.CierreCajaService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Path("/CierreCajaController")
@Tag(name = "Cierres de Caja", description = "Operaciones sobre cierres de caja del restaurante")
public class CierreCajaController {
    
    private static final Logger LOG = Logger.getLogger(CierreCajaController.class.getName());
    
    @EJB
    CierreCajaService cierreCajaService;
    
    @GET
    @Path("/cierres")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todos los cierres de caja")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de cierres",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = CierreCajaDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getCierresCaja() {
        try {
            Respuesta res = cierreCajaService.obtenerTodos();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<CierreCajaDto> cierres = (List<CierreCajaDto>) res.getResultado("CierresCaja");
            return Response.ok(cierres).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo cierres de caja.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo cierres de caja: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/cierre/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene un cierre de caja por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cierre encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = CierreCajaDto.class))),
        @ApiResponse(responseCode = "404", description = "Cierre no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getCierreCaja(@Parameter(description = "ID del cierre", example = "1")
                                  @PathParam("id") Long id) {
        try {
            Respuesta res = cierreCajaService.obtenerPorId(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((CierreCajaDto) res.getResultado("CierreCaja")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo cierre de caja.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo cierre de caja: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/cierres/cajero/{idCajero}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista cierres por cajero")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de cierres",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = CierreCajaDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getCierresPorCajero(@Parameter(description = "ID del cajero", example = "2")
                                        @PathParam("idCajero") Long idCajero) {
        try {
            Respuesta res = cierreCajaService.obtenerPorCajero(idCajero);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<CierreCajaDto> cierres = (List<CierreCajaDto>) res.getResultado("CierresCaja");
            return Response.ok(cierres).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo cierres por cajero.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo cierres de caja: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/cierres/fecha/{fechaInicio}/{fechaFin}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista cierres por rango de fechas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de cierres",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = CierreCajaDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getCierresPorFecha(@Parameter(description = "Fecha inicio (ISO 8601)", example = "2024-05-01T00:00:00")
                                       @PathParam("fechaInicio") String fechaInicio,
                                       @Parameter(description = "Fecha fin (ISO 8601)", example = "2024-05-31T23:59:59")
                                       @PathParam("fechaFin") String fechaFin) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            LocalDateTime fin = LocalDateTime.parse(fechaFin);
            
            Respuesta res = cierreCajaService.obtenerPorFecha(inicio, fin);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<CierreCajaDto> cierres = (List<CierreCajaDto>) res.getResultado("CierresCaja");
            return Response.ok(cierres).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo cierres por fecha.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo cierres de caja: " + ex.getMessage())
                .build();
        }
    }
    
    @POST
    @Path("/cierre")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Crea un nuevo cierre de caja")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cierre creado exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = CierreCajaDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response crearCierre(@Parameter(description = "Datos del cierre a crear", required = true)
                                CierreCajaDto cierreDto) {
        try {
            Respuesta res = cierreCajaService.crear(cierreDto);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok((CierreCajaDto) res.getResultado("CierreCaja")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creando cierre de caja.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error creando cierre de caja: " + ex.getMessage())
                .build();
        }
    }
    
    @PUT
    @Path("/cierre")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Actualiza un cierre de caja existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cierre actualizado exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = CierreCajaDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "404", description = "Cierre no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response actualizarCierre(@Parameter(description = "Datos del cierre a actualizar", required = true)
                                     CierreCajaDto cierreDto) {
        try {
            Respuesta res = cierreCajaService.actualizar(cierreDto);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok((CierreCajaDto) res.getResultado("CierreCaja")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error actualizando cierre de caja.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error actualizando cierre de caja: " + ex.getMessage())
                .build();
        }
    }
    
    @DELETE
    @Path("/cierre/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Elimina un cierre de caja por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cierre eliminado exitosamente",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "404", description = "Cierre no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response eliminarCierre(@Parameter(description = "ID del cierre", example = "1")
                                   @PathParam("id") Long id) {
        try {
            Respuesta res = cierreCajaService.eliminar(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok(res).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando cierre de caja.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error eliminando cierre de caja: " + ex.getMessage())
                .build();
        }
    }
}
