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
    private final StringProperty status;

    public UserRow(String username, String name, String role, String status) {
        this.username = new SimpleStringProperty(username);
        this.name = new SimpleStringProperty(name != null ? name : "");
        this.role = new SimpleStringProperty(role);
        this.status = new SimpleStringProperty(status);
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

    public StringProperty getStatus() {
        return status;
    }
}
