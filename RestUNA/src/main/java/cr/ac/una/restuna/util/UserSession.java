package cr.ac.una.restuna.util;

import cr.ac.una.restuna.model.UsuarioDto;


public class UserSession {
    
    private static UserSession instance;
    private UsuarioDto currentUser;
    
    private UserSession() {
        
    }
    
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    public void setCurrentUser(UsuarioDto user) {
        this.currentUser = user;
    }
    
    public UsuarioDto getCurrentUser() {
        return currentUser;
    }
    
    public void clearSession() {
        this.currentUser = null;
    }
    
    public boolean isAuthenticated() {
        return currentUser != null;
    }
    
    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getUsuario() : null;
    }
    
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRol() : null;
    }
    
    
    public boolean isAdministrador() {
        return currentUser != null && "ADMINISTRADOR".equals(currentUser.getRol());
    }
    
    public boolean isCajero() {
        return currentUser != null && "CAJERO".equals(currentUser.getRol());
    }
    
    public boolean isSalonero() {
        return currentUser != null && "SALONERO".equals(currentUser.getRol());
    }
    
    
    public boolean canAccessSalones() {
        return isAuthenticated(); 
    }
    
    public boolean canAccessOrdenes() {
        return isCajero() || isAdministrador(); 
    }
    
    public boolean canAccessFacturacion() {
        return isCajero() || isAdministrador(); 
    }
    
    public boolean canAccessCierreCaja() {
        return isCajero() || isAdministrador(); 
    }
    
    public boolean canAccessMantenimientos() {
        return isAdministrador(); 
    }
    
    public boolean canAccessReportes() {
        return isAdministrador(); 
    }
    
    public boolean canAccessConfiguracion() {
        return isAdministrador(); 
    }
}