/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.restuna.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class AlertController implements Initializable {

    @FXML
    private Label lbAlertTitle;
    @FXML
    private MFXButton btnClose;
    @FXML
    private ImageView imvAlertGraphic;
    @FXML
    private Label lbAlertInfo;
    @FXML
    private Label lbAlertDescription;
    @FXML
    private MFXButton btnOk;
    @FXML
    private MFXButton btnYes;
    @FXML
    private MFXButton btnNo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onActionBtnClose(ActionEvent event) {
    }


    @FXML
    private void onActionBtnOk(ActionEvent event) {
    }

    @FXML
    private void onActionBtnYes(ActionEvent event) {
    }

    @FXML
    private void onActionBtnNo(ActionEvent event) {
    }
    
    private void setUpData(/*alert.type*/){}
            
}
