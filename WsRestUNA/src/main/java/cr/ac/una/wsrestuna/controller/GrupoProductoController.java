package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.GrupoProductoDto;
import cr.ac.una.wsrestuna.service.GrupoProductoService;
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
 * Controlador REST para gestión de grupos/categorías de productos
 */
@Path("/GrupoProductoController")
@Tag(name = "Grupos de Producto", description = "Operaciones sobre los grupos/categorías del menú")
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
    @Operation(summary = "Obtiene un grupo por ID", description = "Retorna un grupo de productos específico sin contenido de imagen.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Grupo encontrado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = GrupoProductoDto.class))),
        @ApiResponse(responseCode = "404", description = "Grupo no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response getGrupoProducto(@Parameter(description = "ID del grupo a consultar", example = "1")
                                     @PathParam("id") Long id) {
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
    @Operation(summary = "Obtiene todos los grupos", description = "Retorna la lista completa de grupos ordenados por orden de visualización.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = GrupoProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
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
    @Operation(summary = "Obtiene grupos activos", description = "Retorna solo los grupos con estado activo ordenados por visualización.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = GrupoProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
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
    @Operation(summary = "Obtiene grupos de acceso rápido", description = "Retorna los grupos marcados para acceso rápido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = GrupoProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
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
    @Operation(summary = "Obtiene grupos más vendidos", description = "Retorna los grupos más vendidos, máximo 10 registros.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = GrupoProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
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
    @Operation(summary = "Crea o actualiza un grupo", description = "Persiste un nuevo grupo o actualiza uno existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Grupo guardado",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = GrupoProductoDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response guardarGrupoProducto(@Parameter(description = "Información del grupo") GrupoProductoDto dto) {
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
    @Operation(summary = "Elimina un grupo", description = "Elimina un grupo de productos por ID, siempre que no tenga productos asociados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Grupo eliminado"),
        @ApiResponse(responseCode = "403", description = "El grupo tiene productos asociados",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "404", description = "Grupo no encontrado",
                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(mediaType = MediaType.TEXT_PLAIN))
    })
    public Response eliminarGrupoProducto(@Parameter(description = "ID del grupo a eliminar", example = "5")
                                          @PathParam("id") Long id) {
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
