package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.GrupoProducto;
import cr.ac.una.wsrestuna.model.GrupoProductoDto;
import cr.ac.una.wsrestuna.util.Respuesta;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para gestión de grupos/categorías de productos
 */
@Stateless
@LocalBean
public class GrupoProductoService {
    
    private static final Logger LOG = Logger.getLogger(GrupoProductoService.class.getName());
    
    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;
    
    /**
     * Obtiene un grupo de productos por ID
     */
    public Respuesta getGrupoProducto(Long id) {
        try {
            GrupoProducto grupo = em.find(GrupoProducto.class, id);
            if (grupo == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró el grupo con ID: " + id, "getGrupoProducto");
            }
            
            GrupoProductoDto dto = new GrupoProductoDto(grupo);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getGrupoProducto", "GrupoProducto", dto);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupo con ID: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo el grupo: " + ex.getMessage(), "getGrupoProducto " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene todos los grupos de productos ordenados por orden de visualización
     */
    public Respuesta getGrupoProductos() {
        try {
            List<GrupoProducto> grupos = em.createNamedQuery("GrupoProducto.findAll", GrupoProducto.class)
                .getResultList();
            
            List<GrupoProductoDto> dtos = new ArrayList<>();
            for (GrupoProducto grupo : grupos) {
                dtos.add(new GrupoProductoDto(grupo));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getGrupoProductos", "GrupoProductos", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos de productos", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo grupos: " + ex.getMessage(), "getGrupoProductos " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene solo los grupos activos ordenados por orden de visualización
     */
    public Respuesta getGrupoProductosActivos() {
        try {
            List<GrupoProducto> grupos = em.createNamedQuery("GrupoProducto.findActivos", GrupoProducto.class)
                .getResultList();
            
            List<GrupoProductoDto> dtos = new ArrayList<>();
            for (GrupoProducto grupo : grupos) {
                dtos.add(new GrupoProductoDto(grupo));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getGrupoProductosActivos", "GrupoProductos", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos activos", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo grupos activos: " + ex.getMessage(), "getGrupoProductosActivos " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene los grupos marcados para acceso rápido (para menú POS)
     */
    public Respuesta getGrupoProductosAccesoRapido() {
        try {
            List<GrupoProducto> grupos = em.createNamedQuery("GrupoProducto.findAccesoRapido", GrupoProducto.class)
                .getResultList();
            
            List<GrupoProductoDto> dtos = new ArrayList<>();
            for (GrupoProducto grupo : grupos) {
                dtos.add(new GrupoProductoDto(grupo));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getGrupoProductosAccesoRapido", "GrupoProductos", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos de acceso rápido", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo grupos de acceso rápido: " + ex.getMessage(), "getGrupoProductosAccesoRapido " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene los grupos más vendidos
     */
    public Respuesta getGrupoProductosMasVendidos() {
        try {
            List<GrupoProducto> grupos = em.createNamedQuery("GrupoProducto.findMasVendidos", GrupoProducto.class)
                .setMaxResults(10) // Top 10
                .getResultList();
            
            List<GrupoProductoDto> dtos = new ArrayList<>();
            for (GrupoProducto grupo : grupos) {
                dtos.add(new GrupoProductoDto(grupo));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getGrupoProductosMasVendidos", "GrupoProductos", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo grupos más vendidos", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo grupos más vendidos: " + ex.getMessage(), "getGrupoProductosMasVendidos " + ex.getMessage());
        }
    }
    
    /**
     * Guarda un nuevo grupo o actualiza uno existente
     */
    public Respuesta guardarGrupoProducto(GrupoProductoDto dto) {
        try {
            GrupoProducto grupo;
            
            if (dto.getIdGrupoProducto() != null && dto.getIdGrupoProducto() > 0) {
                // Actualizar existente
                grupo = em.find(GrupoProducto.class, dto.getIdGrupoProducto());
                if (grupo == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró el grupo a actualizar", "guardarGrupoProducto");
                }
                grupo.actualizar(dto);
                grupo = em.merge(grupo);
            } else {
                // Crear nuevo
                grupo = new GrupoProducto(dto);
                
                // Si no se especificó orden de visualización, asignar el siguiente disponible
                if (grupo.getOrdenVisualizacion() == null || grupo.getOrdenVisualizacion() == 0) {
                    Long maxOrden = em.createQuery(
                        "SELECT COALESCE(MAX(g.ordenVisualizacion), 0) FROM GrupoProducto g", Long.class)
                        .getSingleResult();
                    grupo.setOrdenVisualizacion(maxOrden + 1);
                }
                
                em.persist(grupo);
            }
            
            em.flush();
            
            GrupoProductoDto resultado = new GrupoProductoDto(grupo);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "guardarGrupoProducto", "GrupoProducto", resultado);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando grupo de productos", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error guardando grupo: " + ex.getMessage(), "guardarGrupoProducto " + ex.getMessage());
        }
    }
    
    /**
     * Elimina un grupo de productos por ID
     */
    public Respuesta eliminarGrupoProducto(Long id) {
        try {
            GrupoProducto grupo = em.find(GrupoProducto.class, id);
            if (grupo == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró el grupo a eliminar", "eliminarGrupoProducto");
            }
            
            // Verificar si tiene productos asociados
            Long cantProductos = em.createQuery(
                "SELECT COUNT(p) FROM Producto p WHERE p.grupoProducto.idGrupoProducto = :idGrupo", Long.class)
                .setParameter("idGrupo", id)
                .getSingleResult();
            
            if (cantProductos > 0) {
                return new Respuesta(false, CodigoRespuesta.ERROR_PERMISOS,
                    "No se puede eliminar el grupo porque tiene " + cantProductos + " producto(s) asociado(s)", 
                    "eliminarGrupoProducto");
            }
            
            em.remove(grupo);
            em.flush();
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "eliminarGrupoProducto", "Id", id);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando grupo con ID: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error eliminando grupo: " + ex.getMessage(), "eliminarGrupoProducto " + ex.getMessage());
        }
    }
}
