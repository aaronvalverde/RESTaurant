package cr.ac.una.shiftsws.controller;

import cr.ac.una.shiftsws.model.UsuarioDto;
import cr.ac.una.shiftsws.service.UsuarioService;
import cr.ac.una.shiftsws.util.Respuesta;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.annotation.SessionMap;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @Inject
    @SessionMap
    private Map<String, Object> sessionMap;

    private String username;
    private String password;
    private UsuarioService usuarioService;

    public LoginController() {
    }

    @PostConstruct
    public void initialize() {
        if (usuarioService == null) {
            usuarioService = new UsuarioService();
        }
    }

    private String login(String user, String password) {
        if (user == null || password == null) {
            addErrorMessage("Error de autenticación", "Usuario o contraseña no válidos");
            return null;
        }

        try {
            Respuesta respuesta = usuarioService.getUsuario(user, password);

            if (respuesta == null) {
                addErrorMessage("Error del sistema", "No se pudo conectar con el servicio de autenticación");
                return null;
            }

            if (respuesta.getEstado()) {
                UsuarioDto usuarioDto = (UsuarioDto) respuesta.getResultado("Usuario");
                if (usuarioDto == null) {
                    addErrorMessage("Error del sistema", "Error al recuperar los datos del usuario");
                    return null;
                }
                sessionMap.put("usuario", usuarioDto);
                return validateRol(usuarioDto);
            } else {
                addErrorMessage("Error de autenticación", respuesta.getMensaje());
                return null;
            }
        } catch (Exception e) {
            addErrorMessage("Error del sistema", "Error inesperado: " + e.getMessage());
            return null;
        }
    }

    public String loginOnAction() {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            addErrorMessage("Error de autenticación", "El nombre de usuario y la contraseña son obligatorios.");
            return null;
        }

        String newPath = login(username, password);
        if (newPath != null) {
            clearFields();
        }
        return newPath;
    }

    private String validateRol(UsuarioDto usuarioDto) {
        if(usuarioDto.getRol().equals("ADMINISTRADOR")) {
            return "shift.xhtml?faces-redirect=true";
        } else {
            return "shift.xhtml?faces-redirect=true";
        }
    }

    private void addErrorMessage(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UsuarioDto getLoggedUser() {
        return (UsuarioDto) sessionMap.get("usuario");
    }

    private void clearFields() {
        username = null;
        password = null;
    }
}