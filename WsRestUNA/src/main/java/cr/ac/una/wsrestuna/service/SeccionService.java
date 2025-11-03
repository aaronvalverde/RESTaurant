package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.Archivo;
import cr.ac.una.wsrestuna.model.Seccion;
import cr.ac.una.wsrestuna.model.SeccionDto;
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


@Stateless
@LocalBean
public class SeccionService {
    
    private static final Logger LOG = Logger.getLogger(SeccionService.class.getName());
    
    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;
    
    
    public Respuesta getSeccion(Long id) {
        try {
            Seccion seccion = em.find(Seccion.class, id);
            if (seccion == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró la sección con ID: " + id, "getSeccion");
            }
            
            SeccionDto dto = new SeccionDto(seccion, false);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getSeccion", "Seccion", dto);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo sección con ID: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo la sección: " + ex.getMessage(), "getSeccion " + ex.getMessage());
        }
    }
    
    
    public Respuesta getSeccionConImagen(Long id) {
        try {
            Seccion seccion = em.find(Seccion.class, id);
            if (seccion == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró la sección con ID: " + id, "getSeccionConImagen");
            }
            
            
            if (seccion.getArchivoImagen() != null) {
                seccion.getArchivoImagen().getContenido();
            }
            
            SeccionDto dto = new SeccionDto(seccion, true);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getSeccionConImagen", "Seccion", dto);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo sección con imagen ID: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo la sección: " + ex.getMessage(), "getSeccionConImagen " + ex.getMessage());
        }
    }
    
    
    public Respuesta getSecciones() {
        try {
            List<Seccion> secciones = em.createNamedQuery("Seccion.findAll", Seccion.class)
                .getResultList();
            
            List<SeccionDto> dtos = new ArrayList<>();
            for (Seccion seccion : secciones) {
                dtos.add(new SeccionDto(seccion, false));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getSecciones", "Secciones", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo secciones", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo secciones: " + ex.getMessage(), "getSecciones " + ex.getMessage());
        }
    }
    
    
    public Respuesta getSeccionesActivas() {
        try {
            List<Seccion> secciones = em.createNamedQuery("Seccion.findActivas", Seccion.class)
                .getResultList();
            
            List<SeccionDto> dtos = new ArrayList<>();
            for (Seccion seccion : secciones) {
                dtos.add(new SeccionDto(seccion, false));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "getSeccionesActivas", "Secciones", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo secciones activas", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error obteniendo secciones activas: " + ex.getMessage(), "getSeccionesActivas " + ex.getMessage());
        }
    }
    
    
    public Respuesta guardarSeccion(SeccionDto dto) {
        try {
            Seccion seccion;
            
            if (dto.getIdSeccion() != null && dto.getIdSeccion() > 0) {
                
                seccion = em.find(Seccion.class, dto.getIdSeccion());
                if (seccion == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró la sección a actualizar", "guardarSeccion");
                }
                seccion.actualizar(dto);
                
                
                if (dto.getIdArchivoImagen() != null) {
                    Archivo archivo = em.find(Archivo.class, dto.getIdArchivoImagen());
                    seccion.setArchivoImagen(archivo);
                } else {
                    seccion.setArchivoImagen(null);
                }
                
                seccion = em.merge(seccion);
            } else {
                
                seccion = new Seccion(dto);
                
                
                if (dto.getIdArchivoImagen() != null) {
                    Archivo archivo = em.find(Archivo.class, dto.getIdArchivoImagen());
                    if (archivo != null) {
                        seccion.setArchivoImagen(archivo);
                    }
                }
                
                em.persist(seccion);
            }
            
            em.flush();
            
            SeccionDto resultado = new SeccionDto(seccion, false);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "guardarSeccion", "Seccion", resultado);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando sección", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error guardando sección: " + ex.getMessage(), "guardarSeccion " + ex.getMessage());
        }
    }
    
    
    public Respuesta eliminarSeccion(Long id) {
        try {
            Seccion seccion = em.find(Seccion.class, id);
            if (seccion == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró la sección a eliminar", "eliminarSeccion");
            }
            
            
            
            
            em.remove(seccion);
            em.flush();
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "eliminarSeccion", "Id", id);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando sección con ID: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error eliminando sección: " + ex.getMessage(), "eliminarSeccion " + ex.getMessage());
        }
    }
}
