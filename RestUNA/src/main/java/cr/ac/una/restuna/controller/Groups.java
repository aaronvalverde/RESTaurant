/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.controller;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author fonse
 */
public class Groups extends RecursiveTreeObject<Groups> {
    
    private final SimpleStringProperty quickAccess;
    private final SimpleStringProperty idGroup;
    private final SimpleStringProperty description;
    private final SimpleStringProperty nameGroup;
    private final SimpleStringProperty status;

    public Groups(String quickAccess, String idGroup, String description, String nameGroup, String status) {
        this.quickAccess = new SimpleStringProperty(quickAccess);
        this.idGroup = new SimpleStringProperty(idGroup);
        this.description = new SimpleStringProperty(description);
        this.nameGroup = new SimpleStringProperty(nameGroup);
        this.status = new SimpleStringProperty(status);
    }

    public SimpleStringProperty getQuickAccess() {
        return quickAccess;
    }

    public SimpleStringProperty getIdGroup() {
        return idGroup;
    }

    public SimpleStringProperty getDescription() {
        return description;
    }

    public SimpleStringProperty getNameGroup() {
        return nameGroup;
    }

    public SimpleStringProperty getStatus() {
        return status;
    }
    
    
    
}
