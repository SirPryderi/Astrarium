package fx.components;

import astrarium.Astrarium;
import astrarium.CelestialBody;
import astrarium.Orbit;
import astrarium.utils.Mathematics;
import astrarium.utils.Position;
import astrarium.utils.Vector;
import fx.utils.Colors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

import java.util.HashMap;
import java.util.function.Consumer;

import static astrarium.utils.Mathematics.TWO_PI;
import static java.lang.Math.toDegrees;

/**
 * An extension of the standard JavaFX {@link Canvas}, capable of showing an {@link Astrarium} to screen.
 * <p>
 * Created on 10-Nov-16.
 *
 * @author Vittorio
 */
public class SpaceCanvas extends Canvas {
    //region Flags
    /**
     * A flag for showing the orbit trace.
     */
    public BooleanProperty showOrbit = new SimpleBooleanProperty(true);
    /**
     * A flag for showing the labels with bodies names.
     */
    public BooleanProperty showNames = new SimpleBooleanProperty(true);
    /**
     * A flag for showing the radius of the SoI.
     */
    public BooleanProperty showSphereOfInfluence = new SimpleBooleanProperty(false);
    /**
     * A flag for showing the radius of the Hill Sphere.
     */
    public BooleanProperty showHillSphere = new SimpleBooleanProperty(false);
    /**
     * A flag for showing a vector tangent to the current orbit position.
     */
    public BooleanProperty showTangentVector = new SimpleBooleanProperty(false);
    /**
     * A flag for showing markers of important points of the orbit.
     */
    public BooleanProperty showMarkers = new SimpleBooleanProperty(false);
    //endregion

    /**
     * The {@link Astrarium} the map is delegated to display visually.
     */
    private Astrarium astrarium;

    //region Visual properties
    /**
     * A ratio that represents the scale of the map.
     */
    private double zoom = 5e-10;

    /**
     * The offset that indicates the center of the map, that allows it to be panned when changed.
     */
    private Position offset = new Position();
    //endregion

    /**
     * A cache of computed orbit position.
     */
    private HashMap<Orbit, Position[]> orbitsCache = new HashMap<>();

    //region Handlers
    /**
     * The even triggered when a point on the canvas is pressed.
     */
    private Consumer<Position> onClickHandler = null;
    //endregion

    /**
     * {@inheritDoc}
     **/
    public SpaceCanvas() {
        makeCanvasDraggable();
    }

    /**
     * Sets the handler for the event triggered when the canvas is clicked.
     * The position returned is in the absolute coordinates.
     *
     * @param onClickHandler the new handler.
     */
    public void setOnClickHandler(Consumer<Position> onClickHandler) {
        this.onClickHandler = onClickHandler;
    }

    //region Time

    /**
     * Returns the current time of the Map.
     *
     * @return current time.
     */
    public long getTime() {
        return astrarium.getTime();
    }

    /**
     * Sets the time of the {@link SpaceCanvas} and renders all the planets at the given time. It's like magic!
     *
     * @param time current time.
     */
    public void setTime(long time) {
        this.astrarium.setTime(time);
    }
    //endregion

    //region Drawing Functions

    /**
     * Sets the time of the simulation and draws it at the current state.
     *
     * @param time Unix timestamp in milliseconds.
     */
    public void draw(long time) {
        setTime(time);
        draw();
    }

    /**
     * Draws the content of the {@link Astrarium}.
     */
    private void draw() {
        getGraphicsContext2D().clearRect(0, 0, this.getWidth(), this.getHeight());
        getGraphicsContext2D().save();
        translateToCenter();

        getGraphicsContext2D().setStroke(Color.BLACK);
        getGraphicsContext2D().setLineWidth(1);

        drawPlanet(astrarium.getRoot());

        getGraphicsContext2D().restore();
    }

    /**
     * Private method to draw a {@link CelestialBody} on the map.
     *
     * @param celestialBody the body to draw.
     */
    private void drawPlanet(CelestialBody celestialBody) {
        double radius = 5;
        if (celestialBody.getRadius() * zoom > radius)
            radius = celestialBody.getRadius() * zoom;

        //region non-rotated Position
//        if (celestialBody.getOrbit() != null) {
//            getGraphicsContext2D().save();
//            getGraphicsContext2D().translate(celestialBody.getOrbit().getRenderedPositionFromOrbitalPlane().getX() * zoom, celestialBody.getOrbit().getRenderedPositionFromOrbitalPlane().getY() * zoom);
//            getGraphicsContext2D().fillOval(-radius / 2, -radius / 2, radius, radius);
//            getGraphicsContext2D().restore();
//        }
        //endregion

        getGraphicsContext2D().save();
        Position position = celestialBody.getPositionFromParent();

        getGraphicsContext2D().translate(position.getX() * zoom, position.getY() * zoom);

        // Draw planet dot
        getGraphicsContext2D().setFill(Colors.hashColor(celestialBody.getName()));
        getGraphicsContext2D().fillOval(-radius, -radius, radius * 2, radius * 2);

        // Draw label
        if (showNames.get()) {
            getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
            getGraphicsContext2D().setFill(Color.GREY);
            getGraphicsContext2D().fillText(celestialBody.getName(), 0, radius * 2 + 7);
        }

        // Draw sphere of influence
        if (showSphereOfInfluence.get())
            drawSphereOfInfluence(celestialBody);

        if (showHillSphere.get())
            drawHillSphere(celestialBody);

        if (showTangentVector.get())
            drawTangentVector(celestialBody);

        celestialBody.getChildren().forEach(children -> {
            if (showOrbit.get()) {
                getGraphicsContext2D().setStroke(Colors.hashColor(children.getName()));
                drawOrbit(children.getOrbit());
            }
            drawPlanet(children);
        });

        getGraphicsContext2D().restore();
    }

    /**
     * Private method to draw the orbit on the map.
     *
     * @param orbit the orbit to draw.
     */
    private void drawOrbit(Orbit orbit) {
        if (showMarkers.get()) {
            Position periapsisPosition = orbit.getPeriapsisPosition();
            Position apoapsisPosition = orbit.getApoapsisPosition();
            Position northernVertex = orbit.getNorthernVertex();
            Position southernVertex = orbit.getSouthernVertex();
//            Position center = orbit.getCenter();

            drawMarker(periapsisPosition, "Pe", Color.RED, 3);
            drawMarker(apoapsisPosition, "Ap", Color.BLUE, 5);
            drawMarker(northernVertex, "Ap", Color.GREEN, 5);
            drawMarker(southernVertex, "Ap", Color.BLACK, 5);
//            drawMarker(center, "Ap", Color.BLACK, 5);

            drawLine(periapsisPosition, apoapsisPosition);
            drawLine(northernVertex, southernVertex);
        }

        if (Mathematics.equals(orbit.getInclination(), 0, 0.05) && orbit.getEccentricity() < 1)
            overlyOptimisticDrawOrbit(orbit);
        else
            drawOrbitAltCached(orbit);
    }

    @Deprecated
    private void drawEllipse(Orbit orbit) {
        getGraphicsContext2D().save();

        Vector nodeAxis = Vector.getDirectionVector(orbit.getLongitudeOfAscendingNode());
        Vector normalAxis = new Vector(0, 0, 1);
        normalAxis.rotate(nodeAxis, orbit.getInclination());

        Transform rotate0 = new Rotate(toDegrees(orbit.getInclination()), new Point3D(nodeAxis.getX(), nodeAxis.getY(), nodeAxis.getZ()));
        Transform rotate1 = new Rotate(toDegrees(orbit.getLongitudeOfAscendingNode()));
        Transform rotate2 = new Rotate(toDegrees(orbit.getArgumentOfPeriapsis()), new Point3D(normalAxis.getX(), normalAxis.getY(), normalAxis.getZ()));

        Affine affine = new Affine();
        affine.append(rotate0);
        affine.append(rotate1);
        affine.append(rotate2);

        // God, none of this transformation is actually working. Great!

        getGraphicsContext2D().transform(affine);

        getGraphicsContext2D().strokeOval(
                (-orbit.getSemiMajorAxis() * 2 + orbit.getPeriapsis()) * zoom, // x
                -orbit.getSemiMinorAxis() * zoom, // y
                orbit.getSemiMajorAxis() * zoom * 2, // 2 a
                orbit.getSemiMinorAxis() * zoom * 2 // 2 b
        );

        getGraphicsContext2D().restore();
    }

    private void drawLine(Position p1, Position p2) {
        getGraphicsContext2D().strokeLine(p1.getX() * zoom, p1.getY() * zoom, p2.getX() * zoom, p2.getY() * zoom);
    }

    private void overlyOptimisticDrawOrbit(Orbit orbit) {
        getGraphicsContext2D().save();

        getGraphicsContext2D().rotate(toDegrees(orbit.getLongitudeOfAscendingNode() + orbit.getArgumentOfPeriapsis()));

        double x2 = orbit.getSemiMajorAxis() * zoom * 2;
        double y = orbit.getSemiMinorAxis() * zoom;

        getGraphicsContext2D().strokeOval(-x2 + orbit.getPeriapsis() * zoom, -y, x2, y * 2);

        getGraphicsContext2D().restore();
    }

    @Deprecated
    private void drawOrbitAlt(Orbit orbit) {
        final double epsilon = 0.01;

        getGraphicsContext2D().beginPath();

        Position start = toCanvasCoordinate(orbit.getPositionFromParentAtAngle(0));

        moveTo(start);

        Position point;

        for (double theta = epsilon; theta <= TWO_PI; theta += epsilon) {
            point = orbit.getPositionFromParentAtAngle(theta);
            lineTo(toCanvasCoordinate(point));
        }

        lineTo(start);

        getGraphicsContext2D().stroke();
        getGraphicsContext2D().closePath();
    }

    private void drawOrbitAltCached(Orbit orbit) {
        if (!orbitsCache.containsKey(orbit))
            createOrbitCache(orbit);

        Position[] positions = orbitsCache.get(orbit);

        getGraphicsContext2D().beginPath();

        moveTo(toCanvasCoordinate(positions[0]));

        for (int i = 1; i < positions.length; i++) {
            lineTo(toCanvasCoordinate(positions[i]));
        }

        lineTo(toCanvasCoordinate(positions[0]));

        getGraphicsContext2D().stroke();
        getGraphicsContext2D().closePath();
    }

    private void createOrbitCache(Orbit orbit) {
        final int count = 4096;

        final double epsilon = TWO_PI / count;

        Position[] ps = new Position[count];

        Position point;
        for (int i = 0; i < count; i++) {
            point = orbit.getPositionFromParentAtAngle(epsilon * i);

            ps[i] = point;
        }

        this.orbitsCache.put(orbit, ps);
    }

    private void moveTo(Vector p) {
        getGraphicsContext2D().moveTo(p.getX(), p.getY());
    }

    private void lineTo(Vector p) {
        getGraphicsContext2D().lineTo(p.getX(), p.getY());
    }

    /**
     * Private method to draw PoIs on the map.
     *
     * @param position position of the marker.
     * @param name     name of the marker.
     * @param color    color of the marker.
     */
    private void drawMarker(Position position, String name, Color color) {
        drawMarker(position, name, color, 3);
    }

    /**
     * Private method to draw PoIs on the map.
     *
     * @param position position of the marker.
     * @param name     name of the marker.
     * @param color    color of the marker.
     * @param radius   radius of the marker.
     */
    private void drawMarker(Position position, String name, Color color, int radius) {
        getGraphicsContext2D().save();
        getGraphicsContext2D().setFill(color);

        getGraphicsContext2D().fillOval(position.getX() * zoom - radius * 0.5, position.getY() * zoom - radius * 0.5, radius, radius);

        // Todo write labels

        getGraphicsContext2D().restore();
    }

    /**
     * Private method to draw the radius of the SoI.
     *
     * @param celestialBody body to draw the SoI of.
     */
    private void drawSphereOfInfluence(CelestialBody celestialBody) {
        getGraphicsContext2D().save();

        getGraphicsContext2D().setStroke(Color.GREEN);

        double radius = celestialBody.getSphereOfInfluence() * zoom;

        getGraphicsContext2D().strokeOval(-radius / 2, -radius / 2, radius, radius);

        getGraphicsContext2D().restore();
    }

    /**
     * Private method to draw the radius of the Hill Sphere.
     *
     * @param celestialBody body to draw the Hill Sphere of.
     */
    private void drawHillSphere(CelestialBody celestialBody) {
        getGraphicsContext2D().save();

        getGraphicsContext2D().setStroke(Color.BLUE);

        double radius = celestialBody.getHillSphere() * zoom;

        getGraphicsContext2D().strokeOval(-radius / 2, -radius / 2, radius, radius);

        getGraphicsContext2D().restore();
    }

    /**
     * Private method to draw a vector tangent to the orbit at the current position.
     *
     * @param celestialBody the body to draw the tangent vector of.
     */
    private void drawTangentVector(CelestialBody celestialBody) {
        if (celestialBody.getOrbit() == null)
            return;

        //Position position = celestialBody.getPositionFromParent();

        //getGraphicsContext2D().save();

        //getGraphicsContext2D().translate(position.getX(), position.getY());

        //getGraphicsContext2D().rotate(celestialBody.getOrbit().getTangentVector1());

        double angle = celestialBody.getOrbit().getTangentAngle();

        angle = angle + celestialBody.getOrbit().getLongitudeOfAscendingNode();

        double magnitude = celestialBody.getOrbit().getVelocityMagnitude() / 10000;

        getGraphicsContext2D().strokeLine(0, 0, Math.cos(angle) * magnitude, Math.sin(angle) * magnitude);

        //getGraphicsContext2D().restore();
    }
    //endregion

    //region Zoom

    /**
     * Increases the zoom of the map.
     */
    public void zoomIn() {
        zoom = zoom + zoom / 2;
    }

    /**
     * Decreases the zoom of the map.
     */
    public void zoomOut() {
        zoom = zoom + zoom / 2;
    }

    /**
     * Sets the zoom of the map at a specific value.
     *
     * @param amount zoom ratio.
     */
    public void changeZoom(double amount) {
        zoom = zoom + zoom / amount;
    }
    //endregion

    //region Offset

    /**
     * Sets the center of the map.
     *
     * @param x x coordinate of the center.
     * @param y y coordinate of the center.
     */
    private void setOffset(double x, double y) {
        offset.setValues(-x * zoom, -y * zoom);
    }

    /**
     * Sets the center of the map.
     *
     * @param position {@link Position} to be at the center of the map.
     */
    public void setOffset(Position position) {
        setOffset(position.getX(), position.getY());
    }

    /**
     * Move the reference coordinates at the center of the {@link SpaceCanvas}.
     */
    private void translateToCenter() {
        getGraphicsContext2D().translate(this.getWidth() / 2 + offset.getX(), this.getHeight() / 2 + offset.getY());
    }
    //endregion

    /**
     * Sets the Astrarium the {@link SpaceCanvas} is delegated to display.
     *
     * @param astrarium the new astrarium to set.
     */
    public void setAstrarium(Astrarium astrarium) {
        this.astrarium = astrarium;
    }

    /**
     * Adds interactivity to the canvas.
     */
    private void makeCanvasDraggable() {
        Position start = new Position();
        Position initialOffset = new Position();

        this.setOnMousePressed(event -> {
            initialOffset.setValues(offset);
            start.setValues(event.getX(), event.getY());
        });

        this.setOnMouseDragged(event -> {

            this.setCursor(Cursor.CLOSED_HAND);
            offset.setValues(initialOffset.getX() + (event.getX() - start.getX()), (initialOffset.getY() + event.getY() - start.getY()), 0);
            this.setOnMouseReleased(event1 -> this.setCursor(Cursor.CROSSHAIR));
        });

        this.setOnMouseEntered(event -> this.setCursor(Cursor.CROSSHAIR));

        this.setOnMouseExited(event -> this.setCursor(Cursor.DEFAULT));

        this.setOnScroll(event -> {
            event.consume();
            double old_zoom = zoom;

            if (event.getDeltaY() == 0)
                return;

            if (event.isControlDown())
                changeZoom(event.getDeltaY() / 5);
            else if (event.isAltDown())
                changeZoom(event.getDeltaY() * 5);
            else
                changeZoom(event.getDeltaY());

            offset.setValues(offset.getX() * zoom / old_zoom, offset.getY() * zoom / old_zoom);
        });

        this.setOnMouseClicked(event -> {
            double x = event.getX() - getWidth() / 2;
            double y = event.getY() - getHeight() / 2;

            Position position = toUniversalCoordinate(new Position(x, y));

            if (onClickHandler != null && event.getButton() == MouseButton.PRIMARY && event.isStillSincePress())
                onClickHandler.accept(position);
        });
    }

    /**
     * Converts a position from the universal coordinates used in the model
     * to the coordinates in the viewport of the canvas.
     *
     * @param position the position to convert.
     * @return the converted position.
     */
    private Position toCanvasCoordinate(Position position) {
        return (Position) position.getCopy().multiplied(zoom);
    }

    /**
     * Converts a position from the canvas coordinates
     * to the coordinates universal coordinates.
     *
     * @param position the position to convert.
     * @return the converted position.
     */
    private Position toUniversalCoordinate(Position position) {
        return (Position) position.getCopy().divided(zoom);
    }

    @Override
    public boolean isResizable() {
        return true;
    }
}
