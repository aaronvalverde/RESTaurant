package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.ArchivoDto;
import cr.ac.una.wsrestuna.service.ArchivoService;
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

/**
 * Controlador REST para gestión de archivos (imágenes)
 */
@Path("/ArchivoController")
@Tag(name = "Archivos", description = "Operaciones sobre archivos e imágenes")
public class ArchivoController {
    
    private static final Logger LOG = Logger.getLogger(ArchivoController.class.getName());
    
    @EJB
    ArchivoService archivoService;
    
    /**
     * Obtiene un archivo por ID con su contenido
     * GET /ArchivoController/archivo/{id}
     */
    @GET
    @Path("/archivo/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtiene un archivo", description = "Recupera un archivo incluyendo su contenido Base64.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Archivo encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ArchivoDto.class))),
        @ApiResponse(responseCode = "404", description = "Archivo no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getArchivo(@Parameter(description = "ID del archivo", example = "10")
                               @PathParam("id") Long id) {
        try {
            Respuesta res = archivoService.getArchivo(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((ArchivoDto) res.getResultado("Archivo")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo archivo.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo archivo: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Obtiene todos los archivos (sin contenido, solo metadata)
     * GET /ArchivoController/archivos
     */
    @GET
    @Path("/archivos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista archivos", description = "Retorna solo metadatos de los archivos almacenados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de archivos",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ArchivoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getArchivos() {
        try {
            Respuesta res = archivoService.getArchivos();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<ArchivoDto> archivos = (List<ArchivoDto>) res.getResultado("Archivos");
            return Response.ok(archivos).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo archivos.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo archivos: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Guarda un archivo (crear o actualizar)
     * POST /ArchivoController/archivo
     */
    @POST
    @Path("/archivo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Guarda un archivo", description = "Crea o actualiza un archivo en el repositorio.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Archivo guardado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ArchivoDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response guardarArchivo(
            @Parameter(description = "Información del archivo (incluyendo Base64)") ArchivoDto archivo) {
        try {
            Respuesta res = archivoService.guardarArchivo(archivo);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            return Response.ok((ArchivoDto) res.getResultado("Archivo")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando archivo.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error guardando archivo: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Elimina un archivo por ID
     * DELETE /ArchivoController/archivo/{id}
     */
    @DELETE
    @Path("/archivo/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Elimina un archivo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Archivo eliminado"),
        @ApiResponse(responseCode = "400", description = "No se pudo eliminar",
                content = @Content(mediaType = MediaType.APPLICATION_JSON)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response eliminarArchivo(@Parameter(description = "ID del archivo", example = "10")
                                    @PathParam("id") Long id) {
        try {
            Respuesta res = archivoService.eliminarArchivo(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok((Long) res.getResultado("Id")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando archivo.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error eliminando archivo: " + ex.getMessage())
                .build();
        }
    }
}
