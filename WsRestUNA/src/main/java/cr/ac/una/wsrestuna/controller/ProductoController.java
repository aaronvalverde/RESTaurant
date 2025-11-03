package cr.ac.una.wsrestuna.controller;

import cr.ac.una.wsrestuna.model.ProductoDto;
import cr.ac.una.wsrestuna.service.ProductoService;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
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
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador REST para gestión de productos del menú
 */
@Path("/producto")
@Tag(name = "Productos", description = "Operaciones sobre productos del menú del restaurante")
public class ProductoController {

    private static final Logger LOG = Logger.getLogger(ProductoController.class.getName());

    @EJB
    ProductoService productoService;

    /**
     * Obtiene un producto por ID
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener producto por ID", description = "Retorna los datos de un producto específico según su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ProductoDto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response getProducto(
            @Parameter(description = "ID único del producto", required = true, example = "1")
            @PathParam("id") Long id) {
        try {
            Respuesta respuesta = productoService.getProducto(id);

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok(respuesta.getResultado("Producto")).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo producto.", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo el producto").build();
        }
    }

    /**
     * Obtiene todos los productos
     */
    @GET
    @Path("/productos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener todos los productos", description = "Retorna la lista completa de productos del menú")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response getProductos() {
        try {
            Respuesta respuesta = productoService.getProductos();

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok(new GenericEntity<List<ProductoDto>>((List<ProductoDto>) respuesta.getResultado("Productos")) {
            }).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos.", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo los productos").build();
        }
    }

    /**
     * Obtiene productos activos
     */
    @GET
    @Path("/productos/activos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener productos activos", description = "Retorna la lista de productos con estado activo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos activos obtenida exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response getProductosActivos() {
        try {
            Respuesta respuesta = productoService.getProductosActivos();

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok(new GenericEntity<List<ProductoDto>>((List<ProductoDto>) respuesta.getResultado("Productos")) {
            }).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos activos.", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo los productos activos").build();
        }
    }

    /**
     * Obtiene productos por grupo
     */
    @GET
    @Path("/productos/grupo/{idGrupo}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener productos por grupo", description = "Retorna los productos que pertenecen a un grupo específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos del grupo obtenida exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response getProductosPorGrupo(
            @Parameter(description = "ID del grupo de productos", required = true, example = "1")
            @PathParam("idGrupo") Long idGrupo) {
        try {
            Respuesta respuesta = productoService.getProductosPorGrupo(idGrupo);

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok(new GenericEntity<List<ProductoDto>>((List<ProductoDto>) respuesta.getResultado("Productos")) {
            }).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos por grupo.", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo productos por grupo").build();
        }
    }

    /**
     * Obtiene productos activos por grupo
     */
    @GET
    @Path("/productos/grupo/{idGrupo}/activos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener productos activos por grupo", description = "Retorna los productos activos que pertenecen a un grupo específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos activos del grupo obtenida exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response getProductosPorGrupoActivos(
            @Parameter(description = "ID del grupo de productos", required = true, example = "1")
            @PathParam("idGrupo") Long idGrupo) {
        try {
            Respuesta respuesta = productoService.getProductosPorGrupoActivos(idGrupo);

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok(new GenericEntity<List<ProductoDto>>((List<ProductoDto>) respuesta.getResultado("Productos")) {
            }).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos activos por grupo.", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo productos activos por grupo").build();
        }
    }

    /**
     * Obtiene productos con acceso rápido
     */
    @GET
    @Path("/productos/accesorapido")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener productos de acceso rápido", description = "Retorna los productos marcados con acceso rápido para facilitar las órdenes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos de acceso rápido obtenida exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response getProductosAccesoRapido() {
        try {
            Respuesta respuesta = productoService.getProductosAccesoRapido();

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok(new GenericEntity<List<ProductoDto>>((List<ProductoDto>) respuesta.getResultado("Productos")) {
            }).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos de acceso rápido.", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo productos de acceso rápido").build();
        }
    }

    /**
     * Obtiene productos más vendidos
     */
    @GET
    @Path("/productos/masvendidos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Obtener productos más vendidos", description = "Retorna los 20 productos más vendidos ordenados por cantidad")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos más vendidos obtenida exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ProductoDto.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response getProductosMasVendidos() {
        try {
            Respuesta respuesta = productoService.getProductosMasVendidos();

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok(new GenericEntity<List<ProductoDto>>((List<ProductoDto>) respuesta.getResultado("Productos")) {
            }).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos más vendidos.", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error obteniendo productos más vendidos").build();
        }
    }

    /**
     * Guarda un producto (crear o actualizar)
     */
    @POST
    @Path("/producto")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Guardar producto", description = "Crea un nuevo producto o actualiza uno existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto guardado exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ProductoDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Grupo de producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response guardarProducto(
            @Parameter(description = "Datos del producto a guardar", required = true)
            ProductoDto productoDto) {
        try {
            Respuesta respuesta = productoService.guardarProducto(productoDto);

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok(respuesta.getResultado("Producto")).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error guardando producto.", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error guardando el producto").build();
        }
    }

    /**
     * Elimina un producto (marca como inactivo)
     */
    @DELETE
    @Path("/producto/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Eliminar producto", description = "Marca un producto como inactivo (no elimina físicamente)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente",
                content = @Content(mediaType = MediaType.APPLICATION_JSON,
                        schema = @Schema(implementation = ProductoDto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public Response eliminarProducto(
            @Parameter(description = "ID único del producto a eliminar", required = true, example = "1")
            @PathParam("id") Long id) {
        try {
            Respuesta respuesta = productoService.eliminarProducto(id);

            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok(respuesta.getResultado("Producto")).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error eliminando producto.", e);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                    .entity("Error eliminando el producto").build();
        }
    }
}
