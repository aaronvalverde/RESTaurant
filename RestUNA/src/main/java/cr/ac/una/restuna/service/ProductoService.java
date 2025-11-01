package cr.ac.una.restuna.service;

import cr.ac.una.restuna.dto.ProductoDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para comunicación con el servidor REST para productos
 */
public class ProductoService {

    private static final Logger LOG = Logger.getLogger(ProductoService.class.getName());

    /**
     * Obtiene un producto por ID
     */
    public Respuesta getProducto(Long id) {
        try {
            Request request = new Request("producto/" + id);
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            ProductoDto producto = new ProductoDto(request.readEntity(String.class));
            return new Respuesta(true, "", "", "Producto", producto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo producto.", e);
            return new Respuesta(false, "Error obteniendo el producto.", "getProducto " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los productos
     */
    public Respuesta getProductos() {
        try {
            Request request = new Request("producto/productos");
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String json = request.readEntity(String.class);
            return new Respuesta(true, "", "", "Productos", json);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos.", e);
            return new Respuesta(false, "Error obteniendo los productos.", "getProductos " + e.getMessage());
        }
    }

    /**
     * Obtiene productos activos
     */
    public Respuesta getProductosActivos() {
        try {
            Request request = new Request("producto/productos/activos");
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String json = request.readEntity(String.class);
            return new Respuesta(true, "", "", "Productos", json);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos activos.", e);
            return new Respuesta(false, "Error obteniendo los productos activos.", "getProductosActivos " + e.getMessage());
        }
    }

    /**
     * Obtiene productos por grupo
     */
    public Respuesta getProductosPorGrupo(Long idGrupo) {
        try {
            Request request = new Request("producto/productos/grupo/" + idGrupo);
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String json = request.readEntity(String.class);
            return new Respuesta(true, "", "", "Productos", json);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos por grupo.", e);
            return new Respuesta(false, "Error obteniendo productos por grupo.", "getProductosPorGrupo " + e.getMessage());
        }
    }

    /**
     * Obtiene productos activos por grupo
     */
    public Respuesta getProductosPorGrupoActivos(Long idGrupo) {
        try {
            Request request = new Request("producto/productos/grupo/" + idGrupo + "/activos");
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String json = request.readEntity(String.class);
            return new Respuesta(true, "", "", "Productos", json);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos activos por grupo.", e);
            return new Respuesta(false, "Error obteniendo productos activos por grupo.", "getProductosPorGrupoActivos " + e.getMessage());
        }
    }

    /**
     * Obtiene productos con acceso rápido
     */
    public Respuesta getProductosAccesoRapido() {
        try {
            Request request = new Request("producto/productos/accesorapido");
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String json = request.readEntity(String.class);
            return new Respuesta(true, "", "", "Productos", json);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos de acceso rápido.", e);
            return new Respuesta(false, "Error obteniendo productos de acceso rápido.", "getProductosAccesoRapido " + e.getMessage());
        }
    }

    /**
     * Obtiene productos más vendidos
     */
    public Respuesta getProductosMasVendidos() {
        try {
            Request request = new Request("producto/productos/masvendidos");
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            String json = request.readEntity(String.class);
            return new Respuesta(true, "", "", "Productos", json);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos más vendidos.", e);
            return new Respuesta(false, "Error obteniendo productos más vendidos.", "getProductosMasVendidos " + e.getMessage());
        }
    }

    /**
     * Guarda un producto
     */
    public Respuesta guardarProducto(ProductoDto producto) {
        try {
            Request request = new Request("producto/producto");
            request.post(producto);

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            ProductoDto productoGuardado = new ProductoDto(request.readEntity(String.class));
            return new Respuesta(true, "", "", "Producto", productoGuardado);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error guardando producto.", e);
            return new Respuesta(false, "Error guardando el producto.", "guardarProducto " + e.getMessage());
        }
    }

    /**
     * Elimina un producto
     */
    public Respuesta eliminarProducto(Long id) {
        try {
            Request request = new Request("producto/producto/" + id);
            request.delete();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            return new Respuesta(true, "", "");

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error eliminando producto.", e);
            return new Respuesta(false, "Error eliminando el producto.", "eliminarProducto " + e.getMessage());
        }
    }
}
