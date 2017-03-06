package fx;

import astrarium.Astrarium;
import astrarium.CelestialBody;
import astrarium.Orbit;
import astrarium.transfers.Transferable;
import astrarium.utils.Conversion;
import astrarium.utils.Position;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * An extension of the standard JavaFX {@link Canvas}, capable of showing an {@link Astrarium} to screen.
 * <p>
 * Created on 10-Nov-16.
 *
 * @author Vittorio
 */
public class SpaceCanvas extends Canvas {
    // Flags
    public BooleanProperty showOrbit = new SimpleBooleanProperty(true);
    public BooleanProperty showNames = new SimpleBooleanProperty(true);
    public BooleanProperty showSphereOfInfluence = new SimpleBooleanProperty(false);
    public BooleanProperty showHillSphere = new SimpleBooleanProperty(false);
    public BooleanProperty showTangentVector = new SimpleBooleanProperty(false);
    // Astrarium
    private Astrarium astrarium;
    private List<Transferable> transferables = new ArrayList<>();
    // Visual properties
    private double zoom = 0.5e-9;
    private Point2D offset = new Point2D.Double(0, 0);

    public void setAstrarium(Astrarium astrarium) {
        this.astrarium = astrarium;
    }

    public long getTime() {
        return astrarium.getTime();
    }

    public void setTime(long time) {
        this.astrarium.setTime(time);
    }

    public void draw(long time) {
        setTime(time);
        draw();
    }

    public void draw() {
        getGraphicsContext2D().clearRect(0, 0, this.getWidth(), this.getHeight());
        getGraphicsContext2D().save();
        translateToCenter();

        getGraphicsContext2D().setStroke(Color.BLACK);
        getGraphicsContext2D().setLineWidth(1);

        drawPlanet(astrarium.getRoot());

        drawTransfers();

        getGraphicsContext2D().restore();
    }

    private void drawTransfers() {
        getGraphicsContext2D().setStroke(Color.BLUE);

        transferables.forEach(transferable -> drawOrbit(transferable.getOrbit()));
    }

    public void translateToCenter() {
        getGraphicsContext2D().translate(this.getWidth() / 2 + offset.getX(), this.getHeight() / 2 + offset.getY());
    }

    public void drawPlanet(CelestialBody celestialBody) {
        double radius = 10;
        if (celestialBody.getRadius() * zoom > radius)
            radius = celestialBody.getRadius() * zoom;

        //region Unrotated Position
//        if (celestialBody.getOrbit() != null) {
//            getGraphicsContext2D().save();
//            getGraphicsContext2D().translate(celestialBody.getOrbit().getRenderedPositionFromOrbitalPlane().getX() * zoom, celestialBody.getOrbit().getRenderedPositionFromOrbitalPlane().getY() * zoom);
//            getGraphicsContext2D().fillOval(-radius / 2, -radius / 2, radius, radius);
//            getGraphicsContext2D().restore();
//        }
        //endregion

        getGraphicsContext2D().save();
        Position position = celestialBody.getRelativePosition();

        getGraphicsContext2D().translate(position.getX() * zoom, position.getY() * zoom);

        // Draw planet dot
        getGraphicsContext2D().setFill(Color.RED);
        getGraphicsContext2D().fillOval(-radius / 2, -radius / 2, radius, radius);

        // Draw label
        if (showNames.get()) {
            getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
            getGraphicsContext2D().setFill(Color.GREY);
            getGraphicsContext2D().fillText(celestialBody.getName(), 0, radius + 7);
        }

        // Draw sphere of influence
        if (showSphereOfInfluence.get())
            drawSphereOfInfluence(celestialBody);

        if (showHillSphere.get())
            drawHillSphere(celestialBody);

        if (showTangentVector.get())
            drawTangentVector(celestialBody);

        celestialBody.getChildren().forEach(children -> {
            if (showOrbit.get())
                drawOrbit(children.getOrbit());

            drawPlanet(children);
        });

        getGraphicsContext2D().restore();
    }

    public void drawOrbit(Orbit orbit) {
        if (orbit.getEccentricity() > 1) {
            drawOrbitManually(orbit);
            return;
        }

        getGraphicsContext2D().save();

        getGraphicsContext2D().rotate(Conversion.radToDeg(orbit.getLongitudeOfAscendingNode()));

        // TODO rotate the orbit
        getGraphicsContext2D().strokeOval(
                (-orbit.getSemiMajorAxis() * 2 + orbit.getPeriapsis()) * zoom, // x
                -orbit.getSemiMinorAxis() * zoom, // y
                orbit.getSemiMajorAxis() * zoom * 2, // 2 a
                orbit.getSemiMinorAxis() * zoom * 2 // 2 b
        );

        getGraphicsContext2D().restore();
    }

    private void drawOrbitManually(Orbit orbit) {
        // TODO Change  the resolution according to the zoom
        // TODO draw only the bit actually visible in the viewport

        getGraphicsContext2D().beginPath();
        double theta = 0;

        double maxValue = orbit.getRadius(theta) * zoom;

        getGraphicsContext2D().moveTo(maxValue, 0);

        for (; theta <= Math.PI * 2; theta += .05) {
            double radius = orbit.getRadius(theta) * zoom;

            double x = radius * Math.cos(theta);
            double y = -radius * Math.sin(theta);

            if (x > maxValue)
                continue;

            //ctx.quadraticCurveTo(x*2, y*2, x, y);
            getGraphicsContext2D().lineTo(x, y);
        }

        getGraphicsContext2D().lineTo(orbit.getRadius(theta) * zoom, -Math.PI);

        getGraphicsContext2D().stroke();
        getGraphicsContext2D().closePath();
    }

    public void drawSphereOfInfluence(CelestialBody celestialBody) {
        getGraphicsContext2D().save();

        getGraphicsContext2D().setStroke(Color.GREEN);

        double radius = celestialBody.getSphereOfInfluence() * zoom;

        getGraphicsContext2D().strokeOval(-radius / 2, -radius / 2, radius, radius);

        getGraphicsContext2D().restore();
    }

    public void drawHillSphere(CelestialBody celestialBody) {
        getGraphicsContext2D().save();

        getGraphicsContext2D().setStroke(Color.BLUE);

        double radius = celestialBody.getHillSphere() * zoom;

        getGraphicsContext2D().strokeOval(-radius / 2, -radius / 2, radius, radius);

        getGraphicsContext2D().restore();
    }

    public void drawTangentVector(CelestialBody celestialBody) {
        if (celestialBody.getOrbit() == null)
            return;

        //Position position = celestialBody.getPositionFromParent();

        //getGraphicsContext2D().save();

        //getGraphicsContext2D().translate(position.getX(), position.getY());

        //getGraphicsContext2D().rotate(celestialBody.getOrbit().getTangentVector1());

        double angle = celestialBody.getOrbit().getTangentVector();

        angle = angle + celestialBody.getOrbit().getLongitudeOfAscendingNode();

        double magnitude = celestialBody.getOrbit().getVelocity() / 10000;

        getGraphicsContext2D().strokeLine(0, 0, Math.cos(angle) * magnitude, Math.sin(angle) * magnitude);

        //getGraphicsContext2D().restore();
    }

    public void zoomIn() {
        zoom = zoom + zoom / 2;
    }

    public void zoomOut() {
        zoom = zoom + zoom / 2;
    }

    public void changeZoom(double amount) {
        zoom = zoom + zoom / amount;
    }

    public void makeCanvasDraggable() {
        Point2D start = new Point2D.Double();
        Point2D initialOffset = new Point2D.Double();

        this.setOnMousePressed(event -> {
            initialOffset.setLocation(offset);
            start.setLocation(event.getX(), event.getY());
        });

        this.setOnMouseDragged(event -> {

            this.setCursor(Cursor.CLOSED_HAND);
            offset.setLocation(initialOffset.getX() + (event.getX() - start.getX()), (initialOffset.getY() + event.getY() - start.getY()));
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

            offset.setLocation(offset.getX() * zoom / old_zoom, offset.getY() * zoom / old_zoom);
        });
    }

    public void setOffset(double x, double y) {
        offset.setLocation(-x * zoom, -y * zoom);
    }

    public void setOffset(Position position) {
        setOffset(position.getX(), position.getY());
    }

    public void addTransfer(Transferable transferable) {
        this.transferables.add(transferable);
    }

    @Override
    public boolean isResizable() {
        return true;
    }
}
