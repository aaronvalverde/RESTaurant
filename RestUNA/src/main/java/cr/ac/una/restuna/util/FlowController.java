package cr.ac.una.restuna.util;

import cr.ac.una.restuna.controller.Controller;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlowController {

    private static FlowController instance;
    private final Stage mainStage;
    private ResourceBundle language;
    private final Map<String, FXMLLoader> loaders = new HashMap<>();
    private static final Logger logger = Logger.getLogger(FlowController.class.getSimpleName());
    private BorderPane contentArea;

    public FlowController(Stage mainStage, ResourceBundle language) {
        this.mainStage = mainStage;
        this.language = language;
        instance = this;
    }

    public static FlowController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FlowController has not been initialized");
        }
        return instance;
    }

    public void setLanguage(ResourceBundle language) {
        this.language = language;
        clearCache();
    }

    public void setContentArea(BorderPane contentArea) {
        this.contentArea = contentArea;
    }

    private FXMLLoader loadLoader(String name) throws IOException {
        synchronized (loaders) {
            FXMLLoader loader = loaders.get(name);
            if (loader != null) {
                Parent cachedRoot = loader.getRoot();
                if (cachedRoot != null && cachedRoot.getScene() != null) {
                    loaders.remove(name);
                    loader = null;
                }
            }

            if (loader == null) {
                try {
                    loader = new FXMLLoader();
                    URL resource = getClass().getResource("/cr/ac/una/restuna/view/" + name + ".fxml");

                    if (resource == null) {
                        throw new IOException("FXML file not found: " + name + ".fxml");
                    }

                    loader.setLocation(resource);
                    loader.setResources(language);
                    loader.load();
                    loaders.put(name, loader);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to load FXML: " + name, e);
                    throw e;
                }
            }
            return loader;
        }
    }

    public void goView(String viewName) {
        goView(viewName, null);
    }

    public void goView(String viewName, String action) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = loadLoader(viewName);
                if (loader == null) {
                    logger.log(Level.SEVERE, "Loader is null for view: " + viewName);
                    return;
                }

                Controller controller = loader.getController();
                if (controller == null) {
                    logger.log(Level.SEVERE, "Controller is null for view: " + viewName);
                    return;
                }

                controller.setStage(mainStage);

                if (action != null && !action.isEmpty()) {
                    controller.initialize();
                }

                Parent newContent = loader.getRoot();

                BorderPane currentContentArea = contentArea;
                if (currentContentArea == null) {
                    Scene currentScene = mainStage.getScene();
                    if (currentScene != null) {
                        currentContentArea = findContentArea(currentScene.getRoot());
                    }
                }

                if (currentContentArea != null) {
                    currentContentArea.setCenter(newContent);
                } else {
                    logger.log(Level.WARNING, "No BorderPane found to update view: " + viewName);
                    // Fallback to creating a new scene
                    Scene scene = new Scene(newContent);
                    MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
                    mainStage.setScene(scene);
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading view: " + viewName, e);
            }
        });
    }

    public void goMain(String viewName) {
        goMain(viewName, null);
    }

    public void goMain(String viewName, String action) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = loadLoader(viewName);
                if (loader == null) {
                    logger.log(Level.SEVERE, "Loader is null for main view: " + viewName);
                    return;
                }

                Controller controller = loader.getController();
                if (controller == null) {
                    logger.log(Level.SEVERE, "Controller is null for main view: " + viewName);
                    return;
                }

                controller.setStage(mainStage);

                if (action != null && !action.isEmpty()) {
                    controller.initialize();
                }

                Parent root = loader.getRoot();
                Scene scene = new Scene(root);
                MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
                mainStage.setScene(scene);

                // Clear contentArea reference since we're in main view
                this.contentArea = null;

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading main view: " + viewName, e);
            }
        });
    }

    private BorderPane findContentArea(Parent root) {
        return findNodeById(root, "contentArea", BorderPane.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T findNodeById(Parent parent, String id, Class<T> type) {
        if (parent.getId() != null && parent.getId().equals(id) && type.isInstance(parent)) {
            return (T) parent;
        }

        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child.getId() != null && child.getId().equals(id) && type.isInstance(child)) {
                return (T) child;
            }
            if (child instanceof Parent) {
                T result = findNodeById((Parent) child, id, type);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public void goViewInWindowModal(String viewName, Stage parentStage, boolean resizable) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = loadLoader(viewName);
                Controller controller = loader.getController();

                Stage stage = new Stage();
                stage.setTitle(controller.getNombreVista());
                stage.setResizable(resizable);
                stage.setOnHidden((WindowEvent event) -> {
                    controller.getStage().getScene().setRoot(new VBox());
                    controller.setStage(null);
                });

                controller.setStage(stage);

                Scene scene = new Scene(loader.getRoot());
                MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
                stage.setScene(scene);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(parentStage);
                stage.centerOnScreen();
                stage.showAndWait();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error opening modal window view: " + viewName, e);
            }
        });
    }

    public Controller getController(String viewName) {
        try {
            return loadLoader(viewName).getController();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error getting controller for view: " + viewName, e);
            return null;
        }
    }

    public void clearLoader(String viewName) {
        synchronized (loaders) {
            loaders.remove(viewName);
        }
    }

    public void clearCache() {
        synchronized (loaders) {
            loaders.clear();
        }
    }

    public void initialize() {
        clearCache();
    }

    public void exit() {
        Platform.runLater(mainStage::close);
    }
}