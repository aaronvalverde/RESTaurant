package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.model.ArchivoDto;
import cr.ac.una.restuna.model.SeccionDto;
import cr.ac.una.restuna.service.ArchivoService;
import cr.ac.una.restuna.service.SeccionService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.ImagenUtil;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class SectionsMgmtController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXButton btnBack;
    @FXML
    private MFXButton btnClearFilters;
    @FXML
    private MFXFilterComboBox<String> cmbType;
    @FXML
    private MFXScrollPane tableRoot;
    @FXML
    private TreeTableColumn<SeccionDto, String> tbcName;
    @FXML
    private TreeTableColumn<SeccionDto, String> tbcTableGraphic;
    
    
    private final Map<Long, Image> imageCache = new HashMap<>();
    private final ArchivoService archivoService = new ArchivoService();
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
    @FXML
    private TreeTableColumn<SeccionDto, Void> tbcActions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tbvSections.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvSections.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbType.getItems().addAll("SALON", "BARRA", "TERRAZA");

        tbcName.setCellValueFactory(x -> x.getValue().getValue().nombreProperty());
        tbcTax.setCellValueFactory(x -> new ReadOnlyStringWrapper(
                x.getValue().getValue().cobraImpuesto() ? "Sí" : "No"));
        tbcType.setCellValueFactory(x -> x.getValue().getValue().tipoProperty());
        
        
        tbcTableGraphic.setCellValueFactory(x -> new ReadOnlyStringWrapper(""));
        tbcTableGraphic.setCellFactory(column -> new TreeTableCell<SeccionDto, String>() {
            private final ImageView imageView = new ImageView();
            
            {
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(javafx.geometry.Pos.CENTER);
                
                if (empty || getTreeTableRow() == null || getTreeTableRow().getTreeItem() == null) {
                    setGraphic(null);
                    return;
                }
                
                SeccionDto seccion = getTreeTableRow().getTreeItem().getValue();
                if (seccion != null && seccion.getIdArchivoImagen() != null && seccion.getIdArchivoImagen() > 0) {
                    cargarImagenMiniatura(seccion.getIdArchivoImagen(), imageView);
                    setGraphic(imageView);
                } else {
                    setGraphic(null);
                }
            }
        });

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

        
        cargarSecciones();
        setActionsColumn();
    }

    @Override
    public void initialize() {
        
        if (!cargando) {
            cargarSecciones();
        }
    }

    @FXML
    void onActionBtnBack(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MANAGEMENT);
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
    void onActionBtnClearFilters(ActionEvent event) {
        
        txfSearch.clear();
        
        
        cmbType.clearSelection();
        cmbType.setValue(null);
        
        
        filters();
    }

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
            
            imageCache.clear();
            System.out.println("Procesando JSON de secciones...");

            String jsonTrimmed = seccionesJson.trim();
            if (jsonTrimmed.startsWith("[")) {
                procesarArrayDeSecciones(seccionesJson);
            }

            
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

            
            String idStr = extraerValorNumerico(objetoJson, "idSeccion");
            if (idStr != null) {
                seccion.setIdSeccion(Long.parseLong(idStr));
            }

            
            String nombre = extraerValor(objetoJson, "nombre");
            String tipo = extraerValor(objetoJson, "tipo");
            String cobraImpuesto = extraerValor(objetoJson, "cobraImpuesto");
            String estado = extraerValor(objetoJson, "estado");

            if (nombre != null) {
                seccion.setNombre(nombre);
            }
            if (tipo != null) {
                seccion.setTipo(tipo);
            }
            if (cobraImpuesto != null) {
                seccion.setCobraImpuesto(cobraImpuesto);
            }
            if (estado != null) {
                seccion.setEstado(estado);
            }

            
            String idImagenStr = extraerValorNumerico(objetoJson, "idArchivoImagen");
            if (idImagenStr != null) {
                seccion.setIdArchivoImagen(Long.parseLong(idImagenStr));
            }

            
            String fechaStr = extraerValor(objetoJson, "fechaCreacion");
            if (fechaStr != null) {
                try {
                    
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(fechaStr, DateTimeFormatter.ISO_ZONED_DATE_TIME);
                    seccion.setFechaCreacion(zonedDateTime.toLocalDate());
                } catch (Exception ex) {
                    System.err.println("Error parseando fecha: " + ex.getMessage());
                    
                    try {
                        seccion.setFechaCreacion(LocalDate.parse(fechaStr.substring(0, 10)));
                    } catch (Exception ex2) {
                        
                    }
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
    
    private void cargarImagenMiniatura(Long idArchivo, ImageView imageView) {
        
        if (imageCache.containsKey(idArchivo)) {
            imageView.setImage(imageCache.get(idArchivo));
            return;
        }
        
        
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                Respuesta res = archivoService.getArchivo(idArchivo);
                
                if (!res.getEstado()) {
                    return null;
                }
                
                String archivoJson = (String) res.getResultado("Archivo");
                ArchivoDto archivoDto = parsearArchivoDto(archivoJson);
                
                if (archivoDto != null && archivoDto.tieneContenido()) {
                    return ImagenUtil.archivoDtoToImage(archivoDto);
                }
                
                return null;
            }
        };
        
        loadImageTask.setOnSucceeded(e -> {
            Image image = loadImageTask.getValue();
            if (image != null) {
                imageCache.put(idArchivo, image);
                Platform.runLater(() -> imageView.setImage(image));
            }
        });
        
        loadImageTask.setOnFailed(e -> {
            System.err.println("Error cargando imagen miniatura: " + loadImageTask.getException().getMessage());
        });
        
        new Thread(loadImageTask).start();
    }
    
    private ArchivoDto parsearArchivoDto(String json) {
        try {
            ArchivoDto dto = new ArchivoDto();
            
            String idStr = extraerValorNumerico(json, "idArchivo");
            if (idStr != null) dto.setIdArchivo(Long.parseLong(idStr));
            
            String nombre = extraerValor(json, "nombreArchivo");
            if (nombre != null) dto.setNombreArchivo(nombre);
            
            String mime = extraerValor(json, "tipoMime");
            if (mime != null) dto.setTipoMime(mime);
            
            String base64 = extraerValor(json, "contenidoBase64");
            if (base64 != null) dto.setContenidoBase64(base64);
            
            String tamanioStr = extraerValorNumerico(json, "tamanio");
            if (tamanioStr != null) dto.setTamanio(Long.parseLong(tamanioStr));
            
            return dto;
        } catch (Exception e) {
            System.err.println("Error parseando ArchivoDto: " + e.getMessage());
            return null;
        }
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

    private void setActionsColumn() {
        tbcActions.setCellFactory(col -> new TreeTableCell<SeccionDto, Void>() {
            MFXButton btnEdit = new MFXButton("✏️ Editar");
            MFXButton btnDelete = new MFXButton("🗑️ Eliminar");

            {
                
                
                
                
                btnEdit.setOnAction(e -> {
                    SeccionDto seccionDto = getTreeTableRow().getItem();
                    if(seccionDto != null) onEditSection(seccionDto);
                });
                btnDelete.setOnAction(e -> {
                    SeccionDto seccionDto = getTreeTableRow().getItem();
                    if(seccionDto != null) onDeleteSection(seccionDto);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, btnEdit, btnDelete);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(hbox);
                }
            }
        });

    }

    private void onDeleteSection(SeccionDto seccion) {
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar esta sección?");
        confirmacion.setContentText("Sección: " + seccion.getNombre() + " (" + seccion.getTipo() + ")\n\nEsta acción no se puede deshacer.");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                eliminarSeccion(seccion);
            }
        });
    }
    
    private void eliminarSeccion(SeccionDto seccion) {
        
        if (seccion.getIdSeccion() == null || seccion.getIdSeccion() <= 0) {
            mostrarError("Error", "No se puede eliminar la sección", "La sección no tiene un ID válido.");
            return;
        }
        
        Task<Respuesta> deleteTask = new Task<>() {
            @Override
            protected Respuesta call() throws Exception {
                return seccionService.eliminarSeccion(seccion.getIdSeccion());
            }
        };
        
        deleteTask.setOnSucceeded(event -> {
            Respuesta respuesta = deleteTask.getValue();
            
            if (respuesta.getEstado()) {
                
                listSections.remove(seccion);
                
                
                if (seccion.getIdArchivoImagen() != null && seccion.getIdArchivoImagen() > 0) {
                    imageCache.remove(seccion.getIdArchivoImagen());
                }
                
                
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Éxito");
                alerta.setHeaderText("Sección eliminada");
                alerta.setContentText("La sección '" + seccion.getNombre() + "' ha sido eliminada correctamente.");
                alerta.showAndWait();
                
            } else {
                mostrarError("Error al eliminar", "No se pudo eliminar la sección", respuesta.getMensaje());
            }
        });
        
        deleteTask.setOnFailed(event -> {
            mostrarError("Error", "Error al eliminar sección", deleteTask.getException().getMessage());
        });
        
        new Thread(deleteTask).start();
    }
    
    private void mostrarError(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
    
    private String getLanguageString(String key) {
        return FlowController.getInstance().getLanguage().getString(key);
    }
}
