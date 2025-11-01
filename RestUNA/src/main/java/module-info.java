module cr.ac.una.restuna {
    // JavaFX modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    
    // UI Libraries
    requires MaterialFX;
    requires com.jfoenix;
    
    // Java modules
    requires java.logging;
    requires java.base;
    requires java.sql;
    
    // Reporting
    requires net.sf.jasperreports.core;

    // Opens for JavaFX reflection
    opens cr.ac.una.restuna to javafx.fxml;
    opens cr.ac.una.restuna.controller to javafx.fxml;
    opens cr.ac.una.restuna.model to javafx.base;

    // Exports
    exports cr.ac.una.restuna;
}
