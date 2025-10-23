/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.controller;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Representa una fila de usuario en la tabla de gestión de usuarios
 * Estructura simplificada según el modelo de BD actual
 */
public class UserRow extends RecursiveTreeObject<UserRow> {

    private final StringProperty username;
    private final StringProperty name;
    private final StringProperty role;
    private final StringProperty roleDisplay; // Para mostrar rol capitalizado
    private final StringProperty status;
    private final StringProperty statusDisplay; // Para mostrar "Activo"/"Inactivo"

    public UserRow(String username, String name, String role, String status) {
        this.username = new SimpleStringProperty(username);
        this.name = new SimpleStringProperty(name != null ? name : "");
        this.role = new SimpleStringProperty(role);
        // Convertir rol a formato capitalizado (solo primera letra en mayúscula)
        this.roleDisplay = new SimpleStringProperty(capitalizeRole(role));
        this.status = new SimpleStringProperty(status);
        // Convertir A/I a texto legible
        this.statusDisplay = new SimpleStringProperty(
            status != null && status.equals("A") ? "Activo" : "Inactivo"
        );
    }
    
    private String capitalizeRole(String role) {
        if (role == null || role.isEmpty()) return "";
        return role.charAt(0) + role.substring(1).toLowerCase();
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
