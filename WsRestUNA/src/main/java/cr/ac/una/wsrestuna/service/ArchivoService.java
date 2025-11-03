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

/**
 * Servicio para gestión de archivos (imágenes)
 */
@Stateless
@LocalBean
public class ArchivoService {
    
    private static final Logger LOG = Logger.getLogger(ArchivoService.class.getName());
    
    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;
    
    /**
     * Obtiene un archivo por ID incluyendo su contenido
     */
    public Respuesta getArchivo(Long id) {
        try {
            Archivo archivo = em.find(Archivo.class, id);
            if (archivo == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                    "No se encontró el archivo con ID: " + id, "getArchivo", null);
            }
            
            // Forzar la carga del BLOB antes de devolver
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
    
    /**
     * Obtiene todos los archivos sin su contenido (solo metadata)
     */
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
    
    /**
     * Guarda un nuevo archivo o actualiza uno existente
     */
    public Respuesta guardarArchivo(ArchivoDto dto) {
        try {
            Archivo archivo;
            
            if (dto.getIdArchivo() != null && dto.getIdArchivo() > 0) {
                // Actualizar existente
                archivo = em.find(Archivo.class, dto.getIdArchivo());
                if (archivo == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No se encontró el archivo a actualizar", "guardarArchivo", null);
                }
                archivo.actualizar(dto);
                archivo = em.merge(archivo);
            } else {
                // Crear nuevo
                archivo = new Archivo(dto);
                em.persist(archivo);
            }
            
            em.flush();
            
            // Retornar sin contenido para optimizar
            ArchivoDto resultado = new ArchivoDto(archivo, false);
            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                "", "guardarArchivo", "Archivo", resultado);
                
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error guardando archivo", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                "Error guardando archivo: " + ex.getMessage(), "guardarArchivo", ex.getMessage());
        }
    }
    
    /**
     * Elimina un archivo por ID
     */
    public Respuesta eliminarArchivo(Long id) {
        try {
            Archivo archivo = em.find(Archivo.class, id);
            if (archivo == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                    "No se encontró el archivo a eliminar", "eliminarArchivo", null);
            }
            
            // Verificar si está siendo usado por alguna sección
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
