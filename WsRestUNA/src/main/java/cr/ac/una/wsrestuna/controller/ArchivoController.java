package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.ArchivoDto;
import cr.ac.una.wsrestuna.service.ArchivoService;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador REST para gestión de archivos (imágenes)
 */
@Path("/ArchivoController")
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
    public Response getArchivo(@PathParam("id") Long id) {
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
    public Response guardarArchivo(ArchivoDto archivo) {
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
    public Response eliminarArchivo(@PathParam("id") Long id) {
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
