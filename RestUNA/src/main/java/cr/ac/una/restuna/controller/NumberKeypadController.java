package cr.ac.una.restuna.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author aaron
 */
public class NumberKeypadController extends Controller implements Initializable {
    
    @FXML
    private AnchorPane root;
    @FXML
    private MFXButton btnSeven;
    @FXML
    private MFXButton btnEight;
    @FXML
    private MFXButton btnNine;
    @FXML
    private MFXButton btnFour;
    @FXML
    private MFXButton btnFive;
    @FXML
    private MFXButton btnSix;
    @FXML
    private MFXButton btnOne;
    @FXML
    private MFXButton btnTwo;
    @FXML
    private MFXButton btnThree;
    @FXML
    private MFXButton btnDot;
    @FXML
    private MFXButton btnZero;
    @FXML
    private MFXButton btnErase;
    //no forma parte de la vista, pero se recibe para actualizar su contenido en la vista padre.
    private MFXTextField txfInput;
    //raíz en la vista a la cual es invocado.
    private VBox newRoot;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        root.prefHeightProperty().bind(newRoot.heightProperty());
//        root.prefWidthProperty().bind(newRoot.widthProperty());
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void onActionBtnSeven(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("7");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("7");
    }

    @FXML
    private void onActionBtnEight(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("8");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("8");
    }

    @FXML
    private void onActionBtnNine(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("9");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("9");
    }

    @FXML
    private void onActionBtnFour(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("4");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("4");
    }

    @FXML
    private void onActionBtnFive(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("5");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("5");
    }

    @FXML
    private void onActionBtnSix(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("6");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("6");
    }

    @FXML
    private void onActionBtnOne(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("1");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("1");
    }

    @FXML
    private void onActionBtnTwo(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("2");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("2");
    }

    @FXML
    private void onActionBtnThree(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("3");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("3");
    }

    @FXML
    private void onActionBtnDot(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat(".");
            txfInput.setText(number);
            return;
        }
        txfInput.setText(".");
    }

    @FXML
    private void onActionBtnZero(ActionEvent event) {
        if (!txfInput.getText().isBlank() || !txfInput.getText().isEmpty()) {
            String number = txfInput.getText().concat("0");
            txfInput.setText(number);
            return;
        }
        txfInput.setText("0");
    }

    @FXML
    private void onActionBtnErase(ActionEvent event) {
        String number = txfInput.getText().substring(0, txfInput.getText().length()-1);
        txfInput.setText(number);
    }
    
    public void setRoot(VBox newRoot){
        this.newRoot = newRoot;
    }
    
    public void setInputRoot(MFXTextField txfInput){
        this.txfInput = txfInput;
    }
}
