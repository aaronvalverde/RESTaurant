/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.controller.Controller;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.UserSession;
import io.github.palexdev.materialfx.controls.MFXButton;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class MainController extends Controller implements Initializable {

    @FXML
    private HBox topbar;
    @FXML
    private MFXButton btnLogout;
    @FXML
    private MFXButton btnSections;
    @FXML
    private MFXButton btnOrders;
    @FXML
    private MFXButton btnBilling;
    @FXML
    private MFXButton btnCashClosing;
    private MFXButton btnUsers;
    private MFXButton btnSectionsMgmt;
    private MFXButton btnMenuGroups;
    private MFXButton btnMenuItems;
    @FXML
    private MFXButton btnReports;
    @FXML
    private MFXButton btnSettings;
    @FXML
    private BorderPane mainLayout;
    @FXML
    private BorderPane contentArea;
    private WebView wvLogo;
    @FXML
    private MFXButton btnCashOpening;
    @FXML
    private MFXButton btnManagement;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize() {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar el contentArea directamente, no su padre
        FlowController.getInstance().setContentArea(contentArea);

        // Configurar WebView del logo si está disponible
        if (wvLogo != null) {
            try {
                WebEngine webEngine = wvLogo.getEngine();
                String svgPath = getClass()
                        .getResource("/cr/ac/una/restuna/resources/Logo_beany.svg")
                        .toExternalForm();

                String html
                        = "<html>"
                        + "<body style='background:#faf4f0; display:flex; justify-content:center; align-items:center; height:100vh; margin:0;'>"
                        + "<img src='" + svgPath + "' style='transform: scale(3.5); transform-origin: center; width:auto; height:auto;'/>"
                        + "</body>"
                        + "</html>";

                webEngine.loadContent(html);
            } catch (Exception e) {
                System.err.println("Error loading logo WebView: " + e.getMessage());
            }
        }

        // Aplicar permisos basados en rol del usuario
        aplicarPermisosDeRol();
    }

    /**
     * Aplica permisos basados en el rol del usuario autenticado Oculta/muestra
     * botones según los permisos del rol
     */
    private void aplicarPermisosDeRol() {
        UserSession session = UserSession.getInstance();

        if (!session.isAuthenticated()) {
            // Si no hay usuario autenticado, ocultar todo
            ocultarTodosLosBotones();
            return;
        }

        // Mostrar/ocultar botones según permisos
        configurarVisibilidadBoton(btnSections, session.canAccessSalones());
        configurarVisibilidadBoton(btnOrders, session.canAccessOrdenes());
        configurarVisibilidadBoton(btnBilling, session.canAccessFacturacion());
        configurarVisibilidadBoton(btnCashClosing, session.canAccessCierreCaja());

        // Botones de mantenimiento (solo administradores)
        configurarVisibilidadBoton(btnUsers, session.canAccessMantenimientos());
        configurarVisibilidadBoton(btnSectionsMgmt, session.canAccessMantenimientos());
        configurarVisibilidadBoton(btnMenuGroups, session.canAccessMantenimientos());
        configurarVisibilidadBoton(btnMenuItems, session.canAccessMantenimientos());

        // Botones del sistema (solo administradores)
        configurarVisibilidadBoton(btnReports, session.canAccessReportes());
        configurarVisibilidadBoton(btnSettings, session.canAccessConfiguracion());

        // El botón de logout siempre debe estar visible
        configurarVisibilidadBoton(btnLogout, true);
    }

    /**
     * Configura la visibilidad y habilitación de un botón
     */
    private void configurarVisibilidadBoton(MFXButton boton, boolean permitido) {
        if (boton != null) {
            boton.setVisible(permitido);
            boton.setManaged(permitido);
            boton.setDisable(!permitido);
        }
    }

    /**
     * Oculta todos los botones del menú
     */
    private void ocultarTodosLosBotones() {
        configurarVisibilidadBoton(btnSections, false);
        configurarVisibilidadBoton(btnOrders, false);
        configurarVisibilidadBoton(btnBilling, false);
        configurarVisibilidadBoton(btnCashClosing, false);
        configurarVisibilidadBoton(btnUsers, false);
        configurarVisibilidadBoton(btnSectionsMgmt, false);
        configurarVisibilidadBoton(btnMenuGroups, false);
        configurarVisibilidadBoton(btnMenuItems, false);
        configurarVisibilidadBoton(btnReports, false);
        configurarVisibilidadBoton(btnSettings, false);
        configurarVisibilidadBoton(btnLogout, true); // Logout siempre visible
    }

    @FXML
    private void onActionBtnSignOut(ActionEvent event) {
        // Limpiar sesión del usuario
        UserSession.getInstance().clearSession();

        FlowController.getInstance().goMain(AppKeys.LOGIN);
    }

    @FXML
    private void onActionBtnSections(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.SECTIONS);
    }

    @FXML
    private void onActionBtnOrders(ActionEvent event) {
    }

    @FXML
    private void onActionBtnBilling(ActionEvent event) {
    }

    @FXML
    private void onActionBtnCashClosing(ActionEvent event) {
        FlowController.getInstance().goViewInWindowModal(AppKeys.CASH_CLOSING, new Stage(), false);
    }

    private void onActionBtnUsers(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.USERS_MGMT);
    }

    private void onActionBtnSectionsMgmt(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.SECTIONS_MGMT);
    }

    private void onActionBtnMenuGroups(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MENU_GROUPS_MGMT);
    }

    private void onActionBtnMenuItems(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MENU_ITEMS_MGMT);
    }

    @FXML
    private void onActionBtnReports(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.REPORTS);
    }

    @FXML
    private void onActionBtnSettings(ActionEvent event) {
    }

    @FXML
    private void onActionBtnCashOpening(ActionEvent event) {
        FlowController.getInstance().goViewInWindowModal(AppKeys.CASH_OPENING, new Stage(), false);
    }

    @FXML
    private void onActionBtnManagement(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MANAGEMENT);
    }
}
