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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import cr.ac.una.restuna.util.JsonParser;
import java.util.Optional;

/**
 * FXML Controller class
 * 
 * Vista para gestionar MESAS dentro de una sección seleccionada
 *
 * @author aaron
 */
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
    
    // Servicios
    private final SeccionService seccionService = new SeccionService();
    private final MesaService mesaService = new MesaService();
    
    // Estado
    private SeccionDto seccionActual;
    private List<MesaDto> mesasActuales = new ArrayList<>();
    private Map<MFXButton, MesaDto> mesaButtonMap = new HashMap<>();
    private MFXButton mesaSeleccionada;
    private MFXButton seccionSeleccionada;
    private boolean modoEdicion = false;
    private boolean hayCambios = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setNombreVista(AppKeys.SECTIONS);
        sectionsContainer.prefHeightProperty().bind(sectionsRoot.heightProperty());
        sectionsContainer.prefWidthProperty().bind(sectionsRoot.widthProperty());
        btnAddTable.setDisable(true);
        onEditMode(false);
        loadSections();
    }

    @Override
    public void initialize() {
        // Método sobrescrito de Controller - no se usa en este caso
    }

    @FXML
    private void onActionBtnAddTable(ActionEvent event) {
        if (seccionActual == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Agregar Mesa", 
                "Debe seleccionar una sección primero");
            return;
        }
        
        // Solicitar número de mesa
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nueva Mesa");
        dialog.setHeaderText("Agregar nueva mesa");
        dialog.setContentText("Número de mesa:");
        
        Optional<String> resultado = dialog.showAndWait();
        if (resultado.isEmpty() || resultado.get().trim().isEmpty()) {
            return;
        }
        
        String numeroMesa = resultado.get().trim();
        
        // Solicitar capacidad
        TextInputDialog capacidadDialog = new TextInputDialog("4");
        capacidadDialog.setTitle("Nueva Mesa");
        capacidadDialog.setHeaderText("Capacidad de la mesa");
        capacidadDialog.setContentText("Número de personas:");
        
        Optional<String> capResultado = capacidadDialog.showAndWait();
        if (capResultado.isEmpty()) {
            return;
        }
        
        int capacidad;
        try {
            capacidad = Integer.parseInt(capResultado.get().trim());
            if (capacidad < 1 || capacidad > 20) {
                mostrarAlerta(Alert.AlertType.ERROR, "Nueva Mesa", 
                    "La capacidad debe estar entre 1 y 20 personas");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Nueva Mesa", 
                "Capacidad inválida");
            return;
        }
        
        // Crear mesa
        MesaDto mesa = new MesaDto();
        mesa.setIdSeccion(seccionActual.getIdSeccion());
        mesa.setNumeroMesa(numeroMesa);
        mesa.setCapacidad(capacidad);
        mesa.setPosicionX(100.0);
        mesa.setPosicionY(100.0);
        mesa.setEstado("LIBRE");
        
        // Guardar en servidor
        Respuesta respuesta = mesaService.guardarMesa(mesa);
        
        if (respuesta.getEstado()) {
            String contenido = (String) respuesta.getResultado("Mesa");
            if (contenido != null) {
                MesaDto mesaGuardada = parsearMesa(contenido);
                if (mesaGuardada != null) {
                    mesasActuales.add(mesaGuardada);
                    crearBotonMesa(mesaGuardada);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Nueva Mesa", 
                        "Mesa creada correctamente");
                }
            }
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Nueva Mesa", 
                "Error al crear la mesa: " + respuesta.getMensaje());
        }
    }

    @FXML
    private void onActionBtnDeleteTable(ActionEvent event) {
        if (mesaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Eliminar Mesa", 
                "Debe seleccionar una mesa para eliminar");
            return;
        }
        
        MesaDto mesa = mesaButtonMap.get(mesaSeleccionada);
        if (mesa == null || mesa.getIdMesa() == null) {
            // Es una mesa nueva que aún no está guardada
            sectionPane.getChildren().remove(mesaSeleccionada);
            mesaButtonMap.remove(mesaSeleccionada);
            mesasActuales.remove(mesa);
            mesaSeleccionada = null;
            return;
        }
        
        // Confirmar eliminación
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
        //confirmación de guardar cambios (si los hay).
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
            // Deseleccionar al salir del modo edición
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
        
        // Extraer objetos JSON de nivel superior (secciones) del array
        List<String> objetosSecciones = extraerObjetosDelArray(contenido);
        
        for (String objetoJson : objetosSecciones) {
            SeccionDto seccion = parsearSeccion(objetoJson);
            
            if (seccion != null) {
                // Mostrar nombre y tipo de sección
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
        // Deseleccionar sección anterior
        if (seccionSeleccionada != null) {
            seccionSeleccionada.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px;");
        }
        
        // Seleccionar nueva sección
        seccionSeleccionada = boton;
        boton.setStyle("-fx-background-color: #FFA726; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        // Habilitar botón de agregar mesa
        btnAddTable.setDisable(false);
        
        // Cargar mesas de la sección
        loadMesas(seccion);
    }
    
    /**
     * Extrae objetos JSON de primer nivel de un array JSON
     */
    private List<String> extraerObjetosDelArray(String jsonArray) {
        List<String> objetos = new ArrayList<>();
        
        if (jsonArray == null || !jsonArray.trim().startsWith("[")) {
            return objetos;
        }
        
        int nivel = 0;
        int inicioObjeto = -1;
        
        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);
            
            if (c == '{') {
                if (nivel == 0) {
                    inicioObjeto = i;
                }
                nivel++;
            } else if (c == '}') {
                nivel--;
                if (nivel == 0 && inicioObjeto != -1) {
                    objetos.add(jsonArray.substring(inicioObjeto, i + 1));
                    inicioObjeto = -1;
                }
            }
        }
        
        return objetos;
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
        
        sectionPane.getChildren().clear();
        mesaButtonMap.clear();
        mesasActuales.clear();
        mesaSeleccionada = null;
        
        String contenido = (String) respuesta.getResultado("Mesas");
        if (contenido == null || contenido.equals("[]")) {
            return;
        }
        
        // Extraer objetos JSON de nivel superior (mesas) del array
        List<String> objetosMesas = extraerObjetosDelArray(contenido);
        
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
        
        // Color según estado
        String color = switch (mesa.getEstado()) {
            case "LIBRE" -> "#4CAF50"; // Verde
            case "OCUPADA" -> "#F44336"; // Rojo
            case "RESERVADA" -> "#FF9800"; // Naranja
            case "FUERA_SERVICIO" -> "#9E9E9E"; // Gris
            default -> "#2196F3"; // Azul
        };
        
        btnMesa.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 16px;");
        
        // Posicionar
        if (mesa.getPosicionX() != null && mesa.getPosicionY() != null) {
            btnMesa.relocate(mesa.getPosicionX(), mesa.getPosicionY());
        }
        
        // Drag & Drop para mover
        btnMesa.setOnDragDetected(e -> {
            if (modoEdicion) {
                Dragboard db = btnMesa.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("mesa");
                db.setContent(content);
            }
            e.consume();
        });
        
        sectionPane.setOnDragOver(e -> {
            if (e.getGestureSource() != sectionPane && modoEdicion) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });
        
        sectionPane.setOnDragDropped(e -> {
            if (modoEdicion) {
                double x = e.getX() - btnMesa.getWidth() / 2;
                double y = e.getY() - btnMesa.getHeight() / 2;
                btnMesa.relocate(x, y);
                
                // Actualizar posición en DTO
                mesa.setPosicionX(x);
                mesa.setPosicionY(y);
                hayCambios = true;
                
                // Guardar inmediatamente la nueva posición
                guardarPosicionMesa(mesa);
                
                e.setDropCompleted(true);
            }
            e.consume();
        });
        
        // Click para seleccionar
        btnMesa.setOnMouseClicked(e -> {
            if (modoEdicion && e.getButton() == MouseButton.PRIMARY) {
                if (mesaSeleccionada != null) {
                    // Deseleccionar anterior
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
        // Guardar solo la mesa que se movió
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
}
