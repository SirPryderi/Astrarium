package astrarium;

import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a natural body of the Solar System e.g. Stars, Planets, comets, etc.
 * <p>
 * Created  on 28-Oct-16.
 *
 * @author Vittorio
 */
public class CelestialBody extends Body {
    // List of children Bodies
    private final List<CelestialBody> children;

    // Orbit
    private Orbit orbit;

    //region Constructors
    public CelestialBody(String name, double mass, double radius, Orbit orbit) {
        super(name, mass, radius);
        this.orbit = orbit;

        this.children = new ArrayList<>();

        if (orbit != null) {
            orbit.getParent().addChild(this);
        }
    }

    public CelestialBody(String name, double mass, double radius) {
        this(name, mass, radius, null);
    }
    //endregion Constructors

    //region Getters
    public List<CelestialBody> getChildren() {
        return children;
    }

    @Nullable
    public CelestialBody getParent() {
        return getOrbit().getParent();
    }

    @Nullable
    public Orbit getOrbit() {
        return orbit;
    }
    //endregion Getters

    //region Calculations
    public double getStandardGravitationalParameter() {
        return Astrarium.G * this.getMass();
    }

    public double getEscapeVelocity(Double radius) {
        return Math.sqrt(2 * getStandardGravitationalParameter() / radius);
    }

    public double getSphereOfInfluence() {

        if (orbit != null)
            return getOrbit().getSemiMajorAxis() * Math.pow(getMass() / getParent().getMass(), 2D / 5D);
        else // The root body of the system  will have infinite SoI
            return Double.POSITIVE_INFINITY;
    }

    public double getHillSphere() {
        if (orbit != null)
            return getOrbit().getSemiMajorAxis() * (1 - getOrbit().getEccentricity())
                    * Math.pow(getMass() / (3 * getParent().getMass()), 1 / 3D);
        else // The root body of the system  will have infinite SoI
            return Double.POSITIVE_INFINITY;
    }
    //endregion Calculations

    public boolean addChild(CelestialBody celestialBody) {
        return this.children.add(celestialBody);
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
