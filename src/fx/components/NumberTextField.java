package fx.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Textfield implementation that accepts formatted number and stores them in a
 * BigDecimal property The user input is formatted when the focus is lost or the
 * user hits RETURN.
 *
 * @author Thomas Bolz
 */
public class NumberTextField extends TextField {
    /**
     * A class that allows to format and parse numbers.
     */
    private final NumberFormat numberFormat;
    /**
     * The numeric value of the field.
     */
    private ObjectProperty<BigDecimal> number = new SimpleObjectProperty<>();

    public NumberTextField() {
        this(BigDecimal.ZERO);
    }

    public NumberTextField(BigDecimal value) {
        this(value, DecimalFormat.getInstance());
        initHandlers();
    }

    public NumberTextField(BigDecimal value, NumberFormat numberFormat) {
        super();
        this.numberFormat = numberFormat;
        initHandlers();
        setNumber(value);
    }

    public final BigDecimal getNumber() {
        return number.get();
    }

    public final void setNumber(BigDecimal value) {
        number.set(value);
    }

    public ObjectProperty<BigDecimal> numberProperty() {
        return number;
    }

    private void initHandlers() {

        // try to parse when focus is lost or RETURN is hit
        setOnAction(arg0 -> parseAndFormatInput());

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                parseAndFormatInput();
            }
        });

        // Set text in field if BigDecimal property is changed from outside.
        numberProperty().addListener((obserable, oldValue, newValue) -> setText(numberFormat.format(newValue)));
    }

    /**
     * Tries to parse the user input to a number according to the provided NumberFormat.
     */
    private void parseAndFormatInput() {
        try {
            // TODO Better validation must be enforced
            // TODO Longer precision
            String input = getText();
            if (input == null || input.length() == 0) {
                return;
            }
            Number parsedNumber = numberFormat.parse(input);
            BigDecimal newValue = new BigDecimal(parsedNumber.toString());
            setNumber(newValue);
            selectAll();
        } catch (ParseException ex) {
            // If parsing fails keep old number
            setText(numberFormat.format(number.get()));
        }
    }
}