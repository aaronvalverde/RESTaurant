module cr.ac.una.restuna {
    requires javafx.controls;
    requires java.logging;
    requires javafx.fxml;
    requires java.base;
   


    opens cr.ac.una.restuna to javafx.fxml;
    opens cr.ac.una.restuna.controller to javafx.fxml;
    exports cr.ac.una.restuna;
    requires MaterialFX;
    requires javafx.graphicsEmpty;
}
