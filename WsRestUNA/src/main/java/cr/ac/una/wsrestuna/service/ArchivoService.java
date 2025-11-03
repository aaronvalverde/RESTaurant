package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.Archivo;
import cr.ac.una.wsrestuna.model.ArchivoDto;
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
public class ArchivoService {
    
    private static final Logger LOG = Logger.getLogger(ArchivoService.class.getName());
    
    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;
    
    
    public Respuesta getArchivo(Long id) {
        try {
            Archivo archivo = em.find(Archivo.class, id);
            if (archivo == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                    "No se encontró el archivo con ID: " + id, "getArchivo", null);
            }
            
            
            archivo.getContenido();
            
            ArchivoDto dto = new ArchivoDto(archivo, true);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                "", "getArchivo", "Archivo", dto);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo archivo con ID: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                "Error obteniendo el archivo: " + ex.getMessage(), "getArchivo", ex.getMessage());
        }
    }
    
    
    public Respuesta getArchivos() {
        try {
            List<Archivo> archivos = em.createNamedQuery("Archivo.findAll", Archivo.class)
                .getResultList();
            
            List<ArchivoDto> dtos = new ArrayList<>();
            for (Archivo archivo : archivos) {
                dtos.add(new ArchivoDto(archivo, false));
            }
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                "", "getArchivos", "Archivos", dtos);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo archivos", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                "Error obteniendo archivos: " + ex.getMessage(), "getArchivos", ex.getMessage());
        }
    }
    
    
    public Respuesta guardarArchivo(ArchivoDto dto) {
        try {
            Archivo archivo;
            
            if (dto.getIdArchivo() != null && dto.getIdArchivo() > 0) {
                
                archivo = em.find(Archivo.class, dto.getIdArchivo());
                if (archivo == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró el archivo a actualizar", "guardarArchivo", null);
                }
                archivo.actualizar(dto);
                archivo = em.merge(archivo);
            } else {
                
                archivo = new Archivo(dto);
                em.persist(archivo);
            }
            
            em.flush();
            
            
            ArchivoDto resultado = new ArchivoDto(archivo, false);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "guardarArchivo", "Archivo", resultado);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando archivo", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error guardando archivo: " + ex.getMessage(), "guardarArchivo", ex.getMessage());
        }
    }
    
    
    public Respuesta eliminarArchivo(Long id) {
        try {
            Archivo archivo = em.find(Archivo.class, id);
            if (archivo == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró el archivo a eliminar", "eliminarArchivo", null);
            }
            
            
            Long count = em.createQuery(
                "SELECT COUNT(s) FROM Seccion s WHERE s.archivoImagen.idArchivo = :idArchivo", Long.class)
                .setParameter("idArchivo", id)
                .getSingleResult();
                
            if (count > 0) {
                return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "No se puede eliminar el archivo porque está siendo usado por " + count + " sección(es)",
                    "eliminarArchivo", null);
            }
            
            em.remove(archivo);
            em.flush();
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "eliminarArchivo", "Id", id);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error eliminando archivo con ID: " + id, ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error eliminando archivo: " + ex.getMessage(), "eliminarArchivo", ex.getMessage());
        }
    }
}
