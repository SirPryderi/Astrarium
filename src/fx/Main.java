package fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class for running the JavaFX UI application implementing a view for the Astrarium model.
 * <p>
 * Created on 04-Nov-16.
 *
 * @author Vittorio
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Astrarium - Mission Planner");
        primaryStage.getIcons().add(new Image("icons/favicon-60.png"));

        primaryStage.show();
    }

}
