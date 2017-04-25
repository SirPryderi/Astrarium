package fx.modals;

import astrarium.CelestialBody;
import fx.components.NumberTextField;
import fx.components.OrbitField;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 11/04/2017.
 *
 * @author Vittorio
 */
public class BodyModal extends Modal<CelestialBody> {
    private TextField nameField;
    private NumberTextField massField;
    private NumberTextField radiusField;
    private OrbitField orbitField;


    @NotNull
    @Override
    protected String getModalTitle() {
        return "New Celestial Body";
    }

    @NotNull
    @Override
    protected Parent initialise() {
        GridPane gridPane = Modal.makeGridPane();

        FormGridAdapter adapter = new FormGridAdapter(gridPane);

        nameField = new TextField();
        massField = new NumberTextField(10E24, 0, Double.MAX_VALUE);
        radiusField = new NumberTextField(6E6, 0, Double.MAX_VALUE);
        orbitField = new OrbitField();

        adapter.addToGrid("Name", nameField);
        adapter.addToGrid("Mass", massField);
        adapter.addToGrid("Radius", radiusField);
        adapter.addToGrid("Orbit", orbitField);

        orbitField.setPrefWidth(150);

        gridPane.add(makeButtons(), 0, adapter.getOffset() + 1, 2, 1);

        return gridPane;
    }

    @Nullable
    @Override
    public CelestialBody getResult() {
        if (accepted)
            return new CelestialBody(nameField.getText(), massField.getNumber(), radiusField.getNumber(), orbitField.getValue());
        else
            return null;
    }
}
