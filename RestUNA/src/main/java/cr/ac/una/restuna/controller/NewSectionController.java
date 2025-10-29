package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.ArchivoDto;
import cr.ac.una.restuna.model.SeccionDto;
import cr.ac.una.restuna.service.ArchivoService;
import cr.ac.una.restuna.service.SeccionService;
import cr.ac.una.restuna.util.ImagenUtil;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.AppKeys;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

public class NewSectionController extends Controller implements Initializable {

    @FXML
    private MFXTextField txfName;
    @FXML
    private MFXComboBox<String> cmbType;
    @FXML
    private ImageView imvTableGraphic;
    @FXML
    private MFXButton btnChooseImage;
    @FXML
    private MFXButton btnChangeImage;
    @FXML
    private MFXButton btnDeleteImage;
    @FXML
    private MFXButton btnSaveChanges;
    @FXML
    private MFXButton btnAdd;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private HBox imageRoot;
    @FXML
    private MFXCheckbox cbSalesTax;

    private boolean editMode = false;
    private SeccionDto section;
    private SectionsMgmtController parentController;
    private final SeccionService seccionService = new SeccionService();
    private final ArchivoService archivoService = new ArchivoService();
    private File selectedImageFile;
    private Long currentImageId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Use AppKeys constant for the view name/title so FlowController can set window title
        setNombreVista(AppKeys.NEW_SECTION);
        cmbType.getItems().addAll("SALON", "BARRA", "TERRAZA");
        initButtons();
    }

    @Override
    public void initialize() {
        limpiarCampos();
    }
    
    public void setParentController(SectionsMgmtController parent) {
        this.parentController = parent;
    }
    
    public void limpiarCampos() {
        editMode = false;
        section = null;
        selectedImageFile = null;
        currentImageId = null;
        
        txfName.clear();
        cmbType.getSelectionModel().clearSelection();
        cbSalesTax.setSelected(false);
        imvTableGraphic.setImage(null);
        imvTableGraphic.setUserData(null);
        
        initButtons();
    }

    @FXML
    private void onActionBtnChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen de la sección");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de imagen", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            // Validar que sea imagen
            if (!ImagenUtil.esImagen(file)) {
                showMessage("Por favor seleccione un archivo de imagen válido (PNG, JPG, GIF, BMP)");
                return;
            }
            
            // Validar tamaño (máximo 5MB)
            if (!ImagenUtil.validarTamanio(file)) {
                showMessage("La imagen es demasiado grande. Tamaño máximo: 5MB\nTamaño actual: " + 
                           ImagenUtil.formatearTamanio(file.length()));
                return;
            }
            
            try {
                selectedImageFile = file;
                Image image = new Image(file.toURI().toString());
                imvTableGraphic.setImage(image);
                System.out.println("Imagen seleccionada: " + file.getName() + " (" + 
                                  ImagenUtil.formatearTamanio(file.length()) + ")");
            } catch (Exception e) {
                System.err.println("Error cargando imagen: " + e.getMessage());
                showMessage("Error al cargar la imagen: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onActionBtnChangeImage(ActionEvent event) {
        onActionBtnChooseImage(event);
    }

    @FXML
    private void onActionBtnDeleteImage(ActionEvent event) {
        selectedImageFile = null;
        currentImageId = null;
        imvTableGraphic.setImage(null);
        imvTableGraphic.setUserData(null);
        System.out.println("Imagen eliminada");
    }

    @FXML
    private void onActionBtnSaveChanges(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }
        
        guardarSeccion(true);
    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }
        
        guardarSeccion(false);
    }
    
    private boolean validarCampos() {
        if (txfName.getText() == null || txfName.getText().trim().isEmpty()) {
            showMessage("El nombre de la sección es obligatorio");
            txfName.requestFocus();
            return false;
        }
        
        if (cmbType.getValue() == null || cmbType.getValue().trim().isEmpty()) {
            showMessage("El tipo de sección es obligatorio");
            cmbType.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void guardarSeccion(boolean esEdicion) {
        // Deshabilitar botones mientras se guarda
        btnAdd.setDisable(true);
        btnSaveChanges.setDisable(true);
        btnCancel.setDisable(true);
        
        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Crear o actualizar DTO
                SeccionDto dto = esEdicion ? section : new SeccionDto();
                dto.setNombre(txfName.getText().trim());
                dto.setTipo(cmbType.getValue());
                dto.setCobraImpuesto(cbSalesTax.isSelected() ? "S" : "N");
                dto.setEstado("A");
                
                // Si hay una imagen nueva seleccionada, subirla primero
                if (selectedImageFile != null) {
                    System.out.println("Subiendo imagen: " + selectedImageFile.getName());
                    ArchivoDto archivoDto = ImagenUtil.fileToArchivoDto(selectedImageFile);
                    Respuesta resArchivo = archivoService.guardarArchivo(archivoDto);
                    
                    if (resArchivo.getEstado()) {
                        // Extraer ID del archivo guardado
                        String archivoJson = (String) resArchivo.getResultado("Archivo");
                        Long idArchivo = extraerIdArchivo(archivoJson);
                        if (idArchivo != null) {
                            dto.setIdArchivoImagen(idArchivo);
                            System.out.println("Imagen guardada con ID: " + idArchivo);
                        }
                    } else {
                        throw new Exception("Error guardando imagen: " + resArchivo.getMensaje());
                    }
                } else if (currentImageId != null && currentImageId > 0) {
                    // Mantener la imagen existente
                    dto.setIdArchivoImagen(currentImageId);
                }
                
                // Guardar la sección
                System.out.println("Guardando sección: " + dto.getNombre());
                Respuesta resSeccion = seccionService.guardarSeccion(dto);
                
                if (!resSeccion.getEstado()) {
                    throw new Exception("Error guardando sección: " + resSeccion.getMensaje());
                }
                
                System.out.println("Sección guardada exitosamente");
                return null;
            }
        };
        
        saveTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                showMessage(esEdicion ? "Sección actualizada correctamente" : "Sección creada correctamente");
                
                // Recargar la lista en el controlador padre
                if (parentController != null) {
                    parentController.cargarSecciones();
                }
                
                // Cerrar ventana
                getStage().close();
            });
        });
        
        saveTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                Throwable ex = saveTask.getException();
                System.err.println("Error guardando sección: " + ex.getMessage());
                ex.printStackTrace();
                showMessage("Error: " + ex.getMessage());
                
                // Rehabilitar botones
                btnAdd.setDisable(false);
                btnSaveChanges.setDisable(false);
                btnCancel.setDisable(false);
            });
        });
        
        new Thread(saveTask).start();
    }
    
    private Long extraerIdArchivo(String archivoJson) {
        try {
            Pattern pattern = Pattern.compile("\"idArchivo\"\\s*:\\s*(\\d+)");
            Matcher matcher = pattern.matcher(archivoJson);
            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo ID de archivo: " + e.getMessage());
        }
        return null;
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        getStage().close();
    }



    @FXML
    private void onDragOverToSetImage(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
            imageRoot.setStyle("-fx-border-color: #2196F3; -fx-border-style: dashed; -fx-border-width: 2px; -fx-border-radius: 5px;");
        }
        event.consume();
    }

    @FXML
    private void onDragDroppedToSetImage(DragEvent event) {
        boolean success = false;
        
        if (event.getDragboard().hasFiles()) {
            for (File file : event.getDragboard().getFiles()) {
                if (ImagenUtil.esImagen(file)) {
                    if (!ImagenUtil.validarTamanio(file)) {
                        showMessage("La imagen es demasiado grande. Tamaño máximo: 5MB");
                        break;
                    }
                    
                    try {
                        selectedImageFile = file;
                        Image image = new Image(file.toURI().toString());
                        imvTableGraphic.setImage(image);
                        success = true;
                        System.out.println("Imagen cargada por drag & drop: " + file.getName());
                    } catch (Exception e) {
                        System.err.println("Error cargando imagen: " + e.getMessage());
                        showMessage("Error al cargar la imagen");
                    }
                    break;
                }
            }
        }
        
        event.setDropCompleted(success);
        imageRoot.setStyle("");
        event.consume();
    }

    public void loadSection(SeccionDto sectionLoad) {
        editMode = true;
        section = sectionLoad;
        selectedImageFile = null;
        
        txfName.setText(sectionLoad.getNombre());
        cmbType.getSelectionModel().selectItem(sectionLoad.getTipo());
        cbSalesTax.setSelected("S".equals(sectionLoad.getCobraImpuesto()));
        
        // Cargar imagen si existe
        currentImageId = sectionLoad.getIdArchivoImagen();
        if (currentImageId != null && currentImageId > 0) {
            cargarImagenDesdeServidor(currentImageId);
        } else {
            imvTableGraphic.setImage(null);
        }

        initButtons();
    }
    
    private void cargarImagenDesdeServidor(Long idArchivo) {
        Task<ArchivoDto> loadImageTask = new Task<>() {
            @Override
            protected ArchivoDto call() throws Exception {
                System.out.println("Cargando imagen con ID: " + idArchivo);
                Respuesta res = archivoService.getArchivo(idArchivo);
                
                if (!res.getEstado()) {
                    throw new Exception("Error cargando imagen: " + res.getMensaje());
                }
                
                String archivoJson = (String) res.getResultado("Archivo");
                return parsearArchivoDto(archivoJson);
            }
        };
        
        loadImageTask.setOnSucceeded(e -> {
            ArchivoDto archivoDto = loadImageTask.getValue();
            if (archivoDto != null && archivoDto.tieneContenido()) {
                Image image = ImagenUtil.archivoDtoToImage(archivoDto);
                if (image != null) {
                    imvTableGraphic.setImage(image);
                    System.out.println("Imagen cargada: " + archivoDto.getNombreArchivo());
                }
            }
        });
        
        loadImageTask.setOnFailed(e -> {
            System.err.println("Error cargando imagen: " + loadImageTask.getException().getMessage());
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

    private void initButtons() {
        if (editMode) {
            btnAdd.setVisible(false);
            btnAdd.setManaged(false);
        } else {
            btnSaveChanges.setVisible(false);
            btnSaveChanges.setManaged(false);
        }
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
