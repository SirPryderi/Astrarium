package fx;

import astrarium.Astrarium;
import astrarium.CelestialBody;
import astrarium.Orbit;
import astrarium.utils.Vector;
import com.google.gson.JsonParseException;
import fx.components.SpaceCanvas;
import fx.modals.BodyModal;
import fx.modals.Modal;
import io.JsonHub;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

import static astrarium.utils.Mathematics.TWO_PI;
import static java.lang.Math.random;

/**
 * Controller for the view of the main window.
 * <p>
 * Created on 06/03/2017.
 *
 * @author Vittorio
 */
public class MainController {
    /**
     * The model of the application. Is made static to be accessed by other components in a singleton-like fashion.
     */
    public static Astrarium astrarium;

    //region FXML Nodes
    /**
     * The menu bar.
     */
    @FXML
    public MenuBar menu;
    /**
     * The main grid of the UI.
     */
    @FXML
    public HBox grid;
    /**
     * The {@link SpaceCanvas} that contains the system map.
     */
    @FXML
    public SpaceCanvas canvas;
    /**
     * The {@link DatePicker} used to set the time of the simulation.
     */
    @FXML
    public DatePicker datePicker;
    /**
     * The field representing the hour.
     */
    @FXML
    public Spinner hoursField;
    /**
     * The field representing the minutes.
     */
    @FXML
    public Spinner minutesField;
    /**
     * The side hierarchical tree view of the system.
     */
    @FXML
    public TreeView<CelestialBody> navigationTree;
    //endregion

    /**
     * The file currently opened.
     */
    private File currentFile;

    //region Animation
    /**
     * The timer orchestrating the animation of the {@link SpaceCanvas} map.
     */
    private CanvasAnimationTimer canvasAnimationTimer;
    /**
     * The time of the simulation.
     */
    private long time = new Date().getTime();
    //endregion

    //region Constructor

    /**
     * Loads the default map and initialises the UI.
     */
    public MainController() {
        try {
            astrarium = JsonHub.importDefaultMap("SolSystem");
        } catch (Exception e) {
            dialogError("Default map error", "The program failed to load the default map, and will now quit.");
            System.exit(-1);
        }
    }
    //endregion Constructor

    //region Initialisers

    /**
     * Asynchronously initialise the UI after every component has been loaded.
     *
     * @see MainController#initUI()
     */
    @FXML
    public void initialize() {
        Platform.runLater(this::initUI);
    }

    /**
     * Actual UI initialisation.
     */
    private void initUI() {
        initMenu();
        initCanvas();
        initNavigationTree();
        initAnimationTimer();
    }

    /**
     * Populates the navigation tree with the bodies inside the {@link Astrarium}.
     */
    private void initNavigationTree() {
        TreeItem<CelestialBody> root = new TreeItem<>(astrarium.getRoot());

        addToTree(astrarium.getRoot(), root);

        root.setExpanded(true);

        navigationTree.setRoot(root);

        navigationTree.setOnMouseClicked(event -> {
            TreeItem<CelestialBody> item = navigationTree.getSelectionModel().getSelectedItem();

            canvas.setOffset(item.getValue().getPosition());
        });
    }

    /**
     * Adds the children of the body to the navigation tree.
     *
     * @param parentBody the parent celestial body
     * @param parentNode the parent node where to attach the nested tree view.
     */
    private void addToTree(CelestialBody parentBody, TreeItem<CelestialBody> parentNode) {
        for (CelestialBody childBody : parentBody.getChildren()) {
            TreeItem<CelestialBody> childNode = new TreeItem<>(childBody);

            addToTree(childBody, childNode);

            parentNode.getChildren().add(childNode);
        }
    }

    /**
     * Initialises the file menu with the default actions.
     */
    private void initMenu() {
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(0))).selectedProperty().bindBidirectional(canvas.showOrbit);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(1))).selectedProperty().bindBidirectional(canvas.showNames);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(2))).selectedProperty().bindBidirectional(canvas.showSphereOfInfluence);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(3))).selectedProperty().bindBidirectional(canvas.showHillSphere);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(4))).selectedProperty().bindBidirectional(canvas.showTangentVector);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(5))).selectedProperty().bindBidirectional(canvas.showMarkers);
    }

    /**
     * Initiates the 2D map, binding its sizes and creating interactions.
     */
    private void initCanvas() {
        Window window = canvas.getScene().getWindow();

        canvas.widthProperty().bind(window.widthProperty());
        canvas.heightProperty().bind(window.heightProperty());

        Parent parent = canvas.getParent();

        canvas.widthProperty().bind(((Pane) parent).widthProperty());
        canvas.heightProperty().bind(((Pane) parent).heightProperty());

        canvas.setAstrarium(astrarium);

        canvas.setOnClickHandler(position -> {
            CelestialBody body = astrarium.getRoot();

            Vector velocity = body.getCircularOrbitVelocity(position);

            Vector axis = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
//
            double angle = random() * TWO_PI;
//
            velocity.rotate(axis, angle);

            velocity.rotateZ(angle);

            Orbit orbit = Orbit.calculateOrbitFromPositionAndVelocity(astrarium.getRoot(), position, velocity, canvas.getTime());

            new CelestialBody(String.valueOf(new Random().nextInt(100)), 0, 2e5, orbit);

            initNavigationTree();

            System.out.println(orbit);
        });

        canvas.setTime(time);
    }

    /**
     * Initialises and stars the animation timer.
     */
    private void initAnimationTimer() {
        datePicker.setValue(LocalDate.now());

        canvasAnimationTimer = new CanvasAnimationTimer();

        canvasAnimationTimer.start();

//        Timeline timeline = new Timeline(new KeyFrame(
//                Duration.millis(16),
//                ae -> {
//                    canvas.draw(time);
//                    time = new Date().getTime();
//
//                    //datePicker.setValue(new Timestamp(time).toLocalDateTime().toLocalDate());
//                }));
//        timeline.setCycleCount(Animation.INDEFINITE);
//        timeline.play();
    }
    //endregion Initialisers

    //region Handlers

    /**
     * Handles action happening when the date picker is changed.
     */
    @FXML
    public void datePickerActionHandler() {
        // TODO
    }

    /**
     * Opens a pop-up to load a save file.
     */
    @FXML
    public void loadFile() {
        canvasAnimationTimer.stop();

        FileChooser fileChooser = getFileChooser();
        fileChooser.setTitle("Open Data File");
        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());

        if (file != null)
            try {
                astrarium = JsonHub.importAstrariumJson(file);

                initNavigationTree();
                canvas.setAstrarium(astrarium);

                currentFile = file;
            } catch (IOException e) {
                dialogError("Load failed",
                        "The program failed to load the file because of an I/O exception.");
            } catch (JsonParseException e) {
                dialogError("Parsing failed",
                        "The program failed to load the file because the specified file was not a valid Astrarium save.");
            } catch (Exception e) {
                dialogError("Unknown Error",
                        String.format("The program encountered an unknown error while saving the file. Exception details: %s.", e.getClass()));
            }

        canvasAnimationTimer.start();
    }

    /**
     * Opens a pop-up to save a file.
     */
    @FXML
    public void saveAsFile() {
        FileChooser fileChooser = getFileChooser();
        fileChooser.setTitle("Save Data File");
        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());

        if (file != null)
            saveFile(file);
    }

    /**
     * Saves the current opened file.
     * If current file is {@code null}, a save as dialog will appear.
     */
    @FXML
    public void saveFile() {
        saveFile(currentFile);
    }

    /**
     * Saves the current state of the astrarium to the specified {@code file}.
     * If current file is {@code null}, a save as dialog will appear.
     *
     * @param file file to save.
     */
    private void saveFile(File file) {
        canvasAnimationTimer.stop();

        if (file != null)
            try {
                JsonHub.exportJson(file, astrarium);
                currentFile = file;
            } catch (IOException e) {
                dialogError("Save failed", "The program was unable to save the file.");
            }
        else
            saveAsFile();

        canvasAnimationTimer.start();
    }

    /**
     * Returns the default file chooser.
     *
     * @return file chooser.
     */
    @NotNull
    private FileChooser getFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data", "*.json"));

        try {
            File defaultFolder;

            defaultFolder = currentFile != null ?
                    currentFile.getParentFile() :
                    new File("src/astrarium/data/");

            if (!defaultFolder.exists())
                throw new FileNotFoundException();

            fileChooser.setInitialDirectory(defaultFolder);
        } catch (Exception e) {
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
        }

        return fileChooser;
    }

    /**
     * Opens a pop-up to create a new body.
     */
    @FXML
    public void newBody() {
        Modal orbit = new BodyModal();
        orbit.initOwner(canvas.getScene().getWindow());
        orbit.showAndWait();
        orbit.getResult();
    }
    //endregion

    //region Dialogs

    /**
     * Makes a default error dialog.
     *
     * @param headerText  title of the error dialog.
     * @param contentText body of the error dialog.
     */
    private void dialogError(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Stage scene = (Stage) alert.getDialogPane().getScene().getWindow();
        scene.getIcons().add(Main.getIcon());
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }
    //endregion

    /**
     * An inner class that defines the animation behaviour of the SpaceCanvas.
     */
    private class CanvasAnimationTimer extends AnimationTimer {
        @SuppressWarnings("unchecked")
        @Override
        public void handle(long now) {
            now = new Date().getTime();

            canvas.draw(now);
            time = new Date().getTime();

            LocalDateTime localDateTime = new Timestamp(now).toLocalDateTime();

            datePicker.setValue(localDateTime.toLocalDate());

            hoursField.getValueFactory().setValue(localDateTime.getHour());
            minutesField.getValueFactory().setValue(localDateTime.getMinute());
        }
    }
}
