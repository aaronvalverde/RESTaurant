package cr.ac.una.restuna.util;

import cr.ac.una.restuna.model.UsuarioDto;

/**
 * Clase para manejar la sesión del usuario actual
 * Singleton para mantener información del usuario autenticado
 */
public class UserSession {
    
    private static UserSession instance;
    private UsuarioDto currentUser;
    
    private UserSession() {
        // Constructor privado para patrón Singleton
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
    
    // Métodos de conveniencia para verificar roles
    public boolean isAdministrador() {
        return currentUser != null && "ADMINISTRADOR".equals(currentUser.getRol());
    }
    
    public boolean isCajero() {
        return currentUser != null && "CAJERO".equals(currentUser.getRol());
    }
    
    public boolean isSalonero() {
        return currentUser != null && "SALONERO".equals(currentUser.getRol());
    }
    
    // Métodos para verificar permisos específicos
    public boolean canAccessSalones() {
        return isAuthenticated(); // Todos los roles pueden acceder
    }
    
    public boolean canAccessOrdenes() {
        return isCajero() || isAdministrador(); // Solo cajeros y admins
    }
    
    public boolean canAccessFacturacion() {
        return isCajero() || isAdministrador(); // Solo cajeros y admins
    }
    
    public boolean canAccessCierreCaja() {
        return isCajero() || isAdministrador(); // Solo cajeros y admins
    }
    
    public boolean canAccessMantenimientos() {
        return isAdministrador(); // Solo administradores
    }
    
    public boolean canAccessReportes() {
        return isAdministrador(); // Solo administradores
    }
    
    public boolean canAccessConfiguracion() {
        return isAdministrador(); // Solo administradores
    }
}