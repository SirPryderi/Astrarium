package fx.modals;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 11/04/2017.
 *
 * @author Vittorio
 */
public abstract class Modal<T> extends Stage {
    public Modal() {
        this.setTitle(getModalTitle());
        this.initModality(Modality.APPLICATION_MODAL);
        this.setResizable(false);
        this.getIcons().add(new Image("icons/favicon-60.png"));

        this.setScene(new Scene(initialise()));

        this.sizeToScene();
    }

    @NotNull
    protected abstract String getModalTitle();

    @NotNull
    protected abstract Parent initialise();

    @Nullable
    public abstract T getResult();
}
