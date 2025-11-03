package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.*;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servicio EJB para la gestión de órdenes
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Stateless
@LocalBean
public class OrdenService {

    private static final Logger LOGGER = Logger.getLogger(OrdenService.class.getName());

    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;

    public Respuesta obtenerTodas() {
        try {
            TypedQuery<Orden> query = em.createNamedQuery("Orden.findAll", Orden.class);
            List<Orden> ordenes = query.getResultList();
            List<OrdenDto> ordenesDto = ordenes.stream()
                    .map(OrdenDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Órdenes obtenidas correctamente", "", "Ordenes", ordenesDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todas las órdenes", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener órdenes", e.getMessage());
        }
    }

    public Respuesta obtenerPorId(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID de la orden es requerido", "ID nulo");
            }
            
            Orden orden = em.find(Orden.class, id);
            if (orden == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Orden no encontrada", "No existe orden con ID: " + id);
            }
            
            OrdenDto ordenDto = new OrdenDto(orden);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Orden obtenida correctamente", "", "Orden", ordenDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener orden por ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener orden", e.getMessage());
        }
    }

    public Respuesta obtenerPorEstado(String estado) {
        try {
            if (estado == null || estado.trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El estado es requerido", "Estado vacío");
            }
            
            TypedQuery<Orden> query = em.createNamedQuery("Orden.findByEstado", Orden.class);
            query.setParameter("estado", estado);
            List<Orden> ordenes = query.getResultList();
            List<OrdenDto> ordenesDto = ordenes.stream()
                    .map(OrdenDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Órdenes obtenidas correctamente", "", "Ordenes", ordenesDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener órdenes por estado: " + estado, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener órdenes", e.getMessage());
        }
    }

    public Respuesta obtenerPorMesa(Long idMesa, String estado) {
        try {
            if (idMesa == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID de la mesa es requerido", "ID nulo");
            }
            
            TypedQuery<Orden> query;
            List<Orden> ordenes;
            
            if (estado == null || estado.trim().isEmpty()) {
                // Si no se especifica estado, buscar todas las órdenes de la mesa
                query = em.createQuery(
                    "SELECT o FROM Orden o WHERE o.mesa.idMesa = :idMesa ORDER BY o.fechaHora DESC", 
                    Orden.class);
                query.setParameter("idMesa", idMesa);
                ordenes = query.getResultList();
            } else {
                // Si se especifica estado, usar el NamedQuery
                query = em.createNamedQuery("Orden.findByMesa", Orden.class);
                query.setParameter("idMesa", idMesa);
                query.setParameter("estado", estado);
                ordenes = query.getResultList();
            }
            
            List<OrdenDto> ordenesDto = ordenes.stream()
                    .map(OrdenDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Órdenes obtenidas correctamente", "", "Ordenes", ordenesDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener órdenes por mesa", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener órdenes", e.getMessage());
        }
    }

    public Respuesta obtenerPorSeccion(Long idSeccion, String estado) {
        try {
            if (idSeccion == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID de la sección es requerido", "ID nulo");
            }
            
            TypedQuery<Orden> query;
            List<Orden> ordenes;
            
            if (estado == null || estado.trim().isEmpty()) {
                // Si no se especifica estado, buscar todas las órdenes de la sección
                query = em.createQuery(
                    "SELECT o FROM Orden o WHERE o.seccion.idSeccion = :idSeccion ORDER BY o.fechaHora DESC", 
                    Orden.class);
                query.setParameter("idSeccion", idSeccion);
                ordenes = query.getResultList();
            } else {
                // Si se especifica estado, usar el NamedQuery
                query = em.createNamedQuery("Orden.findBySeccion", Orden.class);
                query.setParameter("idSeccion", idSeccion);
                query.setParameter("estado", estado);
                ordenes = query.getResultList();
            }
            
            List<OrdenDto> ordenesDto = ordenes.stream()
                    .map(OrdenDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Órdenes obtenidas correctamente", "", "Ordenes", ordenesDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener órdenes por sección", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener órdenes", e.getMessage());
        }
    }

    public Respuesta crear(OrdenDto ordenDto) {
        try {
            if (ordenDto == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos de la orden son requeridos", "OrdenDto nulo");
            }

            Orden orden = new Orden();
            orden.setFechaHora(LocalDateTime.now());
            orden.setEstado(ordenDto.getEstado() != null ? ordenDto.getEstado() : "PENDIENTE");
            orden.setObservaciones(ordenDto.getObservaciones());

            // Relaciones
            if (ordenDto.getIdMesa() != null) {
                Mesa mesa = em.find(Mesa.class, ordenDto.getIdMesa());
                if (mesa == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                            "Mesa no encontrada", "No existe mesa con ID: " + ordenDto.getIdMesa());
                }
                orden.setMesa(mesa);
            }

            if (ordenDto.getIdCliente() != null) {
                Cliente cliente = em.find(Cliente.class, ordenDto.getIdCliente());
                if (cliente == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                            "Cliente no encontrado", "No existe cliente con ID: " + ordenDto.getIdCliente());
                }
                orden.setCliente(cliente);
            }

            if (ordenDto.getIdSeccion() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "La sección es requerida", "idSeccion nulo");
            }
            Seccion seccion = em.find(Seccion.class, ordenDto.getIdSeccion());
            if (seccion == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Sección no encontrada", "No existe sección con ID: " + ordenDto.getIdSeccion());
            }
            orden.setSeccion(seccion);

            if (ordenDto.getIdSalonero() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El salonero es requerido", "idSalonero nulo");
            }
            Usuario salonero = em.find(Usuario.class, ordenDto.getIdSalonero());
            if (salonero == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Salonero no encontrado", "No existe usuario con ID: " + ordenDto.getIdSalonero());
            }
            orden.setSalonero(salonero);

            // Detalles
            if (ordenDto.getDetalles() != null && !ordenDto.getDetalles().isEmpty()) {
                for (DetalleOrdenDto detalleDto : ordenDto.getDetalles()) {
                    Producto producto = em.find(Producto.class, detalleDto.getIdProducto());
                    if (producto == null) {
                        return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                                "Producto no encontrado", "No existe producto con ID: " + detalleDto.getIdProducto());
                    }
                    
                    DetalleOrden detalle = new DetalleOrden(producto, detalleDto.getCantidad());
                    orden.agregarDetalle(detalle);
                }
            }

            em.persist(orden);
            em.flush();
            
            ordenDto = new OrdenDto(orden);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Orden creada correctamente", "", "Orden", ordenDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear orden", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al crear orden", e.getMessage());
        }
    }

    public Respuesta actualizar(OrdenDto ordenDto) {
        try {
            if (ordenDto == null || ordenDto.getIdOrden() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos de la orden y su ID son requeridos", "OrdenDto inválido");
            }

            Orden orden = em.find(Orden.class, ordenDto.getIdOrden());
            if (orden == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Orden no encontrada", "No existe orden con ID: " + ordenDto.getIdOrden());
            }

            orden.setEstado(ordenDto.getEstado());
            orden.setObservaciones(ordenDto.getObservaciones());

            em.merge(orden);
            em.flush();
            
            ordenDto = new OrdenDto(orden);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Orden actualizada correctamente", "", "Orden", ordenDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar orden", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al actualizar orden", e.getMessage());
        }
    }

    public Respuesta cambiarEstado(Long id, String nuevoEstado) {
        try {
            if (id == null || nuevoEstado == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID y el nuevo estado son requeridos", "Parámetros nulos");
            }

            Orden orden = em.find(Orden.class, id);
            if (orden == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Orden no encontrada", "No existe orden con ID: " + id);
            }

            orden.setEstado(nuevoEstado);
            em.merge(orden);
            em.flush();

            OrdenDto ordenDto = new OrdenDto(orden);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Estado de orden actualizado correctamente", "", "Orden", ordenDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cambiar estado de orden", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al cambiar estado", e.getMessage());
        }
    }

    public Respuesta eliminar(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID de la orden es requerido", "ID nulo");
            }
            
            Orden orden = em.find(Orden.class, id);
            if (orden == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Orden no encontrada", "No existe orden con ID: " + id);
            }
            
            em.remove(orden);
            em.flush();
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Orden eliminada correctamente", "");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar orden con ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al eliminar orden", e.getMessage());
        }
    }
}
