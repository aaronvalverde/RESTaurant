package cr.ac.una.restuna.util;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.input.KeyEvent;


public class TextFieldValidator {

    
    public static void addTextOnlyValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            
            if (!character.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s.,'-]")) {
                event.consume();
            }
        });
    }

    
    public static void addIntegerOnlyValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            
            
            if (!character.matches("[0-9]")) {
                event.consume();
            }
        });
    }

    
    public static void addDecimalOnlyValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            String currentText = textField.getText();
            
            
            if (!character.matches("[0-9.]")) {
                event.consume();
                return;
            }
            
            
            if (character.equals(".") && currentText.contains(".")) {
                event.consume();
            }
        });
    }

    
    public static void addAlphanumericOnlyValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            
            if (!character.matches("[a-zA-Z0-9_]")) {
                event.consume();
            }
        });
    }

    
    public static void addEmailValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            
            if (!character.matches("[a-zA-Z0-9@._-]")) {
                event.consume();
            }
        });
    }

    
    public static void addPhoneValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            
            if (!character.matches("[0-9+\\-() ]")) {
                event.consume();
            }
        });
    }

    
    public static void addMaxLengthValidation(MFXTextField textField, int maxLength) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (textField.getText().length() >= maxLength) {
                event.consume();
            }
        });
    }
}
