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
 * Servicio EJB para la gestión de cierres de caja
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Stateless
@LocalBean
public class CierreCajaService {

    private static final Logger LOGGER = Logger.getLogger(CierreCajaService.class.getName());

    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;

    public Respuesta obtenerTodos() {
        try {
            TypedQuery<CierreCaja> query = em.createNamedQuery("CierreCaja.findAll", CierreCaja.class);
            List<CierreCaja> cierres = query.getResultList();
            List<CierreCajaDto> cierresDto = cierres.stream()
                    .map(CierreCajaDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cierres de caja obtenidos correctamente", "", "CierresCaja", cierresDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todos los cierres de caja", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener cierres de caja", e.getMessage());
        }
    }

    public Respuesta obtenerPorId(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del cierre de caja es requerido", "ID nulo");
            }
            
            CierreCaja cierre = em.find(CierreCaja.class, id);
            if (cierre == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Cierre de caja no encontrado", "No existe cierre con ID: " + id);
            }
            
            CierreCajaDto cierreDto = new CierreCajaDto(cierre);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cierre de caja obtenido correctamente", "", "CierreCaja", cierreDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener cierre de caja por ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener cierre de caja", e.getMessage());
        }
    }

    public Respuesta obtenerPorCajero(Long idCajero) {
        try {
            if (idCajero == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del cajero es requerido", "ID nulo");
            }
            
            TypedQuery<CierreCaja> query = em.createNamedQuery("CierreCaja.findByCajero", CierreCaja.class);
            query.setParameter("idUsuario", idCajero);
            List<CierreCaja> cierres = query.getResultList();
            List<CierreCajaDto> cierresDto = cierres.stream()
                    .map(CierreCajaDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cierres de caja obtenidos correctamente", "", "CierresCaja", cierresDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener cierres por cajero", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener cierres de caja", e.getMessage());
        }
    }

    public Respuesta obtenerPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        try {
            if (fechaInicio == null || fechaFin == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Las fechas son requeridas", "Fechas nulas");
            }
            
            TypedQuery<CierreCaja> query = em.createNamedQuery("CierreCaja.findByFecha", CierreCaja.class);
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            List<CierreCaja> cierres = query.getResultList();
            List<CierreCajaDto> cierresDto = cierres.stream()
                    .map(CierreCajaDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cierres de caja obtenidos correctamente", "", "CierresCaja", cierresDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener cierres por fecha", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener cierres de caja", e.getMessage());
        }
    }

    public Respuesta crear(CierreCajaDto cierreDto) {
        try {
            if (cierreDto == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos del cierre son requeridos", "CierreCajaDto nulo");
            }

            CierreCaja cierre = new CierreCaja();
            cierre.setFechaHora(LocalDateTime.now());
            cierre.setEfectivoInicial(cierreDto.getEfectivoInicial() != null ? cierreDto.getEfectivoInicial() : BigDecimal.ZERO);
            cierre.setEfectivoSistema(cierreDto.getEfectivoSistema() != null ? cierreDto.getEfectivoSistema() : BigDecimal.ZERO);
            cierre.setEfectivoDeclarado(cierreDto.getEfectivoDeclarado() != null ? cierreDto.getEfectivoDeclarado() : BigDecimal.ZERO);
            cierre.setTarjetaSistema(cierreDto.getTarjetaSistema() != null ? cierreDto.getTarjetaSistema() : BigDecimal.ZERO);
            cierre.setTarjetaDeclarado(cierreDto.getTarjetaDeclarado() != null ? cierreDto.getTarjetaDeclarado() : BigDecimal.ZERO);
            cierre.setTotalFacturas(cierreDto.getTotalFacturas() != null ? cierreDto.getTotalFacturas() : 0);
            cierre.setObservaciones(cierreDto.getObservaciones());

            // Cajero
            if (cierreDto.getIdCajero() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El cajero es requerido", "idCajero nulo");
            }
            Usuario cajero = em.find(Usuario.class, cierreDto.getIdCajero());
            if (cajero == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Cajero no encontrado", "No existe usuario con ID: " + cierreDto.getIdCajero());
            }
            cierre.setCajero(cajero);

            // Calcular diferencias
            cierre.calcularDiferencias();

            em.persist(cierre);
            em.flush();
            
            cierreDto = new CierreCajaDto(cierre);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cierre de caja creado correctamente", "", "CierreCaja", cierreDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear cierre de caja", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al crear cierre de caja", e.getMessage());
        }
    }

    public Respuesta actualizar(CierreCajaDto cierreDto) {
        try {
            if (cierreDto == null || cierreDto.getIdCierreCaja() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos del cierre y su ID son requeridos", "CierreCajaDto inválido");
            }

            CierreCaja cierre = em.find(CierreCaja.class, cierreDto.getIdCierreCaja());
            if (cierre == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Cierre de caja no encontrado", "No existe cierre con ID: " + cierreDto.getIdCierreCaja());
            }

            cierre.setEfectivoDeclarado(cierreDto.getEfectivoDeclarado());
            cierre.setTarjetaDeclarado(cierreDto.getTarjetaDeclarado());
            cierre.setObservaciones(cierreDto.getObservaciones());
            cierre.calcularDiferencias();

            em.merge(cierre);
            em.flush();
            
            cierreDto = new CierreCajaDto(cierre);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cierre de caja actualizado correctamente", "", "CierreCaja", cierreDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar cierre de caja", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al actualizar cierre de caja", e.getMessage());
        }
    }

    public Respuesta eliminar(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del cierre es requerido", "ID nulo");
            }
            
            CierreCaja cierre = em.find(CierreCaja.class, id);
            if (cierre == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Cierre de caja no encontrado", "No existe cierre con ID: " + id);
            }
            
            em.remove(cierre);
            em.flush();
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cierre de caja eliminado correctamente", "");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar cierre de caja con ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al eliminar cierre de caja", e.getMessage());
        }
    }
}
