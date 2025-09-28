package cr.ac.una.restuna.util;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.regex.Pattern;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;


public class Format {
    private static Format INSTANCE = null;

    public DateTimeFormatter formatDateShort = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    public DateTimeFormatter formatDateMedium = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    public DecimalFormat decimalFormat = new DecimalFormat("#,###,###,##0.00");

    private Format() {
    }

    private static void createInstance() {
        if (INSTANCE == null) {
            synchronized (Format.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Format();
                }
            }
        }
    }

    public static Format getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public TextFormatter twoDecimalFormat() {
        TextFormatter numericFormat = new TextFormatter<>(c
                -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            if (c.getControlNewText().contains(",")) {
                ParsePosition parsePosition = new ParsePosition(0);
                Object object = decimalFormat.parse(c.getControlNewText(), parsePosition);

                if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                    return null;
                } else {
                    Pattern validDoubleText = Pattern.compile("^[0-9]*+(\\.[0-9]{0,2})?$");
                    if (validDoubleText.matcher(c.getControlNewText().replace(",", "")).matches()) {
                        return c;
                    } else {
                        return null;
                    }
                }
            } else {
                Pattern validDoubleText = Pattern.compile("^[0-9]*+(\\.[0-9]{0,2})?$");
                if (validDoubleText.matcher(c.getControlNewText().replace(",", "")).matches()) {
                    return c;
                } else {
                    return null;
                }
            }
        });
        return numericFormat;
    }

    public TextFormatter integerFormat() {
        TextFormatter numericFormat = new TextFormatter<>(c
                -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }

            Pattern validDoubleText = Pattern.compile("\\d+");
            if (validDoubleText.matcher(c.getControlNewText()).matches()) {
                return c;
            } else {
                return null;
            }
        });
        return numericFormat;
    }

    public TextFormatter idFormat(Integer maxLength) {
        TextFormatter<String> cedulaFormat = new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            if (maxLength > 0) {
                if (((TextInputControl) c.getControl()).getLength() >= maxLength && !c.isDeleted()) {
                    return null;
                }
                if (c.getText().length() > maxLength && !c.isDeleted()) {
                    return null;
                }
            }
            c.setText(c.getText().replaceAll("[^a-zA-Z0-9-]", ""));
            if(c.getControlNewText().matches(".*-{2,}.*")){
                return null;
            }
            return c;

        });
        return cedulaFormat;
    }

    public TextFormatter lettersFormat(Integer maxLength) {
        TextFormatter<String> letrasFormat = new TextFormatter<>(c -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }
            if (maxLength > 0) {
                if (((TextInputControl) c.getControl()).getLength() >= maxLength && !c.isDeleted()) {
                    return null;
                }
                if (c.getText().length() > maxLength && !c.isDeleted()) {
                    return null;
                }
            }
            
            if(c.getControlNewText().matches(".*[^a-zA-ZáéíóúÁÉÍÓÚñÑ ].*")){
                return null;
            }
            if(c.getControlNewText().matches(".*\\s{2,}.*")){
                return null;
            }
            return c;

        });
        return letrasFormat;
    }

    public TextFormatter maxLengthFormat(Integer length) {
        TextFormatter maxLengthFormat = new TextFormatter<>(c
                -> {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }

            if (((TextInputControl) c.getControl()).getLength() >= length && !c.isDeleted()) {
                return null;
            }
            if (c.getText().length() > length && !c.isDeleted()) {
                return null;
            }
            return c;
        });
        return maxLengthFormat;
    }
}