package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.Cliente;
import cr.ac.una.wsrestuna.model.ClienteDto;
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
 * Servicio EJB para la gestión de clientes
 * 
 * @author Kendall Fonseca
 * @author Kaleb Alfaro
 */
@Stateless
@LocalBean
public class ClienteService {

    private static final Logger LOGGER = Logger.getLogger(ClienteService.class.getName());

    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;

    /**
     * Obtiene todos los clientes
     */
    public Respuesta obtenerTodos() {
        try {
            TypedQuery<Cliente> query = em.createNamedQuery("Cliente.findAll", Cliente.class);
            List<Cliente> clientes = query.getResultList();
            List<ClienteDto> clientesDto = clientes.stream()
                    .map(ClienteDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Clientes obtenidos correctamente", "", "Clientes", clientesDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todos los clientes", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener clientes", e.getMessage());
        }
    }

    /**
     * Obtiene un cliente por ID
     */
    public Respuesta obtenerPorId(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del cliente es requerido", "ID nulo");
            }
            
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Cliente no encontrado", "No existe cliente con ID: " + id);
            }
            
            ClienteDto clienteDto = new ClienteDto(cliente);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cliente obtenido correctamente", "", "Cliente", clienteDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener cliente por ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener cliente", e.getMessage());
        }
    }

    /**
     * Obtiene un cliente por cédula
     */
    public Respuesta obtenerPorCedula(String cedula) {
        try {
            if (cedula == null || cedula.trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "La cédula es requerida", "Cédula vacía");
            }
            
            TypedQuery<Cliente> query = em.createNamedQuery("Cliente.findByCedula", Cliente.class);
            query.setParameter("cedula", cedula);
            Cliente cliente = query.getSingleResult();
            
            ClienteDto clienteDto = new ClienteDto(cliente);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cliente obtenido correctamente", "", "Cliente", clienteDto);
        } catch (NoResultException e) {
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                    "Cliente no encontrado", "No existe cliente con cédula: " + cedula);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener cliente por cédula: " + cedula, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener cliente", e.getMessage());
        }
    }

    /**
     * Obtiene un cliente por correo
     */
    public Respuesta obtenerPorCorreo(String correo) {
        try {
            if (correo == null || correo.trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El correo es requerido", "Correo vacío");
            }
            
            TypedQuery<Cliente> query = em.createNamedQuery("Cliente.findByCorreo", Cliente.class);
            query.setParameter("correo", correo);
            Cliente cliente = query.getSingleResult();
            
            ClienteDto clienteDto = new ClienteDto(cliente);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cliente obtenido correctamente", "", "Cliente", clienteDto);
        } catch (NoResultException e) {
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                    "Cliente no encontrado", "No existe cliente con correo: " + correo);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener cliente por correo: " + correo, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener cliente", e.getMessage());
        }
    }

    /**
     * Crea un nuevo cliente
     */
    public Respuesta crear(ClienteDto clienteDto) {
        try {
            if (clienteDto == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos del cliente son requeridos", "ClienteDto nulo");
            }

            // Validar cédula única
            try {
                TypedQuery<Cliente> query = em.createNamedQuery("Cliente.findByCedula", Cliente.class);
                query.setParameter("cedula", clienteDto.getCedula());
                query.getSingleResult();
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Ya existe un cliente con esta cédula", "Cédula duplicada: " + clienteDto.getCedula());
            } catch (NoResultException e) {
                // No existe, podemos continuar
            }

            Cliente cliente = new Cliente(clienteDto);
            em.persist(cliente);
            em.flush();
            
            clienteDto = new ClienteDto(cliente);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cliente creado correctamente", "", "Cliente", clienteDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear cliente", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al crear cliente", e.getMessage());
        }
    }

    /**
     * Actualiza un cliente existente
     */
    public Respuesta actualizar(ClienteDto clienteDto) {
        try {
            if (clienteDto == null || clienteDto.getIdCliente() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos del cliente y su ID son requeridos", "ClienteDto inválido");
            }

            Cliente cliente = em.find(Cliente.class, clienteDto.getIdCliente());
            if (cliente == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Cliente no encontrado", "No existe cliente con ID: " + clienteDto.getIdCliente());
            }

            // Validar cédula única si cambió
            if (!cliente.getCedula().equals(clienteDto.getCedula())) {
                try {
                    TypedQuery<Cliente> query = em.createNamedQuery("Cliente.findByCedula", Cliente.class);
                    query.setParameter("cedula", clienteDto.getCedula());
                    Cliente existente = query.getSingleResult();
                    if (!existente.getIdCliente().equals(cliente.getIdCliente())) {
                        return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                                "Ya existe otro cliente con esta cédula", "Cédula duplicada: " + clienteDto.getCedula());
                    }
                } catch (NoResultException e) {
                    // No existe, podemos continuar
                }
            }

            cliente.actualizarDesdeDto(clienteDto);
            em.merge(cliente);
            em.flush();
            
            clienteDto = new ClienteDto(cliente);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cliente actualizado correctamente", "", "Cliente", clienteDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar cliente", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al actualizar cliente", e.getMessage());
        }
    }

    /**
     * Elimina un cliente por ID
     */
    public Respuesta eliminar(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del cliente es requerido", "ID nulo");
            }
            
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Cliente no encontrado", "No existe cliente con ID: " + id);
            }
            
            em.remove(cliente);
            em.flush();
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Cliente eliminado correctamente", "");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar cliente con ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al eliminar cliente", e.getMessage());
        }
    }
}
