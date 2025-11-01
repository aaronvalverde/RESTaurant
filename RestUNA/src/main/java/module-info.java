module cr.ac.una.restuna {
    requires javafx.controls;
    requires java.logging;
    requires javafx.fxml;
    requires java.base;
    requires MaterialFX;
    requires com.jfoenix;
    requires javafx.web;

    opens cr.ac.una.restuna to javafx.fxml;
    opens cr.ac.una.restuna.controller to javafx.fxml;
    opens cr.ac.una.restuna.model to javafx.base;

    exports cr.ac.una.restuna;    
    requires net.sf.jasperreports.core;
}
