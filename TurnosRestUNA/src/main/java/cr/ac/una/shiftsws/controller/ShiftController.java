package cr.ac.una.shiftsws.controller;

import cr.ac.una.shiftsws.model.TurnoDto;
import cr.ac.una.shiftsws.service.TurnoService;
import cr.ac.una.shiftsws.util.Respuesta;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;

@Named("shiftController")
@SessionScoped
public class ShiftController implements Serializable {

    private static final long serialVersionUID = 1L;

    private final TurnoService turnoService = new TurnoService();
    private TurnoDto turnoActual;
    private boolean turnoActivo;

    @Inject
    private LoginController loginController;

    @PostConstruct
    public void initialize() {
        try {
            if (loginController == null || loginController.getLoggedUser() == null) {
                turnoActivo = false;
                return;
            }
            Respuesta r = turnoService.getTurnoActivoByUsuario(loginController.getLoggedUser().getIdUsuario());
            if (r != null && Boolean.TRUE.equals(r.getEstado())) {
                turnoActual = (TurnoDto) r.getResultado("Turno");
                turnoActivo = turnoActual != null && Boolean.TRUE.equals(turnoActual.isActivo());
            } else {
                turnoActivo = false;
            }
        } catch (Exception ex) {
            turnoActivo = false;
        }
    }

    public void toggleTurno() {
        try {
            if (turnoActivo && turnoActual != null) {
                Respuesta respuesta = turnoService.finalizarTurno(turnoActual.getId());
                if (respuesta != null && Boolean.TRUE.equals(respuesta.getEstado())) {
                    turnoActual = (TurnoDto) respuesta.getResultado("Turno");
                    turnoActivo = false;
                } else {
                    initialize();
                }
            } else {
                TurnoDto nuevo = new TurnoDto();
                nuevo.setUsuarioId(loginController.getLoggedUser().getIdUsuario());
                nuevo.setFechaInicio(LocalDateTime.now());
                Respuesta respuesta = turnoService.iniciarTurno(nuevo);
                if (respuesta != null && Boolean.TRUE.equals(respuesta.getEstado())) {
                    turnoActual = (TurnoDto) respuesta.getResultado("Turno");
                    turnoActivo = turnoActual != null;
                } else {
                    initialize();
                }
            }
        } catch (Exception ex) {
            initialize();
        }
    }

    public boolean isTurnoActivo() {
        return turnoActivo;
    }

    public void setTurnoActivo(boolean turnoActivo) {
        this.turnoActivo = turnoActivo;
    }

    public void actualizarTiempo() {
        // lógica aquí
    }
}
