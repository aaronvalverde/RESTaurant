package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.GrupoProductoDto;
import cr.ac.una.wsrestuna.service.GrupoProductoService;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador REST para gestión de grupos/categorías de productos
 */
@Path("/GrupoProductoController")
public class GrupoProductoController {
    
    private static final Logger LOG = Logger.getLogger(GrupoProductoController.class.getName());
    
    @EJB
    GrupoProductoService grupoProductoService;
    
    /**
     * Obtiene un grupo de productos por ID
     * GET /GrupoProductoController/grupo/{id}
     */
    @GET
    @Path("/grupo/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGrupoProducto(@PathParam("id") Long id) {
        try {
            Respuesta res = grupoProductoService.getGrupoProducto(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.NOT_FOUND).entity(res).build();
            }
            return Response.ok((GrupoProductoDto) res.getResultado("GrupoProducto")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupo de productos.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo grupo: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Obtiene todos los grupos de productos ordenados por orden de visualización
     * GET /GrupoProductoController/grupos
     */
    @GET
    @Path("/grupos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGrupoProductos() {
        try {
            Respuesta res = grupoProductoService.getGrupoProductos();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            return Response.ok((List<GrupoProductoDto>) res.getResultado("GrupoProductos")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos de productos.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo grupos: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Obtiene solo los grupos activos ordenados por orden de visualización
     * GET /GrupoProductoController/grupos/activos
     */
    @GET
    @Path("/grupos/activos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGrupoProductosActivos() {
        try {
            Respuesta res = grupoProductoService.getGrupoProductosActivos();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            return Response.ok((List<GrupoProductoDto>) res.getResultado("GrupoProductos")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos activos.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo grupos activos: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Obtiene los grupos marcados para acceso rápido (para menú POS)
     * GET /GrupoProductoController/grupos/accesorapido
     */
    @GET
    @Path("/grupos/accesorapido")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGrupoProductosAccesoRapido() {
        try {
            Respuesta res = grupoProductoService.getGrupoProductosAccesoRapido();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            return Response.ok((List<GrupoProductoDto>) res.getResultado("GrupoProductos")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos de acceso rápido.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo grupos de acceso rápido: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Obtiene los grupos más vendidos
     * GET /GrupoProductoController/grupos/masvendidos
     */
    @GET
    @Path("/grupos/masvendidos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGrupoProductosMasVendidos() {
        try {
            Respuesta res = grupoProductoService.getGrupoProductosMasVendidos();
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            return Response.ok((List<GrupoProductoDto>) res.getResultado("GrupoProductos")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos más vendidos.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo grupos más vendidos: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Guarda un nuevo grupo o actualiza uno existente
     * POST /GrupoProductoController/grupo
     */
    @POST
    @Path("/grupo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response guardarGrupoProducto(GrupoProductoDto dto) {
        try {
            Respuesta res = grupoProductoService.guardarGrupoProducto(dto);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            return Response.ok((GrupoProductoDto) res.getResultado("GrupoProducto")).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando grupo de productos.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error guardando grupo: " + ex.getMessage())
                .build();
        }
    }
    
    /**
     * Elimina un grupo de productos por ID
     * DELETE /GrupoProductoController/grupo/{id}
     */
    @DELETE
    @Path("/grupo/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response eliminarGrupoProducto(@PathParam("id") Long id) {
        try {
            Respuesta res = grupoProductoService.eliminarGrupoProducto(id);
            if (!res.getEstado()) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
            }
            return Response.ok(res).build();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando grupo de productos.", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error eliminando grupo: " + ex.getMessage())
                .build();
        }
    }
}
