package fx.components;

import astrarium.Astrarium;
import astrarium.CelestialBody;
import fx.MainController;
import javafx.scene.control.ComboBox;

/**
 * Custom component that creates a combo box with all the
 * <p>
 * Created on 11/04/2017.
 *
 * @author Vittorio
 */
@SuppressWarnings("WeakerAccess")
public class BodiesComboBox extends ComboBox<CelestialBody> {
    /**
     * Creates a combo box with the given {@code root} and all its children.
     *
     * @param root First element of the system.
     */
    public BodiesComboBox(CelestialBody root) {
        // TODO better graph traversal.
        // TODO Children indentation
        this.getItems().addAll(root);
        this.getItems().addAll(root.getChildren());
    }

    /**
     * Creates a combo box with all the {@link CelestialBody}s in an {@link Astrarium}.
     *
     * @param astrarium the astrarium to use as data-source.
     */
    public BodiesComboBox(Astrarium astrarium) {
        this(astrarium.getRoot());
    }

    /**
     * Attempts to create a combo box using the static {@link MainController#astrarium}.
     */
    public BodiesComboBox() {
        this(MainController.astrarium);
    }

}
