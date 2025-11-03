/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.service.ReporteService;
import cr.ac.una.wsrestuna.util.Respuesta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.ParseException;

/**
 *
 * @author fonse
 */
@Path("/ReporteController")
@Tag(name = "Reporte", description = "Operaciones sobre reportes del restaurante")
public class ReporteController {

    private static final Logger LOG = Logger.getLogger(MesaController.class.getName());

    @EJB
    ReporteService reporteService;

    @GET
    @Path("/reporte/productos-vendidos")
    @Produces("application/pdf")
    @Operation(summary = "Genera reporte de productos vendidos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente",
                content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response reporteProductosVendidos(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)", example = "2025-11-01")
            @QueryParam("fechaInicio") String fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)", example = "2025-11-02")
            @QueryParam("fechaFin") String fechaFin) {
        try {

            if (fechaInicio == null || fechaFin == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Las fechas de inicio y fin son obligatorias")
                        .build();
            }

            SimpleDateFormat formatoDate = new SimpleDateFormat("yyyy-MM-dd");
            Date FechaInicio = new Date(formatoDate.parse(fechaInicio).getTime());
            Date FechaFin = new Date(formatoDate.parse(fechaFin).getTime());

            Respuesta respuesta = reporteService.reporteProductosVendidos(FechaInicio, FechaFin);
            if (!respuesta.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(respuesta.getMensaje())
                        .build();
            }

            byte[] pdf = (byte[]) respuesta.getResultado("Reporte");
            String nombreReporte = "reporte_productos_" + System.currentTimeMillis() + ".pdf";

            return Response.ok(pdf)
                    .header("Content-Disposition", "attachment; filename=\"" + nombreReporte + "\"")
                    .type("application/pdf")
                    .build();
        } catch (ParseException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error en el formato de fecha. Use yyyy-MM-dd")
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error generando reporte de productos vendidos.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generando reporte: " + ex.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/reporte/cierre-caja")
    @Produces("application/pdf")
    @Operation(summary = "Genera reporte de cierre de caja")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente",
                content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response reporteCierreCaja(
            @Parameter(description = "ID del cierre de caja", example = "1")
            @QueryParam("idCierreCaja") Long idCierreCaja) {
        try {
            if (idCierreCaja == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("El ID del cierre de caja es obligatorio")
                        .build();
            }

            Respuesta respuesta = reporteService.reporteCierreCaja(idCierreCaja);
            if (!respuesta.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(respuesta.getMensaje())
                        .build();
            }

            byte[] pdf = (byte[]) respuesta.getResultado("Reporte");
            String nombreReporte = "reporte_cierre_caja_" + System.currentTimeMillis() + ".pdf";

            return Response.ok(pdf)
                    .header("Content-Disposition", "attachment; filename=\"" + nombreReporte + "\"")
                    .type("application/pdf")
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error generando reporte de cierre de caja.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generando reporte: " + ex.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/reporte/facturas")
    @Produces("application/pdf")
    @Operation(summary = "Genera reporte de facturas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte generado correctamente",
                content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response reporteFacturas(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)", example = "2025-11-01")
            @QueryParam("fechaInicio") String fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)", example = "2025-11-02")
            @QueryParam("fechaFin") String fechaFin) {
        try {
            if (fechaInicio == null || fechaFin == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Las fechas de inicio y fin son obligatorias")
                        .build();
            }

             SimpleDateFormat formatoDate = new SimpleDateFormat("yyyy-MM-dd");
            Date FechaInicio = new Date(formatoDate.parse(fechaInicio).getTime());
            Date FechaFin = new Date(formatoDate.parse(fechaFin).getTime());
            
            Respuesta respuesta = reporteService.reporteFacturas(FechaInicio, FechaFin);
            if (!respuesta.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(respuesta.getMensaje())
                    .build();
            }
            
           
            byte[] pdf = (byte[]) respuesta.getResultado("Reporte");
            String nombreReporte = "reporte_facturas_" + System.currentTimeMillis() + ".pdf";
            
            return Response.ok(pdf)
                .header("Content-Disposition", "attachment; filename=\"" + nombreReporte + "\"")
                .type("application/pdf")
                .build();
        } catch (ParseException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error en el formato de fecha. Use yyyy-MM-dd")
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error generando reporte de facturas.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generando reporte: " + ex.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/factura/{id}/pdf")
    @Produces("application/pdf")
    @Operation(summary = "Genera PDF de factura individual")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF de factura generado",
                content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response pdfFactura(
            @Parameter(description = "ID de la factura", example = "1")
            @PathParam("id") Long idFactura) {
        try {
            if (idFactura == null || idFactura <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ID de factura inválido")
                        .build();
            }

            Respuesta respuesta = reporteService.pdfFactura(idFactura);
            if (!respuesta.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(respuesta).build();
            }

            byte[] pdf = (byte[]) respuesta.getResultado("PDF");
            String nombreReporte = "factura_" + idFactura + ".pdf";

            return Response.ok(pdf)
                    .header("Content-Disposition", "attachment; filename=\"" + nombreReporte + "\"")
                    .build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error generando PDF de factura individual.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generando PDF de factura: " + ex.getMessage())
                    .build();
        }
    }

}
