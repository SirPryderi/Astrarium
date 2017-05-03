package fx.components;

import astrarium.Orbit;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.skin.ComboBoxBaseSkin;
import fx.modals.OrbitModal;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TextField;

/**
 * Created on 11/04/2017.
 *
 * @author Vittorio
 */
public class OrbitFieldSkin extends ComboBoxBaseSkin<Orbit> {
    /**
     * The orbit field.
     */
    private final OrbitField field;

    /**
     * {@inheritDoc}
     */
    public OrbitFieldSkin(ComboBoxBase<Orbit> comboBox, ComboBoxBaseBehavior<Orbit> behavior) {
        super(comboBox, behavior);
        field = (OrbitField) comboBox;
    }

    @Override
    public Node getDisplayNode() {
        if (field.getValue() != null)
            return new TextField(field.getValue().toString());
        else
            return new TextField("[NO ORBIT]");
    }

    @Override
    public void show() {
        OrbitModal orbit = new OrbitModal();
        orbit.initOwner(field.getScene().getWindow());
        orbit.showAndWait();

        if (orbit.getResult() != null) {
            field.setValue(orbit.getResult());
        }
    }

    @Override
    public void hide() {

    }
}
