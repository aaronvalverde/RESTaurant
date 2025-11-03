package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.Turno;
import cr.ac.una.wsrestuna.model.TurnoDto;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class TurnoService {

    private static final Logger LOG = Logger.getLogger(TurnoService.class.getName());

    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;

    public Respuesta iniciarTurno(TurnoDto turnoDto) {
        try {
            
            Turno turnoActivo = obtenerTurnoActivoByUsuario(turnoDto.getUsuarioId());
            if (turnoActivo != null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                        "El usuario ya tiene un turno activo", "iniciarTurno", null);
            }

            Turno turno = new Turno();
            turno.setUsuarioId(turnoDto.getUsuarioId());
            turno.setFechaInicio(turnoDto.getFechaInicio());
            turno.setEstado("A");

            em.persist(turno);
            em.flush();

            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                    "", "iniciarTurno", new TurnoDto(turno));

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error iniciando turno.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error iniciando turno", "iniciarTurno", ex.getMessage());
        }
    }

    public Respuesta finalizarTurno(Long id) {
        LOG.log(Level.INFO, "===== INICIANDO finalizarTurno para ID: {0} =====", id);

        try {
            Turno turno = em.find(Turno.class, id);
            LOG.log(Level.INFO, "Turno encontrado: {0}", turno);

            if (turno == null) {
                LOG.log(Level.WARNING, "Turno NULL para ID: {0}", id);
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "Turno no encontrado", "finalizarTurno", null);
            }

            LOG.log(Level.INFO, "Estado actual del turno: {0}", turno.getEstado());
            LOG.log(Level.INFO, "Fecha inicio: {0}", turno.getFechaInicio());
            LOG.log(Level.INFO, "Fecha final ANTES: {0}", turno.getFechaFinal());
            LOG.log(Level.INFO, "Duración ANTES: {0}", turno.getDuracion());

            if (!"A".equals(turno.getEstado())) {
                LOG.log(Level.WARNING, "Turno ya finalizado con estado: {0}", turno.getEstado());
                return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                        "El turno ya está finalizado", "finalizarTurno", null);
            }

            
            LocalDateTime ahora = LocalDateTime.now();
            LOG.log(Level.INFO, "Estableciendo fecha final a: {0}", ahora);
            turno.setFechaFinal(ahora);

            LOG.log(Level.INFO, "Estableciendo estado a: F");
            turno.setEstado("F");

            
            if (turno.getFechaInicio() != null) {
                long minutos = Duration.between(turno.getFechaInicio(), ahora).toMinutes();
                LOG.log(Level.INFO, "Calculando duración: {0} minutos", minutos);
                turno.setDuracion((int) minutos);
            }

            LOG.log(Level.INFO, "Fecha final DESPUÉS de set: {0}", turno.getFechaFinal());
            LOG.log(Level.INFO, "Duración DESPUÉS de set: {0}", turno.getDuracion());
            LOG.log(Level.INFO, "Estado DESPUÉS de set: {0}", turno.getEstado());

            LOG.log(Level.INFO, "Llamando a em.merge()...");
            Turno merged = em.merge(turno);

            LOG.log(Level.INFO, "Llamando a em.flush()...");
            em.flush();

            LOG.log(Level.INFO, "Turno después de merge: {0}", merged);
            LOG.log(Level.INFO, "Fecha final después de flush: {0}", merged.getFechaFinal());
            LOG.log(Level.INFO, "Duración después de flush: {0}", merged.getDuracion());

            TurnoDto dto = new TurnoDto(merged);
            LOG.log(Level.INFO, "TurnoDto creado: {0}", dto);
            LOG.log(Level.INFO, "===== FIN finalizarTurno - ÉXITO =====");

            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                    "Turno finalizado", "finalizarTurno", "Turno", dto);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "===== ERROR en finalizarTurno =====", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error finalizando turno", "finalizarTurno", ex.getMessage());
        }
    }

    public Respuesta getTurno(Long id) {
        try {
            Turno turno = em.find(Turno.class, id);

            if (turno == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "Turno no encontrado", "getTurno", null);
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                    "", "getTurno", new TurnoDto(turno));

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo turno.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo turno", "getTurno", ex.getMessage());
        }
    }

    public Respuesta getTurnosByUsuario(Long usuarioId) {
        try {
            Query query = em.createNamedQuery("Turno.findByUser", Turno.class);
            query.setParameter("usuario", usuarioId);

            List<Turno> turnos = query.getResultList();
            List<TurnoDto> turnosDto = new ArrayList<>();

            for (Turno turno : turnos) {
                turnosDto.add(new TurnoDto(turno));
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                    "", "getTurnosByUsuario", turnosDto);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo turnos por usuario.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo turnos", "getTurnosByUsuario", ex.getMessage());
        }
    }

    public Respuesta getTurnoActivoByUsuario(Long usuarioId) {
        try {
            Turno turno = obtenerTurnoActivoByUsuario(usuarioId);

            if (turno == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                        "No hay turno activo", "getTurnoActivoByUsuario", null);
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                    "", "getTurnoActivoByUsuario", new TurnoDto(turno));

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo turno activo.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo turno activo", "getTurnoActivoByUsuario", ex.getMessage());
        }
    }

    public Respuesta getAllTurnos() {
        try {
            Query query = em.createNamedQuery("Turno.findAll", Turno.class);
            List<Turno> turnos = query.getResultList();
            List<TurnoDto> turnosDto = new ArrayList<>();

            for (Turno turno : turnos) {
                turnosDto.add(new TurnoDto(turno));
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO,
                    "", "getAllTurnos", turnosDto);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error obteniendo todos los turnos.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                    "Error obteniendo turnos", "getAllTurnos", ex.getMessage());
        }
    }

    private Turno obtenerTurnoActivoByUsuario(Long usuarioId) {
        try {
            Query query = em.createQuery(
                    "SELECT t FROM Turno t WHERE t.usuarioId = :usuario AND t.estado = 'A'",
                    Turno.class);
            query.setParameter("usuario", usuarioId);
            return (Turno) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
