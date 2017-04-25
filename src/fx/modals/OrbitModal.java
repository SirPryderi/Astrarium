package fx.modals;

import astrarium.CelestialBody;
import astrarium.Orbit;
import fx.components.BodiesComboBox;
import fx.components.NumberTextField;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 11/04/2017.
 *
 * @author Vittorio
 */
public class OrbitModal extends Modal<Orbit> {
    /**
     * The combo box box used to select the reference body.
     */
    private BodiesComboBox referenceBodyField;
    /**
     * The field used to select the semi-major axis of the orbit.
     */
    private NumberTextField semiMajorAxisField;
    /**
     * The field used to select the eccentricity of the orbit.
     */
    private NumberTextField eccentricityField;
    /**
     * The field used to select the inclination of the orbit.
     */
    private NumberTextField inclinationField;
    /**
     * The field used to select the longitude of the ascending node of the orbit.
     */
    private NumberTextField longitudeOfAscendingNodeField;
    /**
     * The field used to select the argument of the periapsis of the orbit.
     */
    private NumberTextField argumentOfPeriapsisField;
    /**
     * The field used to select the mean anomaly at the time 0 of the simulation.
     */
    private NumberTextField meanAnomalyAtEpochField;

    @NotNull
    @Override
    protected String getModalTitle() {
        return "New Orbit";
    }

    @NotNull
    @Override
    protected Parent initialise() {
        GridPane gridPane = Modal.makeGridPane();

        FormGridAdapter adapter = new FormGridAdapter(gridPane);

        referenceBodyField = new BodiesComboBox();

        semiMajorAxisField = new NumberTextField(1e11, 0, Double.POSITIVE_INFINITY);
        eccentricityField = new NumberTextField(0, 0, 0.9999);
        inclinationField = new NumberTextField();
        inclinationField = new NumberTextField();
        longitudeOfAscendingNodeField = new NumberTextField();
        argumentOfPeriapsisField = new NumberTextField();
        meanAnomalyAtEpochField = new NumberTextField();

        adapter.addToGrid("Reference Body", referenceBodyField);
        adapter.addToGrid("Semi-major Axis", semiMajorAxisField);
        adapter.addToGrid("Eccentricity", eccentricityField);
        adapter.addToGrid("Inclination", inclinationField);
        adapter.addToGrid("Longitude of Ascending Node", longitudeOfAscendingNodeField);
        adapter.addToGrid("Argument of Periapsis", argumentOfPeriapsisField);
        adapter.addToGrid("Mean Anomaly at Epoch", meanAnomalyAtEpochField);

        referenceBodyField.setPrefWidth(150);

        //gridPane.add(new Separator(), 0, adapter.getOffset(), 2, 1);

        gridPane.add(makeButtons(), 0, adapter.getOffset() + 1, 2, 1);

        return gridPane;
    }

    @Override
    @Nullable
    public Orbit getResult() {
        if (accepted) {
            CelestialBody body = referenceBodyField.getSelectionModel().getSelectedItem();
            return new Orbit(
                    body,
                    semiMajorAxisField.getNumber(),
                    eccentricityField.getNumber(),
                    inclinationField.getNumber(),
                    longitudeOfAscendingNodeField.getNumber(),
                    argumentOfPeriapsisField.getNumber(),
                    meanAnomalyAtEpochField.getNumber()
            );
        } else {
            return null;
        }

    }
}
