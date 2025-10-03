/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class UsersMgmtController extends Controller implements Initializable {

    @FXML
    private MFXTextField txfSearch;
    @FXML
    private MFXFilterComboBox<String> cmbRole;
    @FXML
    private MFXFilterComboBox<String> cmbStatus;
    @FXML
    private MFXButton btnAdd;
    @FXML
    private TreeTableColumn<UserRow, String> tbcName;
    @FXML
    private TreeTableColumn<UserRow, String> tbcUser;
    @FXML
    private TreeTableColumn<UserRow, String> tbcEmail;
    @FXML
    private TreeTableColumn<UserRow, String> tbcRole;
    @FXML
    private TreeTableColumn<UserRow, String> tbcStatus;
    @FXML
    private JFXTreeTableView<UserRow> tbvUsers;
    @FXML
    private MFXScrollPane tableRoot;

    private final ObservableList<UserRow> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tbvUsers.prefHeightProperty().bind(tableRoot.heightProperty());
        tbvUsers.prefWidthProperty().bind(tableRoot.widthProperty());

        cmbRole.getItems().addAll("Cajero", "Administrador", "Salonero");
        cmbStatus.getItems().addAll("Activo", "Inactivo");

        tbcName.setCellValueFactory(x -> x.getValue().getValue().getName());
        tbcUser.setCellValueFactory(x -> x.getValue().getValue().getUsername());
        tbcEmail.setCellValueFactory(x -> x.getValue().getValue().getEmail());
        tbcRole.setCellValueFactory(x -> x.getValue().getValue().getRole());
        tbcStatus.setCellValueFactory(x -> x.getValue().getValue().getStatus());

        TreeItem<UserRow> root = new RecursiveTreeItem<>(userList, RecursiveTreeObject::getChildren);
        tbvUsers.setRoot(root);
        tbvUsers.setShowRoot(false);

        txfSearch.textProperty().addListener((obs, oldVal, newVal) -> filters());
        cmbRole.valueProperty().addListener((obs, oldVal, newVal) -> filters());
        cmbStatus.valueProperty().addListener((obs, oldVal, newVal) -> filters());

    }

    @Override
    public void initialize() {

    }

    @FXML
    private void onActionBtnAdd(ActionEvent event) {
        FlowController.getInstance().goViewInWindowModal(AppKeys.NEW_USER, new Stage(), false);
    }

    public void addUser(String name, String username, String email, String role, String status) {

        for (UserRow user : userList) {

            if (user.getUsername().get().equalsIgnoreCase(username)) {

                showMessage("Usuario ya existente: " + username);
                return;

            }

        }

        userList.add(new UserRow(name, username, email, role, status));
        filters();
    }

    private void filters() {
 
        String search = txfSearch.getText() == null ? "" : txfSearch.getText().toLowerCase();
        String filterRol = cmbRole.getValue();
        String filterStatus = cmbStatus.getValue();
        
        
        ObservableList<UserRow> filter = userList.filtered(x -> 
        x.getName().get().toLowerCase().contains(search)
        || x.getUsername().get().toLowerCase().contains(search)
        || x.getEmail().get().toLowerCase().contains(search)
        ).filtered(f -> filterRol == null || filterRol.isEmpty() || f.getRole().get().equals(filterRol)
        ).filtered(s -> filterStatus == null || filterStatus.isEmpty()|| s.getStatus().get().equals(filterStatus));
        
        
        TreeItem<UserRow> root = new RecursiveTreeItem<>(filter, RecursiveTreeObject::getChildren);
        tbvUsers.setRoot(root);
        tbvUsers.setShowRoot(false);
        
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
