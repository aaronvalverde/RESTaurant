/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.controller;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author fonse
 */
// esta clase es temporal hasta que se agrega la db 
public class UserRow extends RecursiveTreeObject<UserRow> {

    private final StringProperty name;
    private final StringProperty username;
    private final StringProperty email;
    private final StringProperty role;
    private final StringProperty status;

    public UserRow(String name, String username, String email, String role, String status) {
        this.name = new SimpleStringProperty(name);
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email);
        this.role = new SimpleStringProperty(role);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getUsername() {
        return username;
    }

    public StringProperty getEmail() {
        return email;
    }

    public StringProperty getRole() {
        return role;
    }

    public StringProperty getStatus() {
        return status;
    }

}
