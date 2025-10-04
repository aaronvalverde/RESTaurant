/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.restuna.controller;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author fonse
 */
public class Sections extends RecursiveTreeObject<Sections> {

    private final StringProperty name;
    private final StringProperty type;
    private final BooleanProperty tax;
    private final StringProperty image;

    public Sections(String name, String type, boolean tax, String image) {
        this.name = new SimpleStringProperty(name);
        this.type = new SimpleStringProperty(type);
        this.tax = new SimpleBooleanProperty(tax);
        this.image = new SimpleStringProperty(image);
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getType() {
        return type;
    }

    public BooleanProperty getTax() {
        return tax;
    }

    public StringProperty getImage() {
        return image;
    }

    
}
