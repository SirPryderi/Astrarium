package fx.modals;

import fx.components.OrbitField;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 26/04/2017.
 *
 * @author Vittorio
 */
public class TransferModal extends Modal<Void> {
    /**
     * Field with the destination orbit.
     */
    private OrbitField orbitField;
    /**
     * Field with the date of earliest departure.
     */
    private DatePicker earliestDepartureField;

    @NotNull
    @Override
    protected String getModalTitle() {
        return "New Transfer";
    }

    @NotNull
    @Override
    protected Parent initialise() {
        GridPane gridPane = Modal.makeGridPane();

        FormGridAdapter adapter = new FormGridAdapter(gridPane);

        orbitField = new OrbitField();
        earliestDepartureField = new DatePicker();

        adapter.addToGrid("Earliest Departure", earliestDepartureField);
        adapter.addToGrid("Destination Orbit", orbitField);

        gridPane.add(makeButtons(), 0, adapter.getOffset() + 1, 2, 1);

        return gridPane;
    }

    @Nullable
    @Override
    public Void getResult() {
        System.out.println(orbitField.getValue());
        System.out.println(earliestDepartureField.getValue());
        return null;
    }
}
