package fx;

import astrarium.Astrarium;
import astrarium.CelestialBody;
import astrarium.Orbit;
import astrarium.utils.Conversion;
import astrarium.utils.Position;
import astrarium.utils.Vector;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.Random;

import static astrarium.utils.Mathematics.TWO_PI;
import static java.lang.Math.PI;
import static java.lang.Math.random;

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
    BooleanProperty showOrbit = new SimpleBooleanProperty(true);
    /**
     * A flag for showing the labels with bodies names.
     */
    BooleanProperty showNames = new SimpleBooleanProperty(true);
    /**
     * A flag for showing the radius of the SoI.
     */
    BooleanProperty showSphereOfInfluence = new SimpleBooleanProperty(false);
    /**
     * A flag for showing the radius of the Hill Sphere.
     */
    BooleanProperty showHillSphere = new SimpleBooleanProperty(false);
    /**
     * A flag for showing a vector tangent to the current orbit position.
     */
    BooleanProperty showTangentVector = new SimpleBooleanProperty(false);
    /**
     * A flag for showing markers of important points of the orbit.
     */
    BooleanProperty showMarkers = new SimpleBooleanProperty(false);
    //endregion

    /**
     * The {@link Astrarium} the map is delegated to display visually.
     */
    private Astrarium astrarium;

    /**
     * A ratio that represents the scale of the map.
     */
    private double zoom = 5e-10;

    //region Visual properties
    /**
     * The offset that indicates the center of the map, that allows it to be panned when changed.
     */
    private Position offset = new Position();
    //endregion

    /**
     * {@inheritDoc}
     **/
    public SpaceCanvas() {
        makeCanvasDraggable();
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
    void draw(long time) {
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
        if (orbit.getEccentricity() > 1) {
            drawOrbitAlt(orbit);
            return;
        }

        getGraphicsContext2D().save();

        getGraphicsContext2D().rotate(Conversion.radToDeg(orbit.getLongitudeOfAscendingNode() + orbit.getArgumentOfPeriapsis()));

        // TODO rotate the orbit
        getGraphicsContext2D().strokeOval(
                (-orbit.getSemiMajorAxis() * 2 + orbit.getPeriapsis()) * zoom, // x
                -orbit.getSemiMinorAxis() * zoom, // y
                orbit.getSemiMajorAxis() * zoom * 2, // 2 a
                orbit.getSemiMinorAxis() * zoom * 2 // 2 b
        );

        if (showMarkers.get()) {
            drawMarker(orbit.getPeriapsis() * zoom, 0, "Pe", Color.RED, 3);
            drawMarker(-orbit.getApoapsis() * zoom, 0, "Ap", Color.BLUE, 5);
        }

        getGraphicsContext2D().restore();
    }

    /**
     * Alternative method to draw orbits that joins lines created evaluating the each point of the orbit.
     *
     * @param orbit the orbit to draw.
     */
    private void drawOrbitAlt(Orbit orbit) {
        // TODO Change  the resolution according to the zoom
        // TODO draw only the bit actually visible in the viewport

        getGraphicsContext2D().beginPath();
        double theta = 0;

        double maxValue = orbit.getRadius(theta) * zoom;

        getGraphicsContext2D().moveTo(maxValue, 0);

        for (; theta <= TWO_PI; theta += .05) {
            double radius = orbit.getRadius(theta) * zoom;

            double x = radius * Math.cos(theta);
            double y = -radius * Math.sin(theta);

            if (x > maxValue)
                continue;

            //ctx.quadraticCurveTo(x*2, y*2, x, y);
            getGraphicsContext2D().lineTo(x, y);
        }

        getGraphicsContext2D().lineTo(orbit.getRadius(theta) * zoom, -PI);

        getGraphicsContext2D().stroke();
        getGraphicsContext2D().closePath();
    }

    /**
     * Private method to draw PoIs on the map.
     *
     * @param x     x coordinate.
     * @param y     y coordinate.
     * @param name  name of the marker.
     * @param color color of the marker.
     */
    private void drawMarker(double x, double y, String name, Color color) {
        drawMarker(x, y, name, color, 3);
    }

    /**
     * Private method to draw PoIs on the map.
     *
     * @param x      x coordinate.
     * @param y      y coordinate.
     * @param name   name of the marker.
     * @param color  color of the marker.
     * @param radius radius of the marker.
     */
    private void drawMarker(double x, double y, String name, Color color, int radius) {
        getGraphicsContext2D().save();
        getGraphicsContext2D().setFill(color);

        getGraphicsContext2D().fillOval(x - radius * 0.5, y - radius * 0.5, radius, radius);

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
    void setOffset(Position position) {
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

            Position position = (Position) new Position(x, y).divided(zoom);

            CelestialBody body = astrarium.getRoot();

            Vector velocity = body.getCircularOrbitVelocity(position);

//            Vector axis = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
//
            double angle = random() * TWO_PI;
//
//            velocity.rotate(axis, angle);

            velocity.rotateZ(angle);

            Orbit orbit = Orbit.calculateOrbitFromPositionAndVelocity(astrarium.getRoot(), position, velocity, getTime());

            new CelestialBody(String.valueOf(new Random().nextInt(100)), 0, 2e5, orbit);

            System.out.println(orbit);
        });
    }

    @Override
    public boolean isResizable() {
        return true;
    }
}
