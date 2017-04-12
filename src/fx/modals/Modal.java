package fx.modals;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract class used to ease the creation of input modals.
 * Created on 11/04/2017.
 *
 * @author Vittorio
 */
public abstract class Modal<T> extends Stage {
    /**
     * A flat used to specify whether the form was successfully validated and okay was pressed.
     */
    protected boolean accepted = false;

    /**
     * Creates a new modal with default modality and icons.
     */
    public Modal() {
        this.setTitle(getModalTitle());
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);
        this.getIcons().add(new Image("icons/favicon-60.png"));

        this.setScene(new Scene(initialise()));

        this.sizeToScene();
    }

    /**
     * Utility method to create a {@link GridPane} with default parameters.
     *
     * @return initialised {@link GridPane}
     */
    protected static GridPane makeGridPane() {
        GridPane gridPane = new GridPane();

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        return gridPane;
    }

    /**
     * Utility method to create a {@link BorderPane} containing a {@code cancel} and {@code okay} buttons with events.
     *
     * @return okay and cancel buttons.
     */
    protected BorderPane makeButtons() {
        BorderPane pane = new BorderPane();

        Button cancel = new Button("Cancel");
        Button okay = new Button("Okay");

        okay.setPrefWidth(75);
        cancel.setPrefWidth(75);

        pane.setRight(okay);
        pane.setLeft(cancel);

        cancel.setOnAction(event -> this.close());
        okay.setOnAction(event -> {
            this.accepted = true;
            this.close();
        });

        return pane;
    }

    /**
     * Returns the title of the modal.
     *
     * @return title.
     */
    @NotNull
    protected abstract String getModalTitle();

    /**
     * Creates the content of the modal.
     *
     * @return modal content.
     */
    @NotNull
    protected abstract Parent initialise();

    /**
     * Returns the data obtained from the modal if {@code okay} was pressed,
     * {@code null} otherwise.
     *
     * @return results of the modal or {@code null}.
     */
    @Nullable
    public abstract T getResult();
}
