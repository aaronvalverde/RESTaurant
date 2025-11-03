package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.*;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servicio EJB para la gestión de facturas
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Stateless
@LocalBean
public class FacturaService {

    private static final Logger LOGGER = Logger.getLogger(FacturaService.class.getName());

    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;

    public Respuesta obtenerTodas() {
        try {
            TypedQuery<Factura> query = em.createNamedQuery("Factura.findAll", Factura.class);
            List<Factura> facturas = query.getResultList();
            List<FacturaDto> facturasDto = facturas.stream()
                    .map(FacturaDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Facturas obtenidas correctamente", "", "Facturas", facturasDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todas las facturas", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener facturas", e.getMessage());
        }
    }

    public Respuesta obtenerPorId(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID de la factura es requerido", "ID nulo");
            }
            
            Factura factura = em.find(Factura.class, id);
            if (factura == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Factura no encontrada", "No existe factura con ID: " + id);
            }
            
            FacturaDto facturaDto = new FacturaDto(factura);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Factura obtenida correctamente", "", "Factura", facturaDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener factura por ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener factura", e.getMessage());
        }
    }

    public Respuesta obtenerPorCajero(Long idCajero) {
        try {
            if (idCajero == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del cajero es requerido", "ID nulo");
            }
            
            TypedQuery<Factura> query = em.createNamedQuery("Factura.findByCajero", Factura.class);
            query.setParameter("idUsuario", idCajero);
            List<Factura> facturas = query.getResultList();
            List<FacturaDto> facturasDto = facturas.stream()
                    .map(FacturaDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Facturas obtenidas correctamente", "", "Facturas", facturasDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener facturas por cajero", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener facturas", e.getMessage());
        }
    }

    public Respuesta obtenerPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        try {
            if (fechaInicio == null || fechaFin == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Las fechas son requeridas", "Fechas nulas");
            }
            
            TypedQuery<Factura> query = em.createNamedQuery("Factura.findByFecha", Factura.class);
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            List<Factura> facturas = query.getResultList();
            List<FacturaDto> facturasDto = facturas.stream()
                    .map(FacturaDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Facturas obtenidas correctamente", "", "Facturas", facturasDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener facturas por fecha", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener facturas", e.getMessage());
        }
    }

    public Respuesta crear(FacturaDto facturaDto) {
        try {
            if (facturaDto == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos de la factura son requeridos", "FacturaDto nulo");
            }

            Factura factura = new Factura();
            factura.setFechaHora(LocalDateTime.now());
            factura.setSubtotal(facturaDto.getSubtotal() != null ? facturaDto.getSubtotal() : BigDecimal.ZERO);
            factura.setImpuestoVenta(facturaDto.getImpuestoVenta() != null ? facturaDto.getImpuestoVenta() : BigDecimal.ZERO);
            factura.setImpuestoServicio(facturaDto.getImpuestoServicio() != null ? facturaDto.getImpuestoServicio() : BigDecimal.ZERO);
            factura.setDescuento(facturaDto.getDescuento() != null ? facturaDto.getDescuento() : BigDecimal.ZERO);
            factura.setTotal(facturaDto.getTotal() != null ? facturaDto.getTotal() : BigDecimal.ZERO);
            factura.setEfectivoRecibido(facturaDto.getEfectivoRecibido() != null ? facturaDto.getEfectivoRecibido() : BigDecimal.ZERO);
            factura.setTarjetaRecibido(facturaDto.getTarjetaRecibido() != null ? facturaDto.getTarjetaRecibido() : BigDecimal.ZERO);
            factura.setEstado(facturaDto.getEstado() != null ? facturaDto.getEstado() : "ACTIVA");
            factura.setCorreoEnviado(facturaDto.getCorreoEnviado() != null ? facturaDto.getCorreoEnviado() : "N");
            
            // El numeroFactura se genera automáticamente por trigger en la BD
            if (facturaDto.getNumeroFactura() != null && !facturaDto.getNumeroFactura().isEmpty()) {
                factura.setNumeroFactura(facturaDto.getNumeroFactura());
            }

            // Relaciones
            if (facturaDto.getIdOrden() != null) {
                Orden orden = em.find(Orden.class, facturaDto.getIdOrden());
                if (orden == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                            "Orden no encontrada", "No existe orden con ID: " + facturaDto.getIdOrden());
                }
                factura.setOrden(orden);
            }

            if (facturaDto.getIdCliente() != null) {
                Cliente cliente = em.find(Cliente.class, facturaDto.getIdCliente());
                if (cliente == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                            "Cliente no encontrado", "No existe cliente con ID: " + facturaDto.getIdCliente());
                }
                factura.setCliente(cliente);
            }

            if (facturaDto.getIdCajero() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El cajero es requerido", "idCajero nulo");
            }
            Usuario cajero = em.find(Usuario.class, facturaDto.getIdCajero());
            if (cajero == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Cajero no encontrado", "No existe usuario con ID: " + facturaDto.getIdCajero());
            }
            factura.setCajero(cajero);

            // Detalles
            if (facturaDto.getDetalles() != null && !facturaDto.getDetalles().isEmpty()) {
                for (DetalleFacturaDto detalleDto : facturaDto.getDetalles()) {
                    Producto producto = em.find(Producto.class, detalleDto.getIdProducto());
                    if (producto == null) {
                        return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                                "Producto no encontrado", "No existe producto con ID: " + detalleDto.getIdProducto());
                    }
                    
                    DetalleFactura detalle = new DetalleFactura(producto, detalleDto.getCantidad());
                    factura.agregarDetalle(detalle);
                }
            }

            // Calcular vuelto
            factura.calcularVuelto();

            em.persist(factura);
            em.flush();
            
            facturaDto = new FacturaDto(factura);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Factura creada correctamente", "", "Factura", facturaDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear factura", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al crear factura", e.getMessage());
        }
    }

    public Respuesta eliminar(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID de la factura es requerido", "ID nulo");
            }
            
            Factura factura = em.find(Factura.class, id);
            if (factura == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Factura no encontrada", "No existe factura con ID: " + id);
            }
            
            em.remove(factura);
            em.flush();
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Factura eliminada correctamente", "");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar factura con ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al eliminar factura", e.getMessage());
        }
    }
}
