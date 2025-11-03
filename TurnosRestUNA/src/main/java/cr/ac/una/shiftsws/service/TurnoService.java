package cr.ac.una.shiftsws.service;

import cr.ac.una.shiftsws.model.TurnoDto;
import cr.ac.una.shiftsws.util.Respuesta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class TurnoService implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TurnoService.class.getName());
    private static final String BASE_URL = "http://localhost:8080/WsRestUNA/ws/TurnoController";

    public Respuesta getAllTurnos() {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(BASE_URL + "/turno")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                List<TurnoDto> turnos = response.readEntity(new GenericType<List<TurnoDto>>() {
                });
                return new Respuesta(true, "Turnos obtenidos correctamente", "", "Turnos", turnos);
            } else {
                String mensaje = response.readEntity(String.class);
                return new Respuesta(false, "Error obteniendo turnos", mensaje, "Turnos", null);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error obteniendo todos los turnos", ex);
            return new Respuesta(false, "Error de conexión", ex.getMessage(), "Turnos", null);
        }
    }

    public Respuesta getTurno(Long id) {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(BASE_URL + "/turno/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                TurnoDto turno = response.readEntity(TurnoDto.class);
                return new Respuesta(true, "Turno obtenido correctamente", "", "Turno", turno);
            } else {
                String mensaje = response.readEntity(String.class);
                return new Respuesta(false, "Error obteniendo turno", mensaje, "Turno", null);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error obteniendo turno por ID", ex);
            return new Respuesta(false, "Error de conexión", ex.getMessage(), "Turno", null);
        }
    }

    public Respuesta getTurnosByUsuario(Long usuarioId) {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(BASE_URL + "/turno/usuario/" + usuarioId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                List<TurnoDto> turnos = response.readEntity(new GenericType<List<TurnoDto>>() {
                });
                return new Respuesta(true, "Turnos obtenidos correctamente", "", "Turnos", turnos);
            } else {
                String mensaje = response.readEntity(String.class);
                return new Respuesta(false, "Error obteniendo turnos", mensaje, "Turnos", new ArrayList<>());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error obteniendo turnos por usuario", ex);
            return new Respuesta(false, "Error de conexión", ex.getMessage(), "Turnos", new ArrayList<>());
        }
    }

    public Respuesta getTurnoActivoByUsuario(Long usuarioId) {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(BASE_URL + "/turno/usuario/" + usuarioId + "/activo")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                TurnoDto turno = response.readEntity(TurnoDto.class);
                return new Respuesta(true, "Turno activo obtenido", "", "Turno", turno);
            } else if (response.getStatus() == 404) {
                return new Respuesta(false, "No hay turno activo", "", "Turno", null);
            } else {
                String mensaje = response.readEntity(String.class);
                return new Respuesta(false, "Error obteniendo turno activo", mensaje, "Turno", null);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error obteniendo turno activo", ex);
            return new Respuesta(false, "Error de conexión", ex.getMessage(), "Turno", null);
        }
    }

    public Respuesta iniciarTurno(TurnoDto turnoDto) {
        try {
            Client client = ClientBuilder.newClient();
            Response response = client.target(BASE_URL + "/turno")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(turnoDto));

            if (response.getStatus() == 200) {
                TurnoDto turno = response.readEntity(TurnoDto.class);
                return new Respuesta(true, "Turno iniciado correctamente", "", "Turno", turno);
            } else {
                String mensaje = response.readEntity(String.class);
                return new Respuesta(false, "Error iniciando turno", mensaje, "Turno", null);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error iniciando turno", ex);
            return new Respuesta(false, "Error de conexión", ex.getMessage(), "Turno", null);
        }
    }

    public Respuesta finalizarTurno(Long id) {
        Client client = null;
        try {
            client = ClientBuilder.newClient();
            Response response = client.target(BASE_URL + "/turno/" + id)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json("{}"));

            LOGGER.log(Level.INFO, "Finalizando turno - ID: {0}, Status: {1}",
                    new Object[]{id, response.getStatus()});

            if (response.getStatus() == 200) {
                TurnoDto turno = response.readEntity(TurnoDto.class);
                return new Respuesta(true, "Turno finalizado correctamente", "", "Turno", turno);
            } else {
                String mensaje = response.readEntity(String.class);
                LOGGER.log(Level.SEVERE, "Error del servidor (status {0}): {1}",
                        new Object[]{response.getStatus(), mensaje});
                return new Respuesta(false, "Error finalizando turno", mensaje, "Turno", null);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Excepción finalizando turno", ex);
            return new Respuesta(false, "Error de conexión", ex.getMessage(), "Turno", null);
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error cerrando cliente", e);
                }
            }
        }
    }
}