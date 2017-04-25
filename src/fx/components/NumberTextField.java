package fx.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Textfield implementation that accepts formatted number and stores them in a
 * BigDecimal property The user input is formatted when the focus is lost or the
 * user hits RETURN.
 *
 * @author Thomas Bolz
 * @author Vittorio
 */
public class NumberTextField extends TextField {
    /**
     * A class that allows to format and parse numbers.
     */
    private final NumberFormat numberFormat;
    /**
     * The numeric value of the field.
     */
    private ObjectProperty<Double> number = new SimpleObjectProperty<>();
    private double minimumValue = Double.NEGATIVE_INFINITY;
    private double maximumValue = Double.POSITIVE_INFINITY;

    public NumberTextField() {
        this(0);
    }

    public NumberTextField(double value) {
        this(value, DecimalFormat.getInstance());
        initHandlers();
    }

    public NumberTextField(double value, NumberFormat numberFormat) {
        super();
        this.numberFormat = numberFormat;
        numberFormat.setMaximumFractionDigits(100);
        initHandlers();
        setValue(value);
    }

    public NumberTextField(double value, double minimumValue, double maximumValue) {
        this(value);

        setMinimumValue(minimumValue);
        setMaximumValue(maximumValue);
    }

    public double getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(double minimumValue) {
        this.minimumValue = minimumValue;
    }

    public double getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(double maximumValue) {
        this.maximumValue = maximumValue;
    }

    public final double getNumber() {
        return number.get();
    }

    public final void setNumber(double value) {
        number.set(value);
    }

    public ObjectProperty<Double> numberProperty() {
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
        numberProperty().addListener((observable, oldValue, newValue) -> setText(numberFormat.format(newValue)));
    }

    public void setValue(double value) {
        setText(numberFormat.format(value));
        this.number.setValue(value);
    }

    /**
     * Tries to parse the user input to a number according to the provided NumberFormat.
     */
    private void parseAndFormatInput() {
        try {
            String input = getText();

            if (input == null || input.length() == 0) {
                if (0 > minimumValue)
                    setValue(0);
                else
                    setValue(minimumValue);
            }

            Number parsedNumber = numberFormat.parse(input);
            double newValue = parsedNumber.doubleValue();

            if (newValue > maximumValue || newValue < minimumValue)
                throw new ParseException("Value out of bounds.", 0);

            setValue(newValue);
        } catch (ParseException ex) {
            // If parsing fails keep old number
            setValue(number.get());
        }
    }
}