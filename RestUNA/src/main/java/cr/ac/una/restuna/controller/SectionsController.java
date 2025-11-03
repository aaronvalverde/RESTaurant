package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.MesaDto;
import cr.ac.una.restuna.model.SeccionDto;
import cr.ac.una.restuna.service.MesaService;
import cr.ac.una.restuna.service.SeccionService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import cr.ac.una.restuna.util.JsonParser;
import java.util.Optional;


public class SectionsController extends Controller implements Initializable {

    @FXML
    private MFXButton btnAddTable;
    @FXML
    private MFXButton btnDeleteTable;
    @FXML
    private MFXButton btnExitEditMode;
    @FXML
    private MFXButton btnEditMode;
    @FXML
    private MFXScrollPane sectionsRoot;
    @FXML
    private VBox sectionsContainer;
    @FXML
    private MFXButton btnExit;
    @FXML
    private AnchorPane sectionPane;
    @FXML
    private VBox editModeBox;
    @FXML
    private VBox btnToBill;
    
    
    private final SeccionService seccionService = new SeccionService();
    private final MesaService mesaService = new MesaService();
    
    
    private SeccionDto seccionActual;
    private List<MesaDto> mesasActuales = new ArrayList<>();
    private Map<MFXButton, MesaDto> mesaButtonMap = new HashMap<>();
    private MFXButton mesaSeleccionada;
    private MFXButton seccionSeleccionada;
    private boolean modoEdicion = false;
    private boolean hayCambios = false;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setNombreVista(AppKeys.SECTIONS);
        sectionsContainer.prefHeightProperty().bind(sectionsRoot.heightProperty());
        sectionsContainer.prefWidthProperty().bind(sectionsRoot.widthProperty());
        btnAddTable.setDisable(true);
        onEditMode(false);
        configurarDragAndDrop();
        loadSections();
    }

    @Override
    public void initialize() {
        
    }
    
    
    private void configurarDragAndDrop() {
        
        sectionPane.setOnDragOver(e -> {
            if (e.getGestureSource() instanceof MFXButton && modoEdicion) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        
        sectionPane.setOnDragDropped(e -> {
            if (modoEdicion && e.getGestureSource() instanceof MFXButton) {
                MFXButton btnMesa = (MFXButton) e.getGestureSource();
                MesaDto mesa = mesaButtonMap.get(btnMesa);
                
                if (mesa != null) {
                    double x = e.getX() - btnMesa.getWidth() / 2;
                    double y = e.getY() - btnMesa.getHeight() / 2;
                    btnMesa.relocate(x, y);
                    
                    
                    mesa.setPosicionX(x);
                    mesa.setPosicionY(y);
                    hayCambios = true;
                    
                    
                    guardarPosicionMesa(mesa);
                    
                    e.setDropCompleted(true);
                }
            }
            e.consume();
        });
        
        
        btnToBill.setOnDragOver(e -> {
            if (e.getGestureSource() instanceof MFXButton && !modoEdicion) {
                MFXButton btnMesa = (MFXButton) e.getGestureSource();
                MesaDto mesa = mesaButtonMap.get(btnMesa);
                
                
                if (mesa != null && "OCUPADA".equals(mesa.getEstado())) {
                    e.acceptTransferModes(TransferMode.MOVE);
                }
            }
            e.consume();
        });
        
        btnToBill.setOnDragDropped(e -> {
            if (!modoEdicion && e.getGestureSource() instanceof MFXButton) {
                MFXButton btnMesa = (MFXButton) e.getGestureSource();
                MesaDto mesa = mesaButtonMap.get(btnMesa);
                
                if (mesa != null && "OCUPADA".equals(mesa.getEstado())) {
                    
                    abrirFacturacion(mesa);
                    e.setDropCompleted(true);
                }
            }
            e.consume();
        });
    }

    @FXML
    private void onActionBtnAddTable(ActionEvent event) {
        if (seccionActual == null) {
            mostrarAlerta(Alert.AlertType.WARNING, getMsg("add.table"),
                getMsg("choose.section.first"));
            return;
        }
        
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(getMsg("new.table"));
        dialog.setHeaderText(getMsg("add.new.table"));
        dialog.setContentText(getMsg("table.number"));
        
        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isEmpty() || resultado.get().trim().isEmpty()) {
            return;
        }
        
        String numeroMesa = resultado.get().trim();
        
        
        MesaDto mesa = new MesaDto();
        mesa.setIdSeccion(seccionActual.getIdSeccion());
        mesa.setNumeroMesa(numeroMesa);
        mesa.setCapacidad(4); 
        mesa.setPosicionX(100.0);
        mesa.setPosicionY(100.0);
        mesa.setEstado("LIBRE");
        
        
        Respuesta respuesta = mesaService.guardarMesa(mesa);
        
        if (respuesta.getEstado()) {
            String contenido = (String) respuesta.getResultado("Mesa");
            if (contenido != null) {
                MesaDto mesaGuardada = parsearMesa(contenido);
                if (mesaGuardada != null) {
                    mesasActuales.add(mesaGuardada);
                    crearBotonMesa(mesaGuardada);
                    mostrarAlerta(Alert.AlertType.INFORMATION, getMsg("new.table"),
                        getMsg("table.success"));
                }
            }
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, getMsg("new.table"),
                getMsg("error.table") + respuesta.getMensaje());
        }
    }

    @FXML
    private void onActionBtnDeleteTable(ActionEvent event) {
        if (mesaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, getMsg("delete.table"),
                getMsg("choose.table.delete"));
            return;
        }
        
        MesaDto mesa = mesaButtonMap.get(mesaSeleccionada);
        if (mesa == null || mesa.getIdMesa() == null) {
            
            sectionPane.getChildren().remove(mesaSeleccionada);
            mesaButtonMap.remove(mesaSeleccionada);
            mesasActuales.remove(mesa);
            mesaSeleccionada = null;
            return;
        }
        
        
        if ("OCUPADA".equals(mesa.getEstado())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Eliminar Mesa", 
                "No se puede eliminar una mesa ocupada con órdenes activas. " +
                "Primero debe facturar o cancelar las órdenes de esta mesa.");
            return;
        }
        
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Mesa");
        confirmacion.setHeaderText("¿Está seguro?");
        confirmacion.setContentText("¿Desea eliminar la mesa " + mesa.getNumeroMesa() + "?");
        
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Respuesta respuesta = mesaService.eliminarMesa(mesa.getIdMesa());
            if (respuesta.getEstado()) {
                sectionPane.getChildren().remove(mesaSeleccionada);
                mesaButtonMap.remove(mesaSeleccionada);
                mesasActuales.remove(mesa);
                mesaSeleccionada = null;
                mostrarAlerta(Alert.AlertType.INFORMATION, "Eliminar Mesa", 
                    "Mesa eliminada correctamente");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Eliminar Mesa", 
                    respuesta.getMensaje());
            }
        }
    }

    @FXML
    private void onActionBtnExitEditMode(ActionEvent event) {
        if (hayCambios) {
            guardarCambios();
        }
        onEditMode(false);
    }

    @FXML
    private void onActionBtnEditMode(ActionEvent event) {
        onEditMode(true);
        
    }

    @FXML
    private void onActionBtnExit(ActionEvent event) {
        if (hayCambios) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Cambios sin guardar");
            confirmacion.setHeaderText("Hay cambios sin guardar");
            confirmacion.setContentText("¿Desea salir sin guardar los cambios?");
            
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                return;
            }
        }
        
        FlowController.getInstance().goHome();
    }

    private void onEditMode(Boolean editMode) {
        this.modoEdicion = editMode;
        editModeBox.setVisible(editMode);
        editModeBox.setManaged(editMode);
        btnExitEditMode.setVisible(editMode);
        btnExitEditMode.setManaged(editMode);
        btnEditMode.setVisible(!editMode);
        btnEditMode.setManaged(!editMode);
        btnDeleteTable.setDisable(true);
        
        if (mesaSeleccionada != null && !editMode) {
            
            mesaSeleccionada.setStyle(mesaSeleccionada.getStyle().replace("-fx-border-color: yellow; -fx-border-width: 3px;", ""));
            mesaSeleccionada = null;
        }
    }

    private void loadSections() {
        Respuesta respuesta = seccionService.getSeccionesActivas();
        
        if (!respuesta.getEstado()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Cargar Secciones", 
                "Error al cargar las secciones: " + respuesta.getMensaje());
            return;
        }
        
        String contenido = (String) respuesta.getResultado("Secciones");
        
        if (contenido == null) {
            return;
        }
        
        sectionsContainer.getChildren().clear();
        
        
        List<String> objetosSecciones = JsonParser.extraerObjetosDelArray(contenido);
        
        for (String objetoJson : objetosSecciones) {
            SeccionDto seccion = parsearSeccion(objetoJson);
            
            if (seccion != null) {
                
                String textoBoton = seccion.getNombre() + " (" + seccion.getTipo() + ")";
                MFXButton btnSeccion = new MFXButton(textoBoton);
                btnSeccion.setPrefWidth(200);
                btnSeccion.setPrefHeight(50);
                btnSeccion.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px;");
                VBox.setMargin(btnSeccion, new Insets(5));
                
                btnSeccion.setOnAction(e -> {
                    seleccionarSeccion(seccion, btnSeccion);
                });
                
                sectionsContainer.getChildren().add(btnSeccion);
            }
        }
    }
    
    private void seleccionarSeccion(SeccionDto seccion, MFXButton boton) {
        
        if (seccionSeleccionada != null) {
            seccionSeleccionada.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px;");
        }
        
        
        seccionSeleccionada = boton;
        boton.setStyle("-fx-background-color: #FFA726; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        
        btnAddTable.setDisable(false);
        
        
        loadMesas(seccion);
    }
    
    private SeccionDto parsearSeccion(String objetoJson) {
        try {
            SeccionDto seccion = new SeccionDto();
            seccion.setIdSeccion(JsonParser.extraerValorLong(objetoJson, "idSeccion"));
            seccion.setNombre(JsonParser.extraerValor(objetoJson, "nombre"));
            seccion.setTipo(JsonParser.extraerValor(objetoJson, "tipo"));
            
            Boolean cobraImpuesto = JsonParser.extraerValorBooleano(objetoJson, "cobraImpuesto");
            seccion.setCobraImpuesto(cobraImpuesto != null ? (cobraImpuesto ? "S" : "N") : "N");
            
            seccion.setEstado(JsonParser.extraerValor(objetoJson, "estado"));
            
            return seccion;
        } catch (Exception e) {
            return null;
        }
    }
    
    private void loadMesas(SeccionDto seccion) {
        this.seccionActual = seccion;
        this.hayCambios = false;
        
        Respuesta respuesta = mesaService.getMesasPorSeccion(seccion.getIdSeccion());
        
        if (!respuesta.getEstado()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Cargar Mesas", 
                "Error al cargar las mesas: " + respuesta.getMensaje());
            return;
        }
        
        
        sectionPane.getChildren().removeIf(node -> node instanceof MFXButton);
        mesaButtonMap.clear();
        mesasActuales.clear();
        mesaSeleccionada = null;
        
        String contenido = (String) respuesta.getResultado("Mesas");
        if (contenido == null || contenido.equals("[]")) {
            return;
        }
        
        
        List<String> objetosMesas = JsonParser.extraerObjetosDelArray(contenido);
        
        for (String objetoJson : objetosMesas) {
            MesaDto mesa = parsearMesa(objetoJson);
            
            if (mesa != null) {
                mesasActuales.add(mesa);
                crearBotonMesa(mesa);
            }
        }
    }
    
    private MesaDto parsearMesa(String objetoJson) {
        try {
            MesaDto mesa = new MesaDto();
            mesa.setIdMesa(JsonParser.extraerValorLong(objetoJson, "idMesa"));
            mesa.setIdSeccion(JsonParser.extraerValorLong(objetoJson, "idSeccion"));
            mesa.setNumeroMesa(JsonParser.extraerValor(objetoJson, "numeroMesa"));
            
            Integer capacidad = JsonParser.extraerValorInteger(objetoJson, "capacidad");
            if (capacidad != null) {
                mesa.setCapacidad(capacidad);
            }
            
            String posXStr = JsonParser.extraerValorNumerico(objetoJson, "posicionX");
            if (posXStr != null) {
                mesa.setPosicionX(Double.parseDouble(posXStr));
            }
            
            String posYStr = JsonParser.extraerValorNumerico(objetoJson, "posicionY");
            if (posYStr != null) {
                mesa.setPosicionY(Double.parseDouble(posYStr));
            }
            
            mesa.setEstado(JsonParser.extraerValor(objetoJson, "estado"));
            
            return mesa;
        } catch (Exception e) {
            return null;
        }
    }
    
    private void crearBotonMesa(MesaDto mesa) {
        MFXButton btnMesa = new MFXButton(mesa.getNumeroMesa());
        btnMesa.setPrefHeight(80);
        btnMesa.setPrefWidth(80);
        
        
        String color = switch (mesa.getEstado()) {
            case "LIBRE" -> "#4CAF50"; 
            case "OCUPADA" -> "#F44336"; 
            case "RESERVADA" -> "#FF9800"; 
            case "FUERA_SERVICIO" -> "#9E9E9E"; 
            default -> "#2196F3"; 
        };
        
        btnMesa.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px;");
        
        
        if (mesa.getPosicionX() != null && mesa.getPosicionY() != null) {
            btnMesa.relocate(mesa.getPosicionX(), mesa.getPosicionY());
        }
        
        
        btnMesa.setOnDragDetected(e -> {
            if (modoEdicion) {
                
                Dragboard db = btnMesa.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("mover");
                db.setContent(content);
                e.consume();
            } else if ("OCUPADA".equals(mesa.getEstado())) {
                
                Dragboard db = btnMesa.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("facturar");
                db.setContent(content);
                e.consume();
            }
        });
        
        
        btnMesa.setOnMouseClicked(e -> {
            if (modoEdicion && e.getButton() == MouseButton.PRIMARY) {
                if (mesaSeleccionada != null) {
                    
                    mesaSeleccionada.setStyle(mesaSeleccionada.getStyle().replace("-fx-border-color: yellow; -fx-border-width: 3px;", ""));
                }
                mesaSeleccionada = btnMesa;
                btnMesa.setStyle(btnMesa.getStyle() + "-fx-border-color: yellow; -fx-border-width: 3px;");
                btnDeleteTable.setDisable(false);
            }
        });
        
        mesaButtonMap.put(btnMesa, mesa);
        sectionPane.getChildren().add(btnMesa);
    }
    
    private void guardarPosicionMesa(MesaDto mesa) {
        
        Respuesta respuesta = mesaService.guardarMesa(mesa);
        
        if (respuesta.getEstado()) {
            System.out.println("Posición de mesa " + mesa.getNumeroMesa() + " guardada: (" + 
                             mesa.getPosicionX() + ", " + mesa.getPosicionY() + ")");
        } else {
            System.err.println("Error guardando posición: " + respuesta.getMensaje());
        }
    }
    
    private void guardarCambios() {
        if (!hayCambios || mesasActuales.isEmpty()) {
            return;
        }
        
        Respuesta respuesta = mesaService.guardarMesas(mesasActuales);
        
        if (respuesta.getEstado()) {
            hayCambios = false;
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardar Cambios", 
                "Cambios guardados correctamente");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Guardar Cambios", 
                "Error al guardar cambios: " + respuesta.getMensaje());
        }
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    
    private void abrirFacturacion(MesaDto mesa) {
        try {
            
            BillingController billingController = (BillingController) FlowController.getInstance()
                .getController(AppKeys.BILLING);
            
            
            if (billingController != null) {
                billingController.cargarMesa(mesa);
            }
            
            
            FlowController.getInstance().goView(AppKeys.BILLING);
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                "No se pudo abrir la ventana de facturación: " + e.getMessage());
        }
    }

    @FXML
    void onDragDroppedToBill(DragEvent event) {
        if (event.getGestureSource() instanceof MFXButton) {
            MFXButton btnMesa = (MFXButton) event.getGestureSource();
            MesaDto mesa = mesaButtonMap.get(btnMesa);
            
            if (mesa != null && "OCUPADA".equals(mesa.getEstado())) {
                
                abrirFacturacion(mesa);
                event.setDropCompleted(true);
            } else if (mesa != null && !"OCUPADA".equals(mesa.getEstado())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Facturar Mesa", 
                    "Solo se pueden facturar mesas ocupadas");
                event.setDropCompleted(false);
            }
        }
        event.consume();
    }

    @FXML
    void onDragOverToBill(DragEvent event) {
        if (event.getGestureSource() instanceof MFXButton && !modoEdicion) {
            MFXButton btnMesa = (MFXButton) event.getGestureSource();
            MesaDto mesa = mesaButtonMap.get(btnMesa);
            if (mesa != null && "OCUPADA".equals(mesa.getEstado())) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        }
        event.consume();
    }

    private String getMsg(String key) {
        return FlowController.getInstance().getLanguage().getString(key);
    }
}
