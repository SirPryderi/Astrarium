package fx.modals;

import astrarium.Spacecraft;
import fx.components.NumberTextField;
import fx.components.OrbitField;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 26/05/2017.
 *
 * @author Vittorio
 */
public class SpacecraftModal extends Modal<Spacecraft> {
    /**
     * Field with the name of the spacecraft.
     */
    private TextField nameField;
    /**
     * Field with the dry mass of the spacecraft.
     */
    private NumberTextField massField;
    /**
     * Field with the mass of the payload.
     */
    private NumberTextField payloadMassField;
    /**
     * Field with the mass of the propellant.
     */
    private NumberTextField fuelMassField;
    /**
     * Field with the maximum mass capacity of the fuel tanks.
     */
    private NumberTextField maxFuelMassField;
    /**
     * Field with the maximum width of the spacecraft.
     */
    private NumberTextField widthField;
    /**
     * Field with the maximum height of the spacecraft.
     */
    private NumberTextField heightField;
    /**
     * Field with the specific impulse of the spacecraft.
     */
    private NumberTextField specificImpulseField;
    /**
     * Field with the initial orbit of the spacecraft.
     */
    private OrbitField orbitField;

    @NotNull
    @Override
    protected String getModalTitle() {
        return "New spacecraft";
    }

    @NotNull
    @Override
    protected Parent initialise() {
        GridPane gridPane = Modal.makeGridPane();

        FormGridAdapter adapter = new FormGridAdapter(gridPane);

        nameField = new TextField();
        massField = new NumberTextField(0, 0, Double.POSITIVE_INFINITY);
        payloadMassField = new NumberTextField(0, 0, Double.POSITIVE_INFINITY);
        fuelMassField = new NumberTextField(0, 0, Double.POSITIVE_INFINITY);
        maxFuelMassField = new NumberTextField(0, 0, Double.POSITIVE_INFINITY);
        widthField = new NumberTextField(0, 0, Double.POSITIVE_INFINITY);
        heightField = new NumberTextField(0, 0, Double.POSITIVE_INFINITY);
        specificImpulseField = new NumberTextField(0, 0, Double.POSITIVE_INFINITY);
        orbitField = new OrbitField();

        adapter.addToGrid("Name", nameField);
        adapter.addToGrid("Dry Mass", massField);
        adapter.addToGrid("Payload Mass", payloadMassField);
        adapter.addToGrid("Fuel Mass", fuelMassField);
        adapter.addToGrid("Max Fuel Mass", maxFuelMassField);
        adapter.addToGrid("Width", widthField);
        adapter.addToGrid("Height", heightField);
        adapter.addToGrid("Specific Impulse", specificImpulseField);
        adapter.addToGrid("Orbit", orbitField);

        orbitField.setPrefWidth(250);

        gridPane.add(makeButtons(), 0, adapter.getOffset() + 1, 2, 1);

        return gridPane;
    }

    @Nullable
    @Override
    public Spacecraft getResult() {
        return new Spacecraft(
                nameField.getText(),
                massField.getNumber(),
                heightField.getHeight(),
                widthField.getWidth(),
                orbitField.getValue());
    }
}
