module cr.ac.una.restuna {
    requires javafx.controls;
    requires javafx.fxml;

    opens cr.ac.una.restuna to javafx.fxml;
    exports cr.ac.una.restuna;
}
