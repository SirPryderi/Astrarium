package astrarium;

import astrarium.utils.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that contains all the orbits used by a spacecraft trajectory.
 * <p>
 * Created on 11/03/2017.
 *
 * @author Vittorio
 */
public class Route {
    /**
     * The spacecraft owning this Route.
     */
    private final Spacecraft spacecraft;

    /**
     * The orbit at the beginning of this Route.
     */
    private final Orbit startOrbit;

    /**
     * The list of nodes representing each manoeuvre of the Route.
     */
    private final List<Node> nodes;

    /**
     * Creates a new Route given the owner {@link Spacecraft} and its starting {@link Orbit}.
     *
     * @param spacecraft the spacecraft owning the Route.
     * @param startOrbit the initial parking orbit.
     */
    public Route(Spacecraft spacecraft, Orbit startOrbit) {
        this.spacecraft = spacecraft;
        this.nodes = new ArrayList<>();
        this.startOrbit = startOrbit;
    }

    /**
     * Adds a new orbit because the body has entered a new SoI.
     *
     * @param time    the time of the SoI change.
     * @param newBody the new primary body.
     */
    private void addSphereOfInfluenceChange(long time, Body newBody) {
        // TODO
    }

    /**
     * Adds a new burn to the list of nodes.
     *
     * @param time the time of the burn.
     * @param burn the components of the burn.
     */
    private void addBurn(long time, Vector burn) {
        // TODO
    }

    /**
     * The Node inner class contains information regarding the type of manoeuvre.
     */
    class Node {
        /**
         * The time when the node happens.
         */
        final long time;
        /**
         * The vector representing the direction and magnitude of the burn.
         */
        final Vector burn;
        /**
         * The new orbit as the result of the manoeuvre.
         */
        final Orbit orbit;

        /**
         * Creates a new change of orbit.
         *
         * @param time  time of the orbit change.
         * @param orbit the new orbit after the orbit change.
         */
        Node(long time, Orbit orbit) {
            this.time = time;
            this.orbit = orbit;
            this.burn = null;
        }

        /**
         * Creates a new manoeuvre.
         *
         * @param time  time of the manoeuvre.
         * @param burn  the burn vector.
         * @param orbit the new orbit after the burn.
         */
        Node(long time, Vector burn, Orbit orbit) {
            this.time = time;
            this.burn = burn;
            this.orbit = orbit;
        }

        /**
         * Return whether it's a propelled manoeuvre or not.
         *
         * @return {@code true} if fuel is used, {@code false} otherwise.
         */
        boolean isBurn() {
            return burn == null;
        }
    }


}
