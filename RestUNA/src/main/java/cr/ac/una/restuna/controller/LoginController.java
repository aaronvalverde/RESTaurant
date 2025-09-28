package cr.ac.una.restuna.controller;

import cr.ac.una.restuna.util.AppKeys;
import cr.ac.una.restuna.util.FlowController;
import cr.ac.una.restuna.util.Format;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class LoginController extends Controller implements Initializable {

    @FXML
    private VBox loginRoot;
    @FXML
    private MFXTextField txfUser;
    @FXML
    private MFXPasswordField pswPassword;
    @FXML
    private MFXButton btnSignIn;

    @Override
    public void initialize() {}

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txfUser.delegateSetTextFormatter(Format.getInstance().lettersFormat(20));
        pswPassword.delegateSetTextFormatter(Format.getInstance().lettersFormat(20));
    }

    @FXML
    private void onKeyPressedPswPassword(KeyEvent event) {
    }

    @FXML
    private void onActionBtnSignIn(ActionEvent event) {
        FlowController.getInstance().goView(AppKeys.MAIN);
    }

}
