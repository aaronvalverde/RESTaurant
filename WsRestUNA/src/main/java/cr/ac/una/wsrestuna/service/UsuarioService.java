package cr.ac.una.wsrestuna.service;

import cr.ac.una.wsrestuna.model.Usuario;
import cr.ac.una.wsrestuna.model.UsuarioDto;
import cr.ac.una.wsrestuna.util.CodigoRespuesta;
import cr.ac.una.wsrestuna.util.Respuesta;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Servicio EJB para la gestión de usuarios
 * Maneja operaciones CRUD y lógica de negocio para usuarios
 */
@Stateless
public class UsuarioService {

    private static final Logger LOGGER = Logger.getLogger(UsuarioService.class.getName());

    @PersistenceContext(unitName = "RestUNA_PU")
    private EntityManager em;

    /**
     * Obtiene todos los usuarios
     */
    public Respuesta obtenerTodos() {
        try {
            TypedQuery<Usuario> query = em.createNamedQuery("Usuario.findAll", Usuario.class);
            List<Usuario> usuarios = query.getResultList();
            List<UsuarioDto> usuariosDto = usuarios.stream()
                    .map(UsuarioDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Usuarios obtenidos correctamente", "", "Usuarios", usuariosDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener todos los usuarios", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener usuarios", e.getMessage());
        }
    }

    /**
     * Obtiene usuarios activos
     */
    public Respuesta obtenerActivos() {
        try {
            TypedQuery<Usuario> query = em.createNamedQuery("Usuario.findActivos", Usuario.class);
            List<Usuario> usuarios = query.getResultList();
            List<UsuarioDto> usuariosDto = usuarios.stream()
                    .map(UsuarioDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Usuarios activos obtenidos correctamente", "", "Usuarios", usuariosDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios activos", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener usuarios activos", e.getMessage());
        }
    }

    /**
     * Obtiene un usuario por ID
     */
    public Respuesta obtenerPorId(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del usuario es requerido", "ID nulo");
            }
            
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Usuario no encontrado", "No existe usuario con ID: " + id);
            }
            
            UsuarioDto usuarioDto = new UsuarioDto(usuario);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Usuario obtenido correctamente", "", "Usuario", usuarioDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuario por ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener usuario", e.getMessage());
        }
    }

    /**
     * Obtiene un usuario por nombre de usuario
     */
    public Respuesta obtenerPorUsuario(String usuario) {
        try {
            if (usuario == null || usuario.trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El nombre de usuario es requerido", "Usuario vacío");
            }
            
            TypedQuery<Usuario> query = em.createNamedQuery("Usuario.findByUsuario", Usuario.class);
            query.setParameter("usuario", usuario);
            Usuario usuarioEntity = query.getSingleResult();
            
            UsuarioDto usuarioDto = new UsuarioDto(usuarioEntity);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Usuario obtenido correctamente", "", "Usuario", usuarioDto);
        } catch (NoResultException e) {
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                    "Usuario no encontrado", "No existe usuario: " + usuario);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuario por nombre: " + usuario, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener usuario", e.getMessage());
        }
    }

    /**
     * Obtiene usuarios por rol
     */
    public Respuesta obtenerPorRol(String rol) {
        try {
            if (rol == null || rol.trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El rol es requerido", "Rol vacío");
            }
            
            TypedQuery<Usuario> query = em.createNamedQuery("Usuario.findByRol", Usuario.class);
            query.setParameter("rol", rol);
            List<Usuario> usuarios = query.getResultList();
            List<UsuarioDto> usuariosDto = usuarios.stream()
                    .map(UsuarioDto::new)
                    .collect(Collectors.toList());
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Usuarios por rol obtenidos correctamente", "", "Usuarios", usuariosDto);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener usuarios por rol: " + rol, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al obtener usuarios por rol", e.getMessage());
        }
    }

    /**
     * Crea un nuevo usuario
     */
    public Respuesta crear(UsuarioDto usuarioDto) {
        try {
            // Validaciones de entrada
            if (usuarioDto == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos del usuario son requeridos", "UsuarioDto nulo");
            }

            if (usuarioDto.getUsuario() == null || usuarioDto.getUsuario().trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El nombre de usuario es requerido", "Usuario vacío");
            }

            if (usuarioDto.getNuevaContrasena() == null || usuarioDto.getNuevaContrasena().trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "La contraseña es requerida", "Contraseña vacía");
            }

            // Validar que no exista el usuario
            if (existeUsuario(usuarioDto.getUsuario())) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Ya existe un usuario con ese nombre", "Usuario duplicado: " + usuarioDto.getUsuario());
            }

            // Crear entidad usando el constructor que llama a actualizar()
            Usuario usuario = new Usuario(usuarioDto);
            
            // Cifrar contraseña
            String contrasenaCifrada = cifrarContrasena(usuarioDto.getNuevaContrasena());
            usuario.setContrasena(contrasenaCifrada);

            // Persistir
            em.persist(usuario);
            em.flush();

            LOGGER.info("Usuario creado exitosamente: " + usuario.getUsuario());
            UsuarioDto resultado = new UsuarioDto(usuario);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Usuario creado correctamente", "", "Usuario", resultado);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear usuario", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al crear usuario", e.getMessage());
        }
    }

    /**
     * Actualiza un usuario existente
     */
    public Respuesta actualizar(UsuarioDto usuarioDto) {
        try {
            // Validaciones de entrada
            if (usuarioDto == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Los datos del usuario son requeridos", "UsuarioDto nulo");
            }

            if (usuarioDto.getIdUsuario() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del usuario es requerido", "ID nulo");
            }

            Usuario usuario = em.find(Usuario.class, usuarioDto.getIdUsuario());
            if (usuario == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Usuario no encontrado", "No existe usuario con ID: " + usuarioDto.getIdUsuario());
            }

            // Validar que no exista otro usuario con el mismo nombre de usuario
            if (usuarioDto.getUsuario() != null && !usuario.getUsuario().equals(usuarioDto.getUsuario()) && 
                existeUsuario(usuarioDto.getUsuario())) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Ya existe otro usuario con ese nombre", "Usuario duplicado: " + usuarioDto.getUsuario());
            }

            // Actualizar campos
            if (usuarioDto.getUsuario() != null) usuario.setUsuario(usuarioDto.getUsuario());
            if (usuarioDto.getRol() != null) usuario.setRol(usuarioDto.getRol());
            if (usuarioDto.getEstado() != null) usuario.setEstado(usuarioDto.getEstado());

            // Actualizar contraseña si se proporciona
            if (usuarioDto.getNuevaContrasena() != null && !usuarioDto.getNuevaContrasena().trim().isEmpty()) {
                String contrasenaCifrada = cifrarContrasena(usuarioDto.getNuevaContrasena());
                usuario.setContrasena(contrasenaCifrada);
            }

            // Actualizar
            usuario = em.merge(usuario);
            em.flush();

            LOGGER.info("Usuario actualizado exitosamente: " + usuario.getUsuario());
            UsuarioDto resultado = new UsuarioDto(usuario);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Usuario actualizado correctamente", "", "Usuario", resultado);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar usuario", e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al actualizar usuario", e.getMessage());
        }
    }

    /**
     * Elimina un usuario (cambio de estado a inactivo)
     */
    public Respuesta eliminar(Long id) {
        try {
            if (id == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El ID del usuario es requerido", "ID nulo");
            }

            Usuario usuario = em.find(Usuario.class, id);
            if (usuario == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Usuario no encontrado", "No existe usuario con ID: " + id);
            }

            usuario.setEstado("I"); // Inactivo
            em.merge(usuario);
            em.flush();

            LOGGER.info("Usuario desactivado exitosamente: " + usuario.getUsuario());
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Usuario eliminado correctamente", "");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario con ID: " + id, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al eliminar usuario", e.getMessage());
        }
    }

    /**
     * Autenticar usuario
     */
    public Respuesta autenticar(String usuario, String contrasena) {
        try {
            if (usuario == null || usuario.trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "El nombre de usuario es requerido", "Usuario vacío");
            }

            if (contrasena == null || contrasena.trim().isEmpty()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "La contraseña es requerida", "Contraseña vacía");
            }

            // Buscar usuario por nombre de usuario
            TypedQuery<Usuario> query = em.createNamedQuery("Usuario.findByUsuario", Usuario.class);
            query.setParameter("usuario", usuario);
            Usuario usuarioEntity;
            
            try {
                usuarioEntity = query.getSingleResult();
            } catch (NoResultException e) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, 
                        "Usuario no encontrado", "No existe usuario: " + usuario);
            }

            // Verificar que el usuario esté activo
            if (!usuarioEntity.isActivo()) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Usuario inactivo", "El usuario " + usuario + " está inactivo");
            }

            // Verificar contraseña
            String contrasenaCifrada = cifrarContrasena(contrasena);
            if (!contrasenaCifrada.equals(usuarioEntity.getContrasena())) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, 
                        "Credenciales incorrectas", "Contraseña incorrecta para: " + usuario);
            }

            // Actualizar último acceso
            usuarioEntity.setFechaUltimoAcceso(LocalDateTime.now());
            em.merge(usuarioEntity);

            LOGGER.info("Usuario autenticado exitosamente: " + usuario);
            UsuarioDto usuarioDto = new UsuarioDto(usuarioEntity);
            
            return new Respuesta(true, CodigoRespuesta.CORRECTO, 
                    "Usuario autenticado correctamente", "", "Usuario", usuarioDto);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al autenticar usuario: " + usuario, e);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, 
                    "Error al autenticar usuario", e.getMessage());
        }
    }

    /**
     * Verifica si existe un usuario con el nombre dado
     */
    private boolean existeUsuario(String usuario) {
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.usuario = :usuario", Long.class);
            query.setParameter("usuario", usuario);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * TODO: Cifra una contraseña usando SHA-256 (DESACTIVADO temporalmente)
     * Por ahora retorna la contraseña en texto plano para pruebas
     */
    private String cifrarContrasena(String contrasena) {
        // TEMPORAL: Sin encriptación por ahora
        return contrasena;
        
        /* TODO: Implementar con JWTokenHelper más adelante
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(contrasena.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar contraseña", e);
        }
        */
    }
}