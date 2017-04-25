package astrarium;

import astrarium.utils.Position;
import astrarium.utils.Vector;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

import static astrarium.utils.Mathematics.PI_BY_TWO;
import static java.lang.Math.sqrt;

/**
 * A class representing a natural body of the Solar System e.g. Stars, Planets, Comets, etc.
 * <p>
 * Created  on 28-Oct-16.
 *
 * @author Vittorio
 */
public class CelestialBody extends Body {
    /**
     * List of children Bodies.
     */
    private final List<CelestialBody> children;

    /**
     * Orbit of the planet.
     */
    private Orbit orbit;

    //region Constructors

    /**
     * Creates a new body with an Orbit in it.
     * <p>
     * Note: it will be added automatically to the children of the parent specified in the {@link Orbit}.
     *
     * @param name   name of the body.
     * @param mass   mass of the body in kilograms.
     * @param radius radius of the body in meters
     * @param orbit  the orbit where the
     */
    public CelestialBody(String name, double mass, double radius, Orbit orbit) {
        super(name, mass, radius);
        this.orbit = orbit;

        this.children = new ArrayList<>();

        if (orbit != null) {
            orbit.getParent().addChild(this);
        }
    }

    /**
     * Overloaded constructor with @{@code null} orbit. Useful to create root elements.
     *
     * @param name   name of the body.
     * @param mass   mass of the body in kilograms.
     * @param radius radius of the body in meters
     */
    public CelestialBody(String name, double mass, double radius) {
        this(name, mass, radius, null);
    }
    //endregion Constructors

    //region Getters

    /**
     * Returns a {@link List} of {@link CelestialBody} that are contained inside the current object.
     *
     * @return List of children CelestialBody.
     */
    public List<CelestialBody> getChildren() {
        return children;
    }

    /**
     * Returns the parent {@link CelestialBody} of the current object.
     * Returns null if the current object is the root of the system.
     *
     * @return parent body.
     */
    @Nullable
    public CelestialBody getParent() {
        if (getOrbit() != null)
            return getOrbit().getParent();
        else
            return null;
    }

    @Override
    @Nullable
    public Orbit getOrbit() {
        return orbit;
    }

    @Override
    public Orbit getOrbitAtTime(long time) {
        return getOrbit();
    }

    //endregion Getters

    //region Calculations

    /**
     * Returns the standard gravitational parameter, a constant for the orbiting bodies,
     * defined as the product of the Universal Gravitational Constant (G) and the mass of the object.
     *
     * @return the standard gravitational parameter = G * mass.
     */
    public double getStandardGravitationalParameter() {
        return Astrarium.G * this.getMass();
    }

    /**
     * Returns the velocity in m/s required to escape the current body at a given radius.
     *
     * @param radius in meters from the center of mass of the body.
     * @return the escape velocity in m/s.
     */
    public double getEscapeVelocity(double radius) {
        return Math.sqrt(2 * getStandardGravitationalParameter() / radius);
    }

    /**
     * Returns the Sphere of Influence (SoI) radius of the current body.
     * The SoI is, in other words, the radius defining the portion of space
     * where an object can be approximated to orbit only the current body.
     *
     * @return SoI radius in meters.
     */
    public double getSphereOfInfluence() {
        if (getOrbit() != null)
            return getOrbit().getSemiMajorAxis() * Math.pow(getMass() / getParent().getMass(), 2D / 5D);
        else // The root body of the system  will have infinite SoI
            return Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the Hill Sphere radius of the current body.
     * The Hill Sphere, similarly to the SoI delimits a region of space where the orbit will be stable.
     *
     * @return the radius of the Hill Sphere in meters.
     */
    public double getHillSphere() {
        if (getOrbit() != null)
            return getOrbit().getSemiMajorAxis() * (1 - getOrbit().getEccentricity())
                    * Math.pow(getMass() / (3 * getParent().getMass()), 1 / 3D);
        else // The root body of the system  will have infinite SoI
            return Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a velocity {@link Vector} that a spacecraft found at the given {@link Position}
     * must have to be in a circular {@link Orbit} around the reference body.
     *
     * @param position the position of the spacecraft.
     * @return The velocity relative to the body that the object must reach to be in a circular orbit.
     */
    public Vector getCircularOrbitVelocity(Position position) {
        double speed = sqrt(this.getStandardGravitationalParameter() / position.getMagnitude());

        double angle = position.getLongitude() + PI_BY_TWO;

        Vector velocity = new Vector(speed, 0, 0);

        velocity.rotateZ(angle);

        return velocity;
    }
    //endregion Calculations

    /**
     * Adds a {@link CelestialBody} as a child of the current body.
     *
     * @param celestialBody the body to add.
     */
    private void addChild(CelestialBody celestialBody) {
        this.children.add(celestialBody);
    }

    @Override
    public void renderAtTime(long time) {
        super.renderAtTime(time);

        for (CelestialBody child : this.getChildren()) {
            child.renderAtTime(time);
        }
    }

    //region toString
    @Override
    public String toString() {
        return getName();
    }
    //endregion toString
}
