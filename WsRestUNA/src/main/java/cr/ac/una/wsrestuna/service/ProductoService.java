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


@Stateless
@LocalBean
public class ProductoService {

    private static final Logger LOG = Logger.getLogger(ProductoService.class.getName());

    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;

    
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

    
    public Respuesta getProductosMasVendidos() {
        try {
            Query query = em.createNamedQuery("Producto.findMasVendidos", Producto.class);
            query.setMaxResults(20); 
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

    
    public Respuesta guardarProducto(ProductoDto productoDto) {
        try {
            Producto producto;

            if (productoDto.getIdProducto() != null && productoDto.getIdProducto() > 0) {
                
                producto = em.find(Producto.class, productoDto.getIdProducto());
                if (producto == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                            "No se encontró el producto a actualizar", "guardarProducto");
                }

                
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
                        
                    }
                }

                
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
                        
                    }
                }

                producto.actualizar(productoDto);
                producto = em.merge(producto);

            } else {
                
                
                Query queryNombre = em.createNamedQuery("Producto.findByNombre", Producto.class);
                queryNombre.setParameter("nombre", productoDto.getNombre());
                try {
                    queryNombre.getSingleResult();
                    return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                            "Ya existe un producto con ese nombre", "guardarProducto");
                } catch (NoResultException e) {
                    
                }

                
                Query queryCorto = em.createNamedQuery("Producto.findByNombreCorto", Producto.class);
                queryCorto.setParameter("nombreCorto", productoDto.getNombreCorto());
                try {
                    queryCorto.getSingleResult();
                    return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                            "Ya existe un producto con ese nombre corto", "guardarProducto");
                } catch (NoResultException e) {
                    
                }

                producto = new Producto(productoDto);

                
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

    
    public Respuesta eliminarProducto(Long id) {
        try {
            Producto producto = em.find(Producto.class, id);

            if (producto == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró el producto a eliminar", "eliminarProducto");
            }

            
            
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
