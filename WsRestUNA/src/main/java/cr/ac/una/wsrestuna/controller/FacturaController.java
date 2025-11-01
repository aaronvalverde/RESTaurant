package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.FacturaDto;
import cr.ac.una.wsrestuna.service.FacturaService;
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

/**
 * Controlador REST para gestión de facturas
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Path("/FacturaController")
@Tag(name = "Facturas", description = "Operaciones sobre facturas del restaurante")
public class FacturaController {
    
    private static final Logger LOG = Logger.getLogger(FacturaController.class.getName());
    
    @EJB
    FacturaService facturaService;
    
    @GET
    @Path("/facturas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todas las facturas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de facturas",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = FacturaDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getFacturas() {
        try {
            Respuesta res = facturaService.obtenerTodas();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<FacturaDto> facturas = (List<FacturaDto>) res.getResultado("Facturas");
            return Response.ok(facturas).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo facturas.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo facturas: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/factura/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene una factura por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factura encontrada",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = FacturaDto.class))),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getFactura(@Parameter(description = "ID de la factura", example = "1")
                               @PathParam("id") Long id) {
        try {
            Respuesta res = facturaService.obtenerPorId(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((FacturaDto) res.getResultado("Factura")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo factura.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo factura: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/facturas/cajero/{idCajero}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista facturas por cajero")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de facturas",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = FacturaDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getFacturasPorCajero(@Parameter(description = "ID del cajero", example = "2")
                                         @PathParam("idCajero") Long idCajero) {
        try {
            Respuesta res = facturaService.obtenerPorCajero(idCajero);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<FacturaDto> facturas = (List<FacturaDto>) res.getResultado("Facturas");
            return Response.ok(facturas).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo facturas por cajero.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo facturas: " + ex.getMessage())
                .build();
        }
    }
    
    @GET
    @Path("/facturas/fecha/{fechaInicio}/{fechaFin}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista facturas por rango de fechas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de facturas",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = FacturaDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getFacturasPorFecha(@Parameter(description = "Fecha inicio (ISO 8601)", example = "2024-05-01T00:00:00")
                                        @PathParam("fechaInicio") String fechaInicio,
                                        @Parameter(description = "Fecha fin (ISO 8601)", example = "2024-05-31T23:59:59")
                                        @PathParam("fechaFin") String fechaFin) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            LocalDateTime fin = LocalDateTime.parse(fechaFin);
            
            Respuesta res = facturaService.obtenerPorFecha(inicio, fin);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<FacturaDto> facturas = (List<FacturaDto>) res.getResultado("Facturas");
            return Response.ok(facturas).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo facturas por fecha.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo facturas: " + ex.getMessage())
                .build();
        }
    }
    
    @POST
    @Path("/factura")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Crea una nueva factura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factura creada exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = FacturaDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response crearFactura(@Parameter(description = "Datos de la factura a crear", required = true)
                                 FacturaDto facturaDto) {
        try {
            Respuesta res = facturaService.crear(facturaDto);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok((FacturaDto) res.getResultado("Factura")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creando factura.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error creando factura: " + ex.getMessage())
                .build();
        }
    }
    
    @DELETE
    @Path("/factura/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Elimina una factura por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factura eliminada exitosamente",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response eliminarFactura(@Parameter(description = "ID de la factura", example = "1")
                                    @PathParam("id") Long id) {
        try {
            Respuesta res = facturaService.eliminar(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok(res).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando factura.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error eliminando factura: " + ex.getMessage())
                .build();
        }
    }
}
