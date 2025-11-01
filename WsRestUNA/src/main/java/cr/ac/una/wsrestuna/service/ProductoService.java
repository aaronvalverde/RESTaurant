package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.GrupoProducto;
import cr.ac.una.wsrestuna.model.Producto;
import cr.ac.una.wsrestuna.model.ProductoDto;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para gestión de productos del menú
 */
@Stateless
@LocalBean
public class ProductoService {

    private static final Logger LOG = Logger.getLogger(ProductoService.class.getName());

    @PersistenceContext(unitName = "WsRestUNAPU")
    private EntityManager em;

    /**
     * Obtiene un producto por ID
     */
    public Respuesta getProducto(Long id) {
        try {
            Query query = em.createNamedQuery("Producto.findById", Producto.class);
            query.setParameter("idProducto", id);

            Producto producto = (Producto) query.getSingleResult();
            ProductoDto productoDto = new ProductoDto(producto);

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Producto", productoDto);

        } catch (NoResultException e) {
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró el producto con ID: " + id, "getProducto NoResultException");
        } catch (NonUniqueResultException e) {
            LOG.log(Level.SEVERE, "Múltiples productos con el mismo ID.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Se encontraron múltiples productos con el mismo ID.", "getProducto NonUniqueResultException");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo producto.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo el producto.", "getProducto " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los productos
     */
    public Respuesta getProductos() {
        try {
            Query query = em.createNamedQuery("Producto.findAll", Producto.class);
            List<Producto> productos = query.getResultList();

            List<ProductoDto> productosDto = new ArrayList<>();
            for (Producto producto : productos) {
                productosDto.add(new ProductoDto(producto));
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Productos", productosDto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo los productos.", "getProductos " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los productos activos
     */
    public Respuesta getProductosActivos() {
        try {
            Query query = em.createNamedQuery("Producto.findActivos", Producto.class);
            List<Producto> productos = query.getResultList();

            List<ProductoDto> productosDto = new ArrayList<>();
            for (Producto producto : productos) {
                productosDto.add(new ProductoDto(producto));
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Productos", productosDto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos activos.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo los productos activos.", "getProductosActivos " + e.getMessage());
        }
    }

    /**
     * Obtiene productos por grupo
     */
    public Respuesta getProductosPorGrupo(Long idGrupo) {
        try {
            Query query = em.createNamedQuery("Producto.findByGrupo", Producto.class);
            query.setParameter("idGrupo", idGrupo);
            List<Producto> productos = query.getResultList();

            List<ProductoDto> productosDto = new ArrayList<>();
            for (Producto producto : productos) {
                productosDto.add(new ProductoDto(producto));
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Productos", productosDto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos por grupo.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo productos por grupo.", "getProductosPorGrupo " + e.getMessage());
        }
    }

    /**
     * Obtiene productos activos por grupo
     */
    public Respuesta getProductosPorGrupoActivos(Long idGrupo) {
        try {
            Query query = em.createNamedQuery("Producto.findByGrupoActivos", Producto.class);
            query.setParameter("idGrupo", idGrupo);
            List<Producto> productos = query.getResultList();

            List<ProductoDto> productosDto = new ArrayList<>();
            for (Producto producto : productos) {
                productosDto.add(new ProductoDto(producto));
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Productos", productosDto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos activos por grupo.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo productos activos por grupo.", "getProductosPorGrupoActivos " + e.getMessage());
        }
    }

    /**
     * Obtiene productos con acceso rápido
     */
    public Respuesta getProductosAccesoRapido() {
        try {
            Query query = em.createNamedQuery("Producto.findAccesoRapido", Producto.class);
            List<Producto> productos = query.getResultList();

            List<ProductoDto> productosDto = new ArrayList<>();
            for (Producto producto : productos) {
                productosDto.add(new ProductoDto(producto));
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Productos", productosDto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos de acceso rápido.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo productos de acceso rápido.", "getProductosAccesoRapido " + e.getMessage());
        }
    }

    /**
     * Obtiene productos más vendidos
     */
    public Respuesta getProductosMasVendidos() {
        try {
            Query query = em.createNamedQuery("Producto.findMasVendidos", Producto.class);
            query.setMaxResults(20); // Top 20 más vendidos
            List<Producto> productos = query.getResultList();

            List<ProductoDto> productosDto = new ArrayList<>();
            for (Producto producto : productos) {
                productosDto.add(new ProductoDto(producto));
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Productos", productosDto);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error obteniendo productos más vendidos.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo productos más vendidos.", "getProductosMasVendidos " + e.getMessage());
        }
    }

    /**
     * Guarda un producto (crear o actualizar)
     */
    public Respuesta guardarProducto(ProductoDto productoDto) {
        try {
            Producto producto;

            if (productoDto.getIdProducto() != null && productoDto.getIdProducto() > 0) {
                // Actualizar existente
                producto = em.find(Producto.class, productoDto.getIdProducto());
                if (producto == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                            "No se encontró el producto a actualizar", "guardarProducto");
                }

                // Verificar si cambió el nombre y si ya existe
                if (!producto.getNombre().equalsIgnoreCase(productoDto.getNombre())) {
                    Query queryNombre = em.createNamedQuery("Producto.findByNombre", Producto.class);
                    queryNombre.setParameter("nombre", productoDto.getNombre());
                    try {
                        Producto existente = (Producto) queryNombre.getSingleResult();
                        if (!existente.getIdProducto().equals(producto.getIdProducto())) {
                            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                                    "Ya existe un producto con ese nombre", "guardarProducto");
                        }
                    } catch (NoResultException e) {
                        // No existe, puede continuar
                    }
                }

                // Verificar si cambió el nombre corto y si ya existe
                if (!producto.getNombreCorto().equalsIgnoreCase(productoDto.getNombreCorto())) {
                    Query queryCorto = em.createNamedQuery("Producto.findByNombreCorto", Producto.class);
                    queryCorto.setParameter("nombreCorto", productoDto.getNombreCorto());
                    try {
                        Producto existente = (Producto) queryCorto.getSingleResult();
                        if (!existente.getIdProducto().equals(producto.getIdProducto())) {
                            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                                    "Ya existe un producto con ese nombre corto", "guardarProducto");
                        }
                    } catch (NoResultException e) {
                        // No existe, puede continuar
                    }
                }

                producto.actualizar(productoDto);
                producto = em.merge(producto);

            } else {
                // Crear nuevo
                // Verificar si ya existe el nombre
                Query queryNombre = em.createNamedQuery("Producto.findByNombre", Producto.class);
                queryNombre.setParameter("nombre", productoDto.getNombre());
                try {
                    queryNombre.getSingleResult();
                    return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                            "Ya existe un producto con ese nombre", "guardarProducto");
                } catch (NoResultException e) {
                    // No existe, puede continuar
                }

                // Verificar si ya existe el nombre corto
                Query queryCorto = em.createNamedQuery("Producto.findByNombreCorto", Producto.class);
                queryCorto.setParameter("nombreCorto", productoDto.getNombreCorto());
                try {
                    queryCorto.getSingleResult();
                    return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                            "Ya existe un producto con ese nombre corto", "guardarProducto");
                } catch (NoResultException e) {
                    // No existe, puede continuar
                }

                producto = new Producto(productoDto);

                // Asociar el grupo
                if (productoDto.getIdGrupoProducto() != null) {
                    GrupoProducto grupo = em.find(GrupoProducto.class, productoDto.getIdGrupoProducto());
                    if (grupo == null) {
                        return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                                "No se encontró el grupo de producto especificado", "guardarProducto");
                    }
                    producto.setIdGrupoProducto(grupo);
                } else {
                    return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                            "Debe especificar un grupo de producto", "guardarProducto");
                }

                em.persist(producto);
            }

            em.flush();

            ProductoDto resultado = new ProductoDto(producto);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Producto", resultado);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error guardando producto.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error guardando el producto: " + e.getMessage(), "guardarProducto " + e.getMessage());
        }
    }

    /**
     * Elimina un producto
     */
    public Respuesta eliminarProducto(Long id) {
        try {
            Producto producto = em.find(Producto.class, id);

            if (producto == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró el producto a eliminar", "eliminarProducto");
            }

            // Verificar si tiene órdenes asociadas (esto requeriría una consulta adicional)
            // Por ahora solo marcamos como inactivo en lugar de eliminar físicamente
            producto.setEstado("I");
            em.merge(producto);
            em.flush();

            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                    "", "", "Producto", new ProductoDto(producto));

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error eliminando producto.", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error eliminando el producto: " + e.getMessage(), "eliminarProducto " + e.getMessage());
        }
    }
}
