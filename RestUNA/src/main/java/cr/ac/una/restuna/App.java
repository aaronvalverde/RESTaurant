package cr.ac.una.restuna;

import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;



public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        new FlowController(stage, null);
        FlowController.getInstance().goView(AppKeys.LOGIN);
        stage.setScene(scene);
        stage.setTitle("RESTaurant");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/cr/ac/una/restuna/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
