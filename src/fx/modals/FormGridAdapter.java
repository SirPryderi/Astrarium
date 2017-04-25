package fx.modals;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class used to populate a grid pane with a format containing label and node in two different columns.
 * Created on 11/04/2017.
 *
 * @author Vittorio
 */
class FormGridAdapter {
    /**
     * The grid pane the adapter is attached to.
     */
    private final GridPane gridPane;
    /**
     * The starting row.
     */
    private int offset = 0;

    /**
     * Initialise the adapter and stars from row 0.
     *
     * @param gridPane the grid pane to populate.
     */
    FormGridAdapter(@NotNull GridPane gridPane) {
        this.gridPane = gridPane;
    }

    /**
     * Initialise the adapter and starts from an arbitrary row {@code offset}.
     *
     * @param gridPane the grid pane to populate.
     * @param offset   the starting row.
     */
    FormGridAdapter(@NotNull GridPane gridPane, int offset) {
        if (offset < 0)
            throw new IllegalArgumentException("Start must be greater than 0");

        this.gridPane = gridPane;
        this.offset = offset;
    }

    /**
     * Returns the last used row index.
     *
     * @return last offset.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Adds the row with label to the next available slot.
     *
     * @param label text label of the row.
     * @param node  content of the row, usually an input of some sort.
     */
    void addToGrid(@NotNull String label, @NotNull Node node) {
        Text text = new Text(label);

        gridPane.add(text, 0, offset);
        gridPane.add(node, 1, offset);

        if (node instanceof TextField)
            ((TextField) node).setPromptText(label);

        offset++;
    }

}
