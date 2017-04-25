package astrarium;

import org.jetbrains.annotations.NotNull;

/**
 * A class representing a man-made object, e.g. a vessel or a space station.
 * <p>
 * It is possible to give it a series of manoeuvres it can make.
 * <p>
 * Created on 27/02/2017.
 *
 * @author Vittorio
 */
public class Spacecraft extends Body {
    /**
     * The list of manoeuvres the spacecraft has planned.
     */
    private final Route route;

    /**
     * Creates a new spacecraft with the given information.
     *
     * @param name       name of the spacecraft - must be unique.
     * @param mass       dry mass of the spacecraft in kilograms.
     * @param height     maximum height of the spacecraft in meters.
     * @param width      maximum width of the spacecraft in meters.
     * @param startOrbit the initial parking orbit of the spacecraft.
     */
    public Spacecraft(String name, double mass, double height, double width, Orbit startOrbit) {
        super(name, mass, Math.hypot(width, height) / 2);
        route = new Route(this, startOrbit);
    }

    @Override
    @NotNull
    public Orbit getOrbit() {
        // todo
        return null;
    }

    @Override
    @NotNull
    public Orbit getOrbitAtTime(long time) {
        // todo
        return null;
    }
}
