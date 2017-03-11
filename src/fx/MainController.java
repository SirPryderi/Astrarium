package fx;

import astrarium.Astrarium;
import astrarium.CelestialBody;
import io.JsonHub;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Controller for the view of the main window.
 * <p>
 * Created on 06/03/2017.
 *
 * @author Vittorio
 */
public class MainController {
    //region FXML Nodes
    @FXML
    public MenuBar menu;
    @FXML
    public HBox gameGrid;
    @FXML
    public SpaceCanvas canvas;
    @FXML
    public DatePicker datePicker;
    @FXML
    public Spinner hoursField;
    @FXML
    public Spinner minutesField;
    @FXML
    public TreeView<CelestialBody> navigationTree;
    //endregion

    private CanvasAnimationTimer canvasAnimationTimer;
    private long time = new Date().getTime();
    private Astrarium astrarium;

    //region Constructor
    public MainController() {
        try {
            astrarium = new Astrarium(JsonHub.importDefaultMap("SolSystem"));
        } catch (IOException e) {
            System.err.println("Failed to load default map.");
            e.printStackTrace();
            System.exit(-1);
        }
    }
    //endregion Constructor

    //region Initialisers
    @FXML
    public void initialize() {
        Platform.runLater(this::initUI);
    }

    private void initUI() {
        assert astrarium.getRoot() != null;

        initMenu();
        initCanvas();
        initNavigationTree();
        initAnimationTimer();
    }

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

    private void addToTree(CelestialBody parentBody, TreeItem<CelestialBody> parentNode) {
        for (CelestialBody childBody : parentBody.getChildren()) {
            TreeItem<CelestialBody> childNode = new TreeItem<>(childBody);

            addToTree(childBody, childNode);

            parentNode.getChildren().add(childNode);
        }
    }

    private void initMenu() {
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(0))).selectedProperty().bindBidirectional(canvas.showOrbit);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(1))).selectedProperty().bindBidirectional(canvas.showNames);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(2))).selectedProperty().bindBidirectional(canvas.showSphereOfInfluence);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(3))).selectedProperty().bindBidirectional(canvas.showHillSphere);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(4))).selectedProperty().bindBidirectional(canvas.showTangentVector);
        ((CheckMenuItem) (menu.getMenus().get(3).getItems().get(5))).selectedProperty().bindBidirectional(canvas.showMarkers);
    }

    private void initCanvas() {
        Window window = canvas.getScene().getWindow();

        canvas.widthProperty().bind(window.widthProperty());
        canvas.heightProperty().bind(window.heightProperty());

        canvas.makeCanvasDraggable();

        Parent parent = canvas.getParent();

        canvas.widthProperty().bind(((Pane) parent).widthProperty());
        canvas.heightProperty().bind(((Pane) parent).heightProperty());

        canvas.setAstrarium(astrarium);

        canvas.setTime(time);
    }

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
    @FXML
    public void datePickerActionHandler() {
        // TODO
    }

    @FXML
    public void loadFile() {
        canvasAnimationTimer.stop();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Data File");

        try {
            File defaultFolder = new File("src/astrarium/data/");

            if (!defaultFolder.exists())
                throw new FileNotFoundException();

            fileChooser.setInitialDirectory(defaultFolder);
        } catch (Exception e) {
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
        }

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data", "*.json"));
        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());

        if (file != null)
            try {

                CelestialBody root = JsonHub.importJson(file);

                astrarium = new Astrarium(root);

                initNavigationTree();
                canvas.setAstrarium(astrarium);
            } catch (IOException e) {
                e.printStackTrace();
            }

        canvasAnimationTimer.start();
    }
    //endregion

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
