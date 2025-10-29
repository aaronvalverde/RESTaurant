package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.model.SeccionDto;
import cr.ac.una.restuna.service.SeccionService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class SectionsMgmtController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXFilterComboBox<String> cmbType;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<SeccionDto, String> tbcName;
    @FXML
    private TreeTableColumn<SeccionDto, String> tbcTableGraphic;
    @FXML
    private TreeTableColumn<SeccionDto, String> tbcTax;
    @FXML
    private TreeTableColumn<SeccionDto, String> tbcType;
    @FXML
    private JFXTreeTableView<SeccionDto> tbvSections;
    @FXML
    private MFXTextField txfSearch;

    private final ObservableList<SeccionDto> listSections = FXCollections.observableArrayList();
    private final SeccionService seccionService = new SeccionService();
    private boolean cargando = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tbvSections.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvSections.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbType.getItems().addAll("SALON", "BARRA", "TERRAZA");

        tbcName.setCellValueFactory(x -> x.getValue().getValue().nombreProperty());
        tbcTax.setCellValueFactory(x -> new ReadOnlyStringWrapper(
            x.getValue().getValue().cobraImpuesto() ? "Sí" : "No"));
        tbcType.setCellValueFactory(x -> x.getValue().getValue().tipoProperty());
        tbcTableGraphic.setCellValueFactory(x -> new ReadOnlyStringWrapper(
                x.getValue().getValue().tieneImagen() ? "Con imagen" : "Sin imagen"));
        
        tbvSections.setOnMouseClicked(x -> {
            if (x.getClickCount() == 2 && tbvSections.getSelectionModel().getSelectedItem() != null) {
                SeccionDto selected = tbvSections.getSelectionModel().getSelectedItem().getValue();
                onEditSection(selected);
            }
        });

        TreeItem<SeccionDto> root = new RecursiveTreeItem<>(listSections, RecursiveTreeObject::getChildren);
        tbvSections.setRoot(root);
        tbvSections.setShowRoot(false);

        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> filters());
        cmbType.valueProperty().addListener((obs, oldVal, newVal) -> filters());
        
        // Cargar secciones al inicializar
        cargarSecciones();
    }

    @Override
    public void initialize() {
        // Recargar al mostrar la vista nuevamente
        if (!cargando) {
            cargarSecciones();
        }
    }

    @FXML
    void onActionBtnAdd(ActionEvent event) {
        NewSectionController controller = (NewSectionController) FlowController.getInstance()
                .getController(AppKeys.NEW_SECTION);
        controller.setParentController(this);
        controller.limpiarCampos();
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_SECTION, new Stage(), false);
    }

    @FXML
    private void onEditSection(SeccionDto sect) {
        NewSectionController controller = (NewSectionController) FlowController.getInstance()
                .getController(AppKeys.NEW_SECTION);
        controller.setParentController(this);
        controller.loadSection(sect);
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_SECTION, new Stage(), false);
    }

    public void cargarSecciones() {
        if (cargando) {
            System.out.println("Ya hay una carga en progreso, ignorando...");
            return;
        }
        
        System.out.println("Iniciando carga de secciones...");
        cargando = true;
        btnAdd.setDisable(true);
        
        Task<Respuesta> task = new Task<>() {
            @Override
            protected Respuesta call() {
                System.out.println("Task: Obteniendo secciones del servidor");
                return seccionService.getSecciones();
            }
        };
        
        task.setOnSucceeded(e -> {
            try {
                Respuesta res = task.getValue();
                
                if (res != null && res.getEstado()) {
                    String jsonSecciones = (String) res.getResultado("Secciones");
                    System.out.println("JSON recibido: " + jsonSecciones);
                    
                    if (jsonSecciones != null && !jsonSecciones.trim().isEmpty()) {
                        procesarSeccionesDesdeJson(jsonSecciones);
                    }
                } else {
                    String mensaje = res != null ? res.getMensaje() : "Error desconocido";
                    showMessage("Error cargando secciones: " + mensaje);
                }
                
            } finally {
                cargando = false;
                btnAdd.setDisable(false);
            }
        });
        
        task.setOnFailed(e -> {
            cargando = false;
            btnAdd.setDisable(false);
            Throwable ex = task.getException();
            System.err.println("Error cargando secciones: " + ex.getMessage());
            ex.printStackTrace();
            
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al cargar secciones");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            });
        });
        
        new Thread(task).start();
    }

    private void procesarSeccionesDesdeJson(String seccionesJson) {
        try {
            listSections.clear();
            System.out.println("Procesando JSON de secciones...");
            
            String jsonTrimmed = seccionesJson.trim();
            if (jsonTrimmed.startsWith("[")) {
                procesarArrayDeSecciones(seccionesJson);
            }
            
            // Workaround para JFXTreeTableView
            TreeItem<SeccionDto> newRoot = new RecursiveTreeItem<>(listSections, RecursiveTreeObject::getChildren);
            tbvSections.setRoot(null);
            tbvSections.setRoot(newRoot);
            tbvSections.setShowRoot(false);
            
            System.out.println("Secciones cargadas: " + listSections.size());
            
        } catch (Exception e) {
            System.err.println("Error procesando secciones desde JSON: " + e.getMessage());
            e.printStackTrace();
            showMessage("Error procesando datos: " + e.getMessage());
        }
    }

    private void procesarArrayDeSecciones(String arrayJson) {
        try {
            String contenido = arrayJson.substring(1, arrayJson.length() - 1);
            int nivelLlaves = 0;
            StringBuilder objetoSeccion = new StringBuilder();
            
            for (int i = 0; i < contenido.length(); i++) {
                char c = contenido.charAt(i);
                
                if (c == '{') {
                    nivelLlaves++;
                    objetoSeccion.append(c);
                } else if (c == '}') {
                    nivelLlaves--;
                    objetoSeccion.append(c);
                    
                    if (nivelLlaves == 0) {
                        procesarObjetoSeccion(objetoSeccion.toString());
                        objetoSeccion = new StringBuilder();
                        
                        if (i + 1 < contenido.length() && contenido.charAt(i + 1) == ',') {
                            i++;
                        }
                    }
                } else if (nivelLlaves > 0) {
                    objetoSeccion.append(c);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando array de secciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarObjetoSeccion(String objetoJson) {
        try {
            SeccionDto seccion = new SeccionDto();
            
            // Extraer ID
            String idStr = extraerValorNumerico(objetoJson, "idSeccion");
            if (idStr != null) {
                seccion.setIdSeccion(Long.parseLong(idStr));
            }
            
            // Extraer campos string
            String nombre = extraerValor(objetoJson, "nombre");
            String tipo = extraerValor(objetoJson, "tipo");
            String cobraImpuesto = extraerValor(objetoJson, "cobraImpuesto");
            String estado = extraerValor(objetoJson, "estado");
            
            if (nombre != null) seccion.setNombre(nombre);
            if (tipo != null) seccion.setTipo(tipo);
            if (cobraImpuesto != null) seccion.setCobraImpuesto(cobraImpuesto);
            if (estado != null) seccion.setEstado(estado);
            
            // Extraer ID de imagen
            String idImagenStr = extraerValorNumerico(objetoJson, "idArchivoImagen");
            if (idImagenStr != null) {
                seccion.setIdArchivoImagen(Long.parseLong(idImagenStr));
            }
            
            // Extraer fecha
            String fechaStr = extraerValor(objetoJson, "fechaCreacion");
            if (fechaStr != null) {
                try {
                    seccion.setFechaCreacion(LocalDate.parse(fechaStr, DateTimeFormatter.ISO_DATE));
                } catch (Exception ex) {
                    System.err.println("Error parseando fecha: " + ex.getMessage());
                }
            }
            
            listSections.add(seccion);
            
        } catch (Exception e) {
            System.err.println("Error procesando objeto de sección: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extraerValor(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extraerValorNumerico(String json, String campo) {
        String patron = "\"" + campo + "\"\\s*:\\s*(\\d+)";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private void showMessage(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void filters() {
        String search = txfSearch.getText() == null ? "" : txfSearch.getText().toLowerCase();
        String type = cmbType.getValue();

        ObservableList<SeccionDto> filter = listSections.filtered(x
                -> x.getNombre().toLowerCase().contains(search)
                || x.getTipo().toLowerCase().contains(search))
                .filtered(t -> type == null || type.isEmpty() || t.getTipo().equals(type));

        TreeItem<SeccionDto> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
        tbvSections.setRoot(root);
        tbvSections.setShowRoot(false);
    }
}
