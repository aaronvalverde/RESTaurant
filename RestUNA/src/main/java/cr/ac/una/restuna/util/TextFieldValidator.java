package cr.ac.una.restuna.util;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.input.KeyEvent;

/**
 * Utility class for text field validation
 * 
 * @author gambo
 */
public class TextFieldValidator {

    /**
     * Allows only letters, spaces, and common punctuation
     */
    public static void addTextOnlyValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            // Allow letters (a-z, A-Z), spaces, accented characters, and common punctuation
            if (!character.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s.,'-]")) {
                event.consume();
            }
        });
    }

    /**
     * Allows only numbers (integers)
     */
    public static void addIntegerOnlyValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            
            // Allow only digits
            if (!character.matches("[0-9]")) {
                event.consume();
            }
        });
    }

    /**
     * Allows only decimal numbers (with dot as separator)
     */
    public static void addDecimalOnlyValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            String currentText = textField.getText();
            
            // Allow digits and one decimal point
            if (!character.matches("[0-9.]")) {
                event.consume();
                return;
            }
            
            // Prevent multiple decimal points
            if (character.equals(".") && currentText.contains(".")) {
                event.consume();
            }
        });
    }

    /**
     * Allows only alphanumeric characters (letters and numbers)
     */
    public static void addAlphanumericOnlyValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            // Allow letters, numbers, and underscore
            if (!character.matches("[a-zA-Z0-9_]")) {
                event.consume();
            }
        });
    }

    /**
     * Allows email format characters
     */
    public static void addEmailValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            // Allow letters, numbers, @, dot, underscore, hyphen
            if (!character.matches("[a-zA-Z0-9@._-]")) {
                event.consume();
            }
        });
    }

    /**
     * Allows phone number format characters
     */
    public static void addPhoneValidation(MFXTextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            // Allow digits, +, -, (, ), and spaces
            if (!character.matches("[0-9+\\-() ]")) {
                event.consume();
            }
        });
    }

    /**
     * Limits the maximum length of text input
     */
    public static void addMaxLengthValidation(MFXTextField textField, int maxLength) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (textField.getText().length() >= maxLength) {
                event.consume();
            }
        });
    }
}
