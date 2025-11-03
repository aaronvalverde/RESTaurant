package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.Mesa;
import cr.ac.una.wsrestuna.model.MesaDto;
import cr.ac.una.wsrestuna.model.Seccion;
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
 * Servicio para gestión de mesas del restaurante
 */
@Stateless
@LocalBean
public class MesaService {
    
    private static final Logger LOG = Logger.getLogger(MesaService.class.getName());
    
    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;
    
    /**
     * Obtiene una mesa por ID
     */
    public Respuesta getMesa(Long id) {
        try {
            Mesa mesa = em.find(Mesa.class, id);
            if (mesa == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró la mesa con ID: " + id, "getMesa");
            }
            
            MesaDto dto = new MesaDto(mesa);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getMesa", "Mesa", dto);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesa con ID: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo la mesa: " + ex.getMessage(), "getMesa " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene todas las mesas
     */
    public Respuesta getMesas() {
        try {
            List<Mesa> mesas = em.createNamedQuery("Mesa.findAll", Mesa.class)
                .getResultList();
            
            List<MesaDto> dtos = new ArrayList<>();
            for (Mesa mesa : mesas) {
                dtos.add(new MesaDto(mesa));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getMesas", "Mesas", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo mesas: " + ex.getMessage(), "getMesas " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene todas las mesas de una sección
     */
    public Respuesta getMesasPorSeccion(Long idSeccion) {
        try {
            List<Mesa> mesas = em.createNamedQuery("Mesa.findBySeccion", Mesa.class)
                .setParameter("idSeccion", idSeccion)
                .getResultList();
            
            List<MesaDto> dtos = new ArrayList<>();
            for (Mesa mesa : mesas) {
                dtos.add(new MesaDto(mesa));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getMesasPorSeccion", "Mesas", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas de sección: " + idSeccion, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo mesas: " + ex.getMessage(), "getMesasPorSeccion " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene mesas por estado
     */
    public Respuesta getMesasPorEstado(String estado) {
        try {
            List<Mesa> mesas = em.createNamedQuery("Mesa.findByEstado", Mesa.class)
                .setParameter("estado", estado)
                .getResultList();
            
            List<MesaDto> dtos = new ArrayList<>();
            for (Mesa mesa : mesas) {
                dtos.add(new MesaDto(mesa));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getMesasPorEstado", "Mesas", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas por estado: " + estado, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo mesas: " + ex.getMessage(), "getMesasPorEstado " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene mesas libres
     */
    public Respuesta getMesasLibres() {
        try {
            List<Mesa> mesas = em.createNamedQuery("Mesa.findLibres", Mesa.class)
                .getResultList();
            
            List<MesaDto> dtos = new ArrayList<>();
            for (Mesa mesa : mesas) {
                dtos.add(new MesaDto(mesa));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getMesasLibres", "Mesas", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo mesas libres", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo mesas libres: " + ex.getMessage(), "getMesasLibres " + ex.getMessage());
        }
    }
    
    /**
     * Guarda una nueva mesa o actualiza una existente
     */
    public Respuesta guardarMesa(MesaDto dto) {
        try {
            Mesa mesa;
            
            if (dto.getIdMesa() != null && dto.getIdMesa() > 0) {
                // Actualizar existente
                mesa = em.find(Mesa.class, dto.getIdMesa());
                if (mesa == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró la mesa a actualizar", "guardarMesa");
                }
                
                // Validar número de mesa único en sección al actualizar
                if (dto.getNumeroMesa() != null && !dto.getNumeroMesa().equals(mesa.getNumeroMesa())) {
                    List<Mesa> mesasExistentes = em.createQuery(
                        "SELECT m FROM Mesa m WHERE m.numeroMesa = :numero AND m.seccion.idSeccion = :idSeccion AND m.idMesa <> :idMesa", Mesa.class)
                        .setParameter("numero", dto.getNumeroMesa())
                        .setParameter("idSeccion", dto.getIdSeccion())
                        .setParameter("idMesa", mesa.getIdMesa())
                        .getResultList();
                    
                    if (!mesasExistentes.isEmpty()) {
                        return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE,
                            "Ya existe una mesa con el número '" + dto.getNumeroMesa() + "' en esta sección", "guardarMesa");
                    }
                }
                
                mesa.actualizar(dto);
                mesa = em.merge(mesa);
            } else {
                // Validar sección
                if (dto.getIdSeccion() == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE,
                        "Debe especificar una sección para la mesa", "guardarMesa");
                }
                
                Seccion seccion = em.find(Seccion.class, dto.getIdSeccion());
                if (seccion == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró la sección especificada", "guardarMesa");
                }
                
                // Validar número de mesa único en sección
                List<Mesa> mesasExistentes = em.createQuery(
                    "SELECT m FROM Mesa m WHERE m.numeroMesa = :numero AND m.seccion.idSeccion = :idSeccion", Mesa.class)
                    .setParameter("numero", dto.getNumeroMesa())
                    .setParameter("idSeccion", dto.getIdSeccion())
                    .getResultList();
                
                if (!mesasExistentes.isEmpty()) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE,
                        "Ya existe una mesa con el número '" + dto.getNumeroMesa() + "' en esta sección", "guardarMesa");
                }
                
                // Crear nueva
                mesa = new Mesa(dto);
                mesa.setSeccion(seccion);
                em.persist(mesa);
            }
            
            em.flush();
            
            MesaDto resultado = new MesaDto(mesa);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "guardarMesa", "Mesa", resultado);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando mesa", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error guardando mesa: " + ex.getMessage(), "guardarMesa " + ex.getMessage());
        }
    }
    
    /**
     * Guarda múltiples mesas (batch)
     */
    public Respuesta guardarMesas(List<MesaDto> dtos) {
        try {
            List<MesaDto> resultados = new ArrayList<>();
            
            for (MesaDto dto : dtos) {
                Respuesta resp = guardarMesa(dto);
                if (!resp.getEstado()) {
                    return resp; // Si falla una, devolver error
                }
                resultados.add((MesaDto) resp.getResultado("Mesa"));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "guardarMesas", "Mesas", resultados);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando mesas batch", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error guardando mesas: " + ex.getMessage(), "guardarMesas " + ex.getMessage());
        }
    }
    
    /**
     * Actualiza el estado de una mesa
     */
    public Respuesta actualizarEstadoMesa(Long id, String estado) {
        try {
            Mesa mesa = em.find(Mesa.class, id);
            if (mesa == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró la mesa", "actualizarEstadoMesa");
            }
            
            mesa.setEstado(estado);
            mesa = em.merge(mesa);
            em.flush();
            
            MesaDto dto = new MesaDto(mesa);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "actualizarEstadoMesa", "Mesa", dto);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error actualizando estado de mesa: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error actualizando estado: " + ex.getMessage(), "actualizarEstadoMesa " + ex.getMessage());
        }
    }
    
    /**
     * Elimina una mesa por ID
     */
    public Respuesta eliminarMesa(Long id) {
        try {
            Mesa mesa = em.find(Mesa.class, id);
            if (mesa == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró la mesa a eliminar", "eliminarMesa");
            }
            
            em.remove(mesa);
            em.flush();
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "eliminarMesa", "Id", id);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando mesa con ID: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error eliminando mesa: " + ex.getMessage(), "eliminarMesa " + ex.getMessage());
        }
    }
}
