package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.Parametro;
import cr.ac.una.wsrestuna.model.ParametroDto;
import cr.ac.una.wsrestuna.model.Usuario;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servicio EJB para la gestión de parámetros de configuración
 * Maneja operaciones CRUD y lógica de negocio para parámetros por usuario
 */
@Stateless
@LocalBean
public class ParametroService {

    private static final Logger LOGGER = Logger.getLogger(ParametroService.class.getName());

    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;

    /**
     * Obtiene todos los parámetros de un usuario
     */
    public Respuesta getParametrosPorUsuario(Long idUsuario) {
        try {
            if (idUsuario == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del usuario es requerido", "getParametrosPorUsuario: ID nulo");
            }

            TypedQuery<Parametro> query = em.createNamedQuery("Parametro.findByUsuario", Parametro.class);
            query.setParameter("idUsuario", idUsuario);
            List<Parametro> parametros = query.getResultList();
            
            List<ParametroDto> parametrosDto = parametros.stream()
                    .map(ParametroDto::new)
                    .collect(Collectors.toList());

            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "", "", "Parametros", parametrosDto);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener parámetros del usuario: " + idUsuario, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Ocurrió un error al consultar los parámetros.", 
                    "getParametrosPorUsuario " + e.getMessage());
        }
    }

    /**
     * Obtiene un parámetro específico de un usuario por clave
     */
    public Respuesta getParametroPorUsuarioYClave(Long idUsuario, String clave) {
        try {
            if (idUsuario == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del usuario es requerido", "getParametroPorUsuarioYClave: ID nulo");
            }

            if (clave == null || clave.trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "La clave del parámetro es requerida", "getParametroPorUsuarioYClave: Clave vacía");
            }

            TypedQuery<Parametro> query = em.createNamedQuery("Parametro.findByUsuarioAndClave", Parametro.class);
            query.setParameter("idUsuario", idUsuario);
            query.setParameter("clave", clave);
            
            Parametro parametro = query.getSingleResult();
            ParametroDto parametroDto = new ParametroDto(parametro);

            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "", "", "Parametro", parametroDto);

        } catch (NoResultException e) {
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                    "No existe un parámetro con la clave especificada.", 
                    "getParametroPorUsuarioYClave NoResultException");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener parámetro: " + clave + " del usuario: " + idUsuario, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Ocurrió un error al consultar el parámetro.", 
                    "getParametroPorUsuarioYClave " + e.getMessage());
        }
    }

    /**
     * Guarda un parámetro (crear o actualizar)
     */
    public Respuesta guardarParametro(ParametroDto parametroDto) {
        try {
            if (parametroDto == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos del parámetro son requeridos", "guardarParametro: ParametroDto nulo");
            }

            if (parametroDto.getIdUsuario() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del usuario es requerido", "guardarParametro: ID usuario nulo");
            }

            if (parametroDto.getClave() == null || parametroDto.getClave().trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "La clave del parámetro es requerida", "guardarParametro: Clave vacía");
            }

            // Verificar que el usuario existe
            Usuario usuario = em.find(Usuario.class, parametroDto.getIdUsuario());
            if (usuario == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "No se encontró el usuario especificado", "guardarParametro: Usuario no existe");
            }

            Parametro parametro;
            
            if (parametroDto.getIdParametro() != null && parametroDto.getIdParametro() > 0) {
                // Actualizar parámetro existente
                parametro = em.find(Parametro.class, parametroDto.getIdParametro());
                if (parametro == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                            "No se encontró el parámetro a modificar.", 
                            "guardarParametro NoResultException");
                }
                parametro.actualizar(parametroDto);
                parametro = em.merge(parametro);
            } else {
                // Crear nuevo parámetro
                parametro = new Parametro(parametroDto);
                parametro.setUsuario(usuario);
                em.persist(parametro);
            }
            
            em.flush();
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "", "", "Parametro", new ParametroDto(parametro));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar parámetro", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Ocurrió un error al guardar el parámetro.", 
                    "guardarParametro " + e.getMessage());
        }
    }

    /**
     * Guarda múltiples parámetros (batch)
     */
    public Respuesta guardarParametros(List<ParametroDto> parametrosDto) {
        try {
            if (parametrosDto == null || parametrosDto.isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "La lista de parámetros es requerida", "guardarParametros: Lista vacía");
            }

            // Validar que todos los parámetros tienen el mismo usuario
            Long idUsuario = parametrosDto.get(0).getIdUsuario();
            if (idUsuario == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del usuario es requerido", "guardarParametros: ID usuario nulo");
            }

            // Verificar que el usuario existe
            Usuario usuario = em.find(Usuario.class, idUsuario);
            if (usuario == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "No se encontró el usuario especificado", "guardarParametros: Usuario no existe");
            }

            // Procesar cada parámetro
            for (ParametroDto parametroDto : parametrosDto) {
                if (!idUsuario.equals(parametroDto.getIdUsuario())) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                            "Todos los parámetros deben pertenecer al mismo usuario", 
                            "guardarParametros: Usuario inconsistente");
                }

                // Buscar si ya existe el parámetro por clave y usuario
                try {
                    TypedQuery<Parametro> query = em.createNamedQuery("Parametro.findByUsuarioAndClave", Parametro.class);
                    query.setParameter("idUsuario", idUsuario);
                    query.setParameter("clave", parametroDto.getClave());
                    Parametro parametroExistente = query.getSingleResult();
                    
                    // Actualizar existente
                    parametroExistente.actualizar(parametroDto);
                    em.merge(parametroExistente);
                    
                } catch (NoResultException e) {
                    // Crear nuevo
                    Parametro nuevoParametro = new Parametro(parametroDto);
                    nuevoParametro.setUsuario(usuario);
                    em.persist(nuevoParametro);
                }
            }
            
            em.flush();
            
            // Retornar todos los parámetros actualizados del usuario
            return getParametrosPorUsuario(idUsuario);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar parámetros", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Ocurrió un error al guardar los parámetros.", 
                    "guardarParametros " + e.getMessage());
        }
    }

    /**
     * Elimina un parámetro
     */
    public Respuesta eliminarParametro(Long id) {
        try {
            if (id == null || id <= 0) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Debe especificar el parámetro a eliminar.", 
                        "eliminarParametro: ID inválido");
            }

            Parametro parametro = em.find(Parametro.class, id);
            if (parametro == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "No se encontró el parámetro a eliminar.", 
                        "eliminarParametro NoResultException");
            }

            em.remove(parametro);
            em.flush();
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar parámetro", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Ocurrió un error al eliminar el parámetro.", 
                    "eliminarParametro " + e.getMessage());
        }
    }
}
