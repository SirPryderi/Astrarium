package astrarium;

import astrarium.utils.Position;

/**
 * An abstract class representing every object that can orbit something else.
 * <p>
 * Created on 20/02/2017.
 *
 * @author Vittorio
 */
public abstract class Body {
    // Name of the body
    private String name;

    // Physical Properties
    private double mass; // kg
    private double radius; // m

    public Body(String name, double mass, double radius) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public Position getPosition() {
        if (this.getOrbit() == null)
            return this.getRelativePosition();

        return (Position) getOrbit().getParent().getPosition().plus(getRelativePosition());
    }

    public Position getRelativePosition(long time) {
        if (getOrbit() != null) {
            return getOrbit().getPositionFromParent(time);
        } else {
            return new Position(0, 0);
        }
    }

    public Position getRelativePosition() {
        if (getOrbit() != null) {
            return getOrbit().getRenderedPositionFromParent();
        } else {
            return new Position(0, 0);
        }
    }

    public void renderAtTime(long time) {
        if (this.getOrbit() != null)
            this.getOrbit().renderAtTime(time);
    }

    abstract Orbit getOrbit();
}
