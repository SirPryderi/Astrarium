package astrarium;

import org.jetbrains.annotations.NotNull;

/**
 * A class that wraps all the other objects of the simulation.
 * <p>
 * It is possible to forward the state of the whole system at a defined time using {@link #setTime(long)}.
 * <p>
 * Created on 20/02/2017.
 *
 * @author Vittorio
 */
public class Astrarium {
    /**
     * Newtonian constant of gravitation.
     */
    public static final double G = 6.67408e-11;

    /**
     * Main body of the system.
     */
    @NotNull
    private final CelestialBody root;

    /**
     * Time of the simulation.
     */
    private long time = 0; // ago in a galaxy far far away...

    /**
     * Constructor that instantiates the {@link Astrarium} object with its root element.
     *
     * @param root the root element of the system.
     */
    public Astrarium(@NotNull CelestialBody root) {
        this.root = root;
    }

    //region Getters

    /**
     * Returns the root element of the simulation.
     *
     * @return the root element of the simulation, e.g. the Sun.
     */
    @NotNull
    public CelestialBody getRoot() {
        return root;
    }

    /**
     * Returns the current time of the simulation expressed in the milliseconds since the Unix Epoch.
     *
     * @return the current time.
     */
    public long getTime() {
        return time;
    }
    //endregion Getters

    /**
     * Sets the time of the {@link Astrarium} to the given one.
     * <p>
     * It synchronises all the {@link Body} stored, so it is a time-consuming operation.
     *
     * @param time time in milliseconds.
     */
    public void setTime(long time) {
        this.time = time;

        root.renderAtTime(time);
    }
}
