package astrarium;

import astrarium.utils.Position;

/**
 * The {@link Body} abstract class wraps methods and attributes of an object that can orbit another {@link Body}.
 * <p>
 * Created on 20/02/2017.
 *
 * @author Vittorio
 * @see CelestialBody
 * @see Spacecraft
 */
public abstract class Body {
    /**
     * Name of the body
     **/
    private String name;

    /**
     * Mass of the object, expressed in kilograms.
     */
    private double mass; // kg
    /**
     * Maximum radius of the object, expressed in meters.
     */
    private double radius; // m

    /**
     * Constructor that sets all the mandatory attributes needed for the physical simulation.
     *
     * @param name   Name of the object. Must be unique.
     * @param mass   Mass of the object in kilograms.
     * @param radius Maximum radius of the object in meters.
     */
    public Body(String name, double mass, double radius) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
    }

    //region Getters and Setters
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
    //endregion Getters and Setters

    /**
     * Returns the absolute position of the object at the time it was rendered with {@link #renderAtTime(long)}.
     * <p>
     * Note that {@link #renderAtTime(long)} must be launched before getting a meaningful value.
     * <p>
     * The position is evaluated from the root element of the system.
     *
     * @return the position at when the object has been rendered.
     */
    public Position getPosition() {
        if (this.getOrbit() == null)
            return this.getRelativePosition();

        return (Position) getOrbit().getParent().getPosition().plus(getRelativePosition());
    }

    /**
     * Returns the position relative to the reference object at the time specified.
     *
     * @param time the time in milliseconds.
     * @return relative position at time
     */
    public Position getRelativePosition(long time) {
        if (getOrbit() != null) {
            return getOrbit().getPositionFromParent(time);
        } else {
            return new Position(0, 0);
        }
    }

    /**
     * Returns the position relative to the reference body of the object at the time it was rendered with {@link #renderAtTime(long)}.
     * <p>
     * Note that {@link #renderAtTime(long)} must be launched before getting a meaningful value.
     * <p>
     * The position is evaluated from the root element of the system.
     *
     * @return the position at when the object has been rendered.
     */
    public Position getRelativePosition() {
        if (getOrbit() != null) {
            return getOrbit().getRenderedPositionFromParent();
        } else {
            return new Position(0, 0);
        }
    }

    /**
     * Renders the position of the given object at the time specified and store the rendered parameters for being retrieved later with methods like {@link #getPosition()} or {@link #getRelativePosition()}.
     *
     * @param time time in  milliseconds
     */
    public void renderAtTime(long time) {
        if (this.getOrbit() != null)
            this.getOrbit().renderAtTime(time);
    }

    /**
     * Returns the orbit at the time it was rendered with {@link #renderAtTime(long)}.
     * <p>
     * Note that {@link #renderAtTime(long)} must be launched before getting a meaningful value.
     *
     * @return orbit at rendered time.
     */
    abstract Orbit getOrbit();

    /**
     * Returns the orbit at the time specified.
     * <p>
     * For a {@link CelestialBody} running {@link #getOrbit()} is equivalent.
     * <p>
     * Note that {@link #renderAtTime(long)} must be launched before getting a meaningful value.
     *
     * @param time when to get the orbit in milliseconds
     * @return orbit at the given time
     */
    abstract Orbit getOrbitAtTime(long time);
}
