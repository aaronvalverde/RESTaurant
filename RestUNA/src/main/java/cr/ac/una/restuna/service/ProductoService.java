package cr.ac.una.restuna.service;

import cr.ac.una.restuna.model.ProductoDto;
import cr.ac.una.restuna.util.Request;
import cr.ac.una.restuna.util.Respuesta;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProductoService {

    private static final Logger LOG = Logger.getLogger(ProductoService.class.getName());

    
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
