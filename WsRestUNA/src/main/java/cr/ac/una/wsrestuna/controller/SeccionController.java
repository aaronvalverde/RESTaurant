package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.SeccionDto;
import cr.ac.una.wsrestuna.service.SeccionService;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador REST para gestión de secciones/salones del restaurante
 */
@Path("/SeccionController")
public class SeccionController {
    
    private static final Logger LOG = Logger.getLogger(SeccionController.class.getName());
    
    @EJB
    SeccionService seccionService;
    
    /**
     * Obtiene una sección por ID (sin contenido de imagen)
     * GET /SeccionController/seccion/{id}
     */
    @GET
    @Path("/seccion/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSeccion(@PathParam("id") Long id) {
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
    
    /**
     * Obtiene una sección por ID con el contenido completo de su imagen
     * GET /SeccionController/seccion/{id}/conimagen
     */
    @GET
    @Path("/seccion/{id}/conimagen")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSeccionConImagen(@PathParam("id") Long id) {
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
    
    /**
     * Obtiene todas las secciones
     * GET /SeccionController/secciones
     */
    @GET
    @Path("/secciones")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
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
    
    /**
     * Obtiene todas las secciones activas
     * GET /SeccionController/secciones/activas
     */
    @GET
    @Path("/secciones/activas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
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
    
    /**
     * Guarda una sección (crear o actualizar)
     * POST /SeccionController/seccion
     */
    @POST
    @Path("/seccion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response guardarSeccion(SeccionDto seccion) {
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
    
    /**
     * Elimina una sección por ID
     * DELETE /SeccionController/seccion/{id}
     */
    @DELETE
    @Path("/seccion/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eliminarSeccion(@PathParam("id") Long id) {
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
