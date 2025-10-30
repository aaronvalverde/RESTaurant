package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.MesaDto;
import cr.ac.una.wsrestuna.service.MesaService;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador REST para gestión de mesas del restaurante
 */
@Path("/MesaController")
public class MesaController {
    
    private static final Logger LOG = Logger.getLogger(MesaController.class.getName());
    
    @EJB
    MesaService mesaService;
    
    /**
     * Obtiene una mesa por ID
     * GET /MesaController/mesa/{id}
     */
    @GET
    @Path("/mesa/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMesa(@PathParam("id") Long id) {
        try {
            Respuesta res = mesaService.getMesa(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((MesaDto) res.getResultado("Mesa")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesa.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo mesa: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Obtiene todas las mesas
     * GET /MesaController/mesas
     */
    @GET
    @Path("/mesas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMesas() {
        try {
            Respuesta res = mesaService.getMesas();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<MesaDto> mesas = (List<MesaDto>) res.getResultado("Mesas");
            return Response.ok(mesas).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo mesas: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Obtiene mesas de una sección específica
     * GET /MesaController/mesas/seccion/{idSeccion}
     */
    @GET
    @Path("/mesas/seccion/{idSeccion}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMesasPorSeccion(@PathParam("idSeccion") Long idSeccion) {
        try {
            Respuesta res = mesaService.getMesasPorSeccion(idSeccion);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<MesaDto> mesas = (List<MesaDto>) res.getResultado("Mesas");
            return Response.ok(mesas).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas de sección.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo mesas: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Obtiene mesas por estado
     * GET /MesaController/mesas/estado/{estado}
     */
    @GET
    @Path("/mesas/estado/{estado}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMesasPorEstado(@PathParam("estado") String estado) {
        try {
            Respuesta res = mesaService.getMesasPorEstado(estado);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<MesaDto> mesas = (List<MesaDto>) res.getResultado("Mesas");
            return Response.ok(mesas).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas por estado.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo mesas: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Obtiene mesas libres
     * GET /MesaController/mesas/libres
     */
    @GET
    @Path("/mesas/libres")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMesasLibres() {
        try {
            Respuesta res = mesaService.getMesasLibres();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<MesaDto> mesas = (List<MesaDto>) res.getResultado("Mesas");
            return Response.ok(mesas).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas libres.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo mesas: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Guarda una mesa (crear o actualizar)
     * POST /MesaController/mesa
     */
    @POST
    @Path("/mesa")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response guardarMesa(MesaDto mesa) {
        try {
            Respuesta res = mesaService.guardarMesa(mesa);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            return Response.ok((MesaDto) res.getResultado("Mesa")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando mesa.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error guardando mesa: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Guarda múltiples mesas (batch)
     * POST /MesaController/mesas
     */
    @POST
    @Path("/mesas")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response guardarMesas(List<MesaDto> mesas) {
        try {
            Respuesta res = mesaService.guardarMesas(mesas);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            
            @SuppressWarnings("unchecked")
            List<MesaDto> resultados = (List<MesaDto>) res.getResultado("Mesas");
            return Response.ok(resultados).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando mesas batch.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error guardando mesas: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Actualiza el estado de una mesa
     * PUT /MesaController/mesa/{id}/estado/{estado}
     */
    @PUT
    @Path("/mesa/{id}/estado/{estado}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarEstadoMesa(@PathParam("id") Long id, @PathParam("estado") String estado) {
        try {
            Respuesta res = mesaService.actualizarEstadoMesa(id, estado);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok((MesaDto) res.getResultado("Mesa")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error actualizando estado de mesa.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error actualizando estado: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Elimina una mesa por ID
     * DELETE /MesaController/mesa/{id}
     */
    @DELETE
    @Path("/mesa/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eliminarMesa(@PathParam("id") Long id) {
        try {
            Respuesta res = mesaService.eliminarMesa(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            }
            return Response.ok((Long) res.getResultado("Id")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando mesa.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error eliminando mesa: " + ex.getMessage())
                .build();
        }
    }
}
