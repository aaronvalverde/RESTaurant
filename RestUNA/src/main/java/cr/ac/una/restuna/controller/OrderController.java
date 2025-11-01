package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.model.DetalleOrdenDto;
import cr.ac.una.restuna.model.GrupoProductoDto;
import cr.ac.una.restuna.model.OrdenDto;
import cr.ac.una.restuna.model.ParametroDto;
import cr.ac.una.restuna.model.ProductoDto;
import cr.ac.una.restuna.model.SeccionDto;
import cr.ac.una.restuna.service.ParametroService;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.BillingCalculator;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.JsonParser;
import cr.ac.una.restuna.util.Respuesta;
import cr.ac.una.restuna.util.UserSession;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class OrderController extends Controller implements Initializable {

    @FXML
    private Label lbTable;
    @FXML
    private Label lbSection;
    @FXML
    private MFXButton btnClose;
    @FXML
    private MFXTextField txfSearch;
    @FXML
    private MFXScrollPane groupsRoot;
    @FXML
    private MFXScrollPane productsRoot;
    @FXML
    private GridPane itemsGrid;
    @FXML
    private MFXTextField txfClientName;
    @FXML
    private MFXScrollPane orderRoot;
    @FXML
    private VBox orderContainer;
    @FXML
    private Label lbVAT;
    @FXML
    private Label lbServiceTax;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private MFXButton btnSave;
    @FXML
    private MFXButton btnToBill;
    @FXML
    private MFXComboBox<?> cmbSection;
    @FXML
    private MFXComboBox<?> cmbTable;
    @FXML
    private MFXComboBox<?> cmbGroups;
    @FXML
    private HBox groupsBox;
    @FXML
    private Label lbSubtotal;
    @FXML
    private Label lbTotal;
    @FXML
    private HBox billingModeBox;
    @FXML
    private MFXCheckbox cbQuickBilling;
    @FXML
    private VBox sectionModeBox;

    //al entrar desde facturación en vista principal.
    private Boolean billingMode = false;
    //al entrar desde vista de salón (drag&drop y click en mesa).
    private Boolean sectionMode = false;
    //settea el quick billing
    private Boolean quickBillingMode = false;

    private OrdenDto currentOrder;
    private List<GrupoProductoDto> groupProduct;
    private Double impIVA = 0.13;
    private Double impService = 0.10;
    private SeccionDto currentSection;
    private final ParametroService parametroService = new ParametroService();
    private java.util.Map<String, ParametroDto> parametrosMap = new java.util.HashMap<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        quickBillingMode = false;
        initBoxes();

        currentOrder = new OrdenDto();
        groupProduct = new ArrayList<>();
        
        // Cargar parámetros del sistema
        cargarParametros();

        updateTotals();
        menuGroups();
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnClose(ActionEvent event) {
        FlowController.getInstance().goHome();
    }

    @FXML
    private void onActionBtnCancel(ActionEvent event) {
        currentOrder = new OrdenDto();
        orderContainer.getChildren().clear();

    }

    @FXML
    private void onActionBtnSave(ActionEvent event) {
    }

    @FXML
    private void onActionBtnToBill(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.BILLING);
    }

    private void initBoxes() {
        if (sectionMode) {
            billingModeBox.setVisible(false);
            billingModeBox.setManaged(false);
            cbQuickBilling.setVisible(false);
            cbQuickBilling.setManaged(false);
            return;
        }
        sectionModeBox.setVisible(false);
        sectionModeBox.setManaged(false);
    }

    public void onOrderMode() {
        sectionMode = true;
    }

    public void onBillingMode() {
        billingMode = true;
    }

    @FXML
    private void onActionCbQuickBilling(ActionEvent event) {
        quickBillingMode = !quickBillingMode;
        setQuickBillingMode(quickBillingMode);
    }

    private void setQuickBillingMode(Boolean isVisible) {
        billingModeBox.setVisible(!isVisible);
        billingModeBox.setManaged(!isVisible);
    }

    private void menuGroups() {
        groupsBox.getChildren().clear();

        for (GrupoProductoDto group : groupProduct) {

            MFXButton btnGroup = new MFXButton(group.getNombre());
            btnGroup.getStyleClass().add("group-button");
            btnGroup.setOnAction(x -> showProduct(group));
            groupsBox.getChildren().add(btnGroup);

        }

    }

    private void showProduct(GrupoProductoDto group) {

        itemsGrid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (ProductoDto product : group.getProduct()) {
            MFXButton btnProduct = new MFXButton(product.getNombreCorto() + "\n$" + product.getPrecio());
            btnProduct.getStyleClass().add("group-button");
            btnProduct.setOnAction(x -> addProduct(product));
            itemsGrid.add(btnProduct, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

    }

    private void addProduct(ProductoDto product) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cr/ac/una/restuna/view/OrderItem.fxml"));
            Node itemNode = loader.load();

            OrderItemController itemController = loader.getController();
            itemController.selectProduct(product);
            itemController.setParentController(this);

            orderContainer.getChildren().add(itemNode);

            DetalleOrdenDto detalle = new DetalleOrdenDto();
            detalle.setIdProducto(product.getIdProducto());
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(product.getPrecio());
            detalle.setSubtotal(product.getPrecio());
            currentOrder.getDetalles().add(detalle);

            updateTotals();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteDetail(DetalleOrdenDto detail) {
        currentOrder.getDetalles().remove(detail);
        updateTotals();
    }

    public void updateTotals() {
        // Determinar si la sección cobra impuesto
        boolean sectionHasTax = currentSection != null && currentSection.cobraImpuesto();
        
        // Calcular usando BillingCalculator
        BillingCalculator.BillingResult result = BillingCalculator.calculateBilling(
            currentOrder.getDetalles(),
            sectionHasTax,
            parametrosMap
        );
        
        // Actualizar labels con formato de moneda
        lbSubtotal.setText(result.getFormattedSubtotal());
        lbVAT.setText(result.getFormattedIva());
        lbServiceTax.setText(result.getFormattedServiceTax());
        lbTotal.setText(result.getFormattedTotal());
    }
    
    /**
     * Cargar parámetros del sistema (impuestos y moneda)
     */
    private void cargarParametros() {
        if (!UserSession.getInstance().isAuthenticated()) {
            System.err.println("No hay usuario autenticado para cargar parámetros");
            return;
        }
        
        Long idUsuario = UserSession.getInstance().getCurrentUser().getIdUsuario();
        if (idUsuario == null) {
            System.err.println("No se pudo obtener el ID del usuario");
            return;
        }
        
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Respuesta respuesta = parametroService.getParametrosPorUsuario(idUsuario);
                
                javafx.application.Platform.runLater(() -> {
                    if (respuesta.getEstado()) {
                        String jsonArray = (String) respuesta.getResultado("Parametros");
                        procesarParametros(jsonArray);
                    } else {
                        System.err.println("Error cargando parámetros: " + respuesta.getMensaje());
                    }
                });
                
                return null;
            }
            
            @Override
            protected void failed() {
                javafx.application.Platform.runLater(() -> {
                    System.err.println("Error cargando parámetros: " + getException().getMessage());
                });
            }
        };
        
        new Thread(task).start();
    }
    
    /**
     * Procesar JSON de parámetros
     */
    private void procesarParametros(String jsonArray) {
        parametrosMap.clear();
        
        if (jsonArray == null || jsonArray.trim().isEmpty() || jsonArray.equals("[]")) {
            return;
        }
        
        // Extraer cada objeto del array JSON
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{[^{}]*\\}");
        java.util.regex.Matcher matcher = pattern.matcher(jsonArray);
        
        while (matcher.find()) {
            String objetoJson = matcher.group();
            ParametroDto parametro = parsearParametro(objetoJson);
            if (parametro != null && parametro.getClave() != null) {
                parametrosMap.put(parametro.getClave(), parametro);
            }
        }
        
        // Actualizar totales con nuevos parámetros
        updateTotals();
    }
    
    /**
     * Parsear un parámetro desde JSON
     */
    private ParametroDto parsearParametro(String objetoJson) {
        try {
            ParametroDto parametro = new ParametroDto();
            
            parametro.setIdParametro(JsonParser.extraerValorLong(objetoJson, "idParametro"));
            parametro.setIdUsuario(JsonParser.extraerValorLong(objetoJson, "idUsuario"));
            parametro.setClave(JsonParser.extraerValor(objetoJson, "clave"));
            parametro.setValor(JsonParser.extraerValor(objetoJson, "valor"));
            parametro.setDescripcion(JsonParser.extraerValor(objetoJson, "descripcion"));
            parametro.setTipoDato(JsonParser.extraerValor(objetoJson, "tipoDato"));
            
            return parametro;
        } catch (Exception e) {
            System.err.println("Error parseando parámetro: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Establecer la sección actual
     */
    public void setCurrentSection(SeccionDto section) {
        this.currentSection = section;
        updateTotals();
    }
}
