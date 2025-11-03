
package cr.ac.una.restuna.util;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class UserRow extends RecursiveTreeObject<UserRow> {

    private final Long idUsuario;
    private final StringProperty username;
    private final StringProperty name;
    private final StringProperty role;
    private final StringProperty roleDisplay; 
    private final StringProperty status;
    private final StringProperty statusDisplay; 

    public UserRow(Long idUsuario, String username, String name, String role, String status) {
        this.idUsuario = idUsuario;
        this.username = new SimpleStringProperty(username);
        this.name = new SimpleStringProperty(name != null ? name : "");
        this.role = new SimpleStringProperty(role);
        
        this.roleDisplay = new SimpleStringProperty(capitalizeRole(role));
        this.status = new SimpleStringProperty(status);
        
        this.statusDisplay = new SimpleStringProperty(
            status != null && status.equals("A") ? "Activo" : "Inactivo"
        );
    }
    
    private String capitalizeRole(String role) {
        if (role == null || role.isEmpty()) return "";
        return role.charAt(0) + role.substring(1).toLowerCase();
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public StringProperty getUsername() {
        return username;
    }
    
    public StringProperty getName() {
        return name;
    }

    public StringProperty getRole() {
        return role;
    }
    
    public StringProperty getRoleDisplay() {
        return roleDisplay;
    }

    public StringProperty getStatus() {
        return status;
    }
    
    public StringProperty getStatusDisplay() {
        return statusDisplay;
    }
}
