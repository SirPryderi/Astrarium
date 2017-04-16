package astrarium;

import astrarium.utils.Mathematics;
import astrarium.utils.Position;
import astrarium.utils.Vector;
import org.jetbrains.annotations.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Date;

import static astrarium.utils.Mathematics.*;
import static java.lang.Math.*;

/**
 * A class that calculates all the required information
 * to compute the position of an object
 * from its reference body at a given time.
 * <p>
 * Created on 03-Nov-16.
 *
 * @author Vittorio
 */
@SuppressWarnings("WeakerAccess")
public final class Orbit {
    /**
     * Standard Gravitational Parameter.
     **/
    private final double STANDARD_GRAVITATIONAL_PARAMETER;

    /**
     * The reference body of the orbit.
     */
    private CelestialBody parent = null;

    //region Definition Parameters

    //region Orbital Parameters
    /**
     * The length of the semi-major axis in meters.
     */
    private double semiMajorAxis; // m
    /**
     * The eccentricity of the orbit.
     */
    private double eccentricity;
    //endregion

    //region Orbital Angles
    /**
     * The inclination of the orbit from the reference plane, along the nodal axis.
     */
    private double inclination; // rad
    /**
     * The angle between the reference direction and the ascending node.
     */
    private double longitudeOfAscendingNode; // rad
    /**
     * The angle on the orbital plane, between the ascending node and the periapsis of the orbit.
     */
    private double argumentOfPeriapsis; // rad
    /**
     * The angle of the mean anomaly at time 0. Used to define the phase of the orbit.
     */
    private double meanAnomalyAtEpoch; // rad

    //endregion

    //endregion

    //region Rendered Values
    // These values are calculated with #renderAtTime() and are stored only for performance purposes.
    /**
     * The position of the body from the parent when {@link #renderAtTime(long)} has been launched.
     */
    private Position _positionFromParent;
    /**
     * The position of the body on the orbital plane when {@link #renderAtTime(long)} has been launched.
     */
    private Position _positionFromOrbitalPlane;
    /**
     * The eccentric anomaly of the body when {@link #renderAtTime(long)} has been launched.
     */
    private double _eccentricAnomaly;
    //endregion

    //region Constructors

    /**
     * Creates an orbit with all its defining parameters.
     *
     * @param parent                   the reference body.
     * @param semiMajorAxis            in meters.
     * @param eccentricity             of the orbit.
     * @param inclination              in radians.
     * @param longitudeOfAscendingNode in radians.
     * @param argumentOfPeriapsis      in radians.
     * @param meanAnomalyAtEpoch       in radians.
     */
    public Orbit(CelestialBody parent, double semiMajorAxis, double eccentricity, double inclination,
                 double longitudeOfAscendingNode, double argumentOfPeriapsis, double meanAnomalyAtEpoch) {
        this.parent = parent;
        this.semiMajorAxis = semiMajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.longitudeOfAscendingNode = longitudeOfAscendingNode;
        this.argumentOfPeriapsis = argumentOfPeriapsis;
        this.meanAnomalyAtEpoch = meanAnomalyAtEpoch;
        if (parent != null) {
            this.STANDARD_GRAVITATIONAL_PARAMETER = parent.getStandardGravitationalParameter();
        } else {
            STANDARD_GRAVITATIONAL_PARAMETER = 0;
        }
    }

    /**
     * Creates an orbit with simplified parameters.
     * <p>
     * All the orbital angles are assumed to be zero.
     *
     * @param parent        the reference body.
     * @param semiMajorAxis in meters.
     * @param eccentricity  of the orbit.
     */
    public Orbit(CelestialBody parent, double semiMajorAxis, double eccentricity) {
        this(parent, semiMajorAxis, eccentricity, 0, 0, 0, 0);
    }
    //endregion Constructor

    //region Calculate Eccentric Anomaly

    /**
     * Uses Newton's method to calculate the Eccentric Anomaly from the Mean Anomaly.
     *
     * @param meanAnomaly  the mean anomaly.
     * @param eccentricity the eccentricity of the orbit.
     * @param precision    the number of decimal places.
     * @return The value of the eccentric anomaly.
     */
    public static double calculateEccentricAnomaly(double meanAnomaly, double eccentricity, double precision) {
        // values are confirmed to work here
        // This has been improved using the standard
        if (eccentricity < 0) {
            throw new RuntimeException("Eccentricity must be bigger than zero.");
        }

        if (eccentricity > 1) {
            throw new RuntimeException("Eccentricity > 1 not yet supported.");
        }

        final int MAX_ITERATIONS = 30;

        double delta = pow(10, -precision);

        double eccentricAnomaly;
        double zero;

        eccentricAnomaly = meanAnomaly;

        zero = eccentricAnomaly - eccentricity * sin(meanAnomaly) - meanAnomaly;

        // Evil loop here
        for (int i = 0; (abs(zero) > delta) && (i < MAX_ITERATIONS); i++) {
            // TODO Fix possible division by zero.
            eccentricAnomaly = eccentricAnomaly - zero / (1 - eccentricity * cos(eccentricAnomaly));
            zero = eccentricAnomaly - eccentricity * sin(eccentricAnomaly) - meanAnomaly;
        }

        // TODO This value should be normalised.
        return eccentricAnomaly;
    }
    //endregion calculateEccentricAnomaly

    //region Calculate Eccentricity

    /**
     * Utility method to calculate the eccentricity of an orbit given its semi-major and semi-minor axes.
     * <p>
     * Both values must be greater than zero,
     * and {@code a} must be greater than {@code b}.
     *
     * @param a Semi-major axis in meters.
     * @param b Semi-minor axis in meters.
     * @return the value of the eccentricity.
     */
    public static double calculateEccentricity(double a, double b) {
        if (b <= 0) throw new IllegalArgumentException("The semi-minor axis must be greater than zero.");
        if (a < b) throw new IllegalArgumentException("The semi-major axis must be greater than the semi-minor.");
        // No need to check if a is positive, because it needs to be greater than b.

        return sqrt(1 - (pow(b, 2) / pow(a, 2)));
    }
    //endregion Calculate Eccentricity

    //region Calculate Orbit from Radius and Velocity

    /**
     * Calculates the orbit around {@code body}, given the relative velocity and position from it.
     * It also regulates the mean anomaly at epoch to match the current position of the object.
     *
     * @param body     the reference body to calculate the orbit from.
     * @param position the position relative to the body.
     * @param velocity the velocity relative to the body.
     * @param time     time when the measurement were made.
     * @return the {@link Orbit} object representing the final orbit.
     */
    // FIXME
    public static Orbit calculateOrbitFromPositionAndVelocity(CelestialBody body, Position position, Vector velocity, long time) {
        Orbit orbit = calculateOrbitFromPositionAndVelocity(body, position, velocity);

        //orbit.renderAtTime(time);

        double trueAnomaly = orbit.getPeriapsisPosition().getAngleWith(position);

        double meanAnomalyFromAngle = orbit.getMeanAnomalyFromAngle(trueAnomaly);

        double actualAnomaly = normaliseAngle(orbit.getMeanAnomaly(time));

//        System.out.println("=========");
//
//        System.out.println(orbit.getMeanAnomalyFromAngle(orbit.getTrueAnomaly(time)));
//        System.out.println(normaliseAngle(orbit.getMeanAnomaly(time)));
//
//        System.out.println("=========");

        // TODO remove debug output

        System.out.println("Wanted M: " + Math.toDegrees(meanAnomalyFromAngle));

        System.out.println("Actual M: " + Math.toDegrees(actualAnomaly));

        orbit.meanAnomalyAtEpoch = normaliseAngle(meanAnomalyFromAngle - actualAnomaly);

        System.out.println("Final M: " + Math.toDegrees(normaliseAngle(orbit.getMeanAnomaly(time))));
        System.out.println("Final T: " + Math.toDegrees(normaliseAngle(orbit.getTrueAnomaly(time))));


        Date date = new Date();
        date.setTime(time);

        System.out.println(date);

        return orbit;
    }


    /**
     * Calculates the orbit around {@code body}, given the relative velocity and position from it.
     *
     * @param body     the reference body to calculate the orbit from.
     * @param position the position relative to the body.
     * @param velocity the velocity relative to the body.
     * @return the {@link Orbit} object representing the final orbit.
     */
    // FIXME too
    public static Orbit calculateOrbitFromPositionAndVelocity(CelestialBody body, Position position, Vector velocity) {
        boolean isEquatorial = Mathematics.equals(position.getZ(), 0) && Mathematics.equals(velocity.getZ(), 0);

        Vector angularMomentum = position.crossProduct(velocity);

        double distance = position.getMagnitude();
        double speedSquared = velocity.getMagnitudeSquared();

        double standardGravitationalParameter = body.getMass() * Astrarium.G;

        Vector eccentricityVector =
                position.product(speedSquared - standardGravitationalParameter / distance)
                        .minus(velocity.product(position.dotProduct(velocity)))
                        .divided(standardGravitationalParameter);

        double eccentricity = eccentricityVector.getMagnitude();

        if (Mathematics.equals(eccentricity, 0))
            eccentricity = 0;

        double longitudeOfAscendingNode = 0;
        double inclination = 0;
        double argumentOfPeriapsis = 0;

        System.out.println("\n\n ++++");

        if (!isEquatorial) {
            System.out.println("Not equatorial");

            Vector zAxisUnitVector = new Vector(0, 0, 1);

            Vector nodeAxisVector = zAxisUnitVector.crossProduct(angularMomentum);

            longitudeOfAscendingNode = acos(nodeAxisVector.getX() / nodeAxisVector.getMagnitude());

            if (longitudeOfAscendingNode == Double.NaN) {
                throw new RuntimeException("Longitude of ascending node is NaN!");
            }

            if (nodeAxisVector.getY() < 0)
                longitudeOfAscendingNode = TWO_PI - longitudeOfAscendingNode;

            if (eccentricity != 0) {
                argumentOfPeriapsis = acos(nodeAxisVector.dotProduct(eccentricityVector)
                        / nodeAxisVector.getMagnitude()
                        / eccentricity);

                if (Double.isNaN(argumentOfPeriapsis))
                    argumentOfPeriapsis = 0;
            }

            inclination = acos(angularMomentum.getZ() / angularMomentum.getMagnitude());

            System.out.println("Node axis " + nodeAxisVector);

            System.out.println("LoAN " + longitudeOfAscendingNode);

            System.out.println("AoP " + argumentOfPeriapsis);

            System.out.println("Inclination " + inclination);
        } else {
            if (eccentricity != 0)
                argumentOfPeriapsis = eccentricityVector.getLongitude();
            // TODO
        }

        double specificOrbitalEnergy = speedSquared / 2 - standardGravitationalParameter / distance;

        double semiMajorAxis = -standardGravitationalParameter / (2 * specificOrbitalEnergy);

        return new Orbit(
                body,
                semiMajorAxis,
                eccentricity,
                inclination,
                longitudeOfAscendingNode,
                argumentOfPeriapsis,
                0
        );
    }
    //endregion

    //region Eccentricity

    /**
     * Returns the eccentricity of the orbit.
     *
     * @return the eccentricity.
     */
    public double getEccentricity() {
        return eccentricity;
    }
    //endregion

    //region Axis

    /**
     * Returns the length of the semi-major axis in meters.
     *
     * @return length of the semi-major axis in meters.
     */
    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    /**
     * Returns the length of the semi-minor axis in meters.
     *
     * @return length of the semi-minor axis in meters.
     */
    // TODO these kind of values dependant on orbital parameters should be always stored for performance reason.
    public double getSemiMinorAxis() {
        return sqrt(semiMajorAxis * getSemiLatusRectum());
    }

    /**
     * Returns the distance between the geometrical center of the ellipse and one of the focuses expressed in meters.
     *
     * @return distance between the center and the focus.
     */
    public double getFocusDistance() {
        return this.semiMajorAxis * this.eccentricity;
    }
    //endregion Axis

    //region Radii

    /**
     * Returns the length of the semi-latus rectum of the ellipse expressed in meters.
     *
     * @return length of the semi-latus rectum.
     */
    public double getSemiLatusRectum() {
        switch (getOrbitType()) {
            case CIRCULAR:
                return this.getSemiMajorAxis();
            case PARABOLIC:
                return 2 * getPeriapsis();
            default:
                return abs(this.getSemiMajorAxis() * (1 - pow(this.getEccentricity(), 2)));
        }

    }

    /**
     * Returns the length of the orbit radius at a given the true anomaly {@code theta}.
     *
     * @param theta true anomaly of the orbit in radians.
     * @return radius of the orbit.
     */
    public double getRadius(double theta) {
        switch (getOrbitType()) {
            case CIRCULAR:
                return semiMajorAxis;
            case PARABOLIC:
                return 2 * getPeriapsis() / (1 + cos(theta));
            default:
                return (getSemiLatusRectum()) / (1 + eccentricity * cos(theta));
        }

    }

    /**
     * Returns the altitude of the apoapsis, the point of maximum distance from the reference body,
     * from its center of mass. The value is expressed in meters.
     *
     * @return length of the apoapsis.
     */
    public double getApoapsis() {
        return this.getSemiMajorAxis() * (1 + this.getEccentricity());
    }

    /**
     * * Returns the altitude of the periapsis, the point of minimum distance from the reference body,
     * from its center of mass. The value is expressed in meters.
     *
     * @return length of the periapsis.
     */
    public double getPeriapsis() {
        return this.getSemiMajorAxis() * (1 - this.getEccentricity());
    }
    //endregion

    //region Angles

    /**
     * Returns the inclination of the orbit from the reference plane, expressed in radians.
     *
     * @return inclination of the orbit.
     */
    public double getInclination() {
        return inclination;
    }

    /**
     * Returns the angle between the reference direction and the ascending node,
     * expressed in radians.
     *
     * @return longitude of the ascending node.
     */
    public double getLongitudeOfAscendingNode() {
        return longitudeOfAscendingNode;
    }

    /**
     * Returns the angle between the periapsis and the ascending node, calculated on the orbital plane, with value in radians.
     *
     * @return the argument of periapsis.
     */
    public double getArgumentOfPeriapsis() {
        return argumentOfPeriapsis;
    }

    /**
     * Returns the value of the mean anomaly at the epoch, or t = 0, of the simulation, expressed in radians.
     * Used to describe the phase of the orbit.
     *
     * @return mean anomaly at epoch.
     */
    public double getMeanAnomalyAtEpoch() {
        return meanAnomalyAtEpoch;
    }

    /**
     * Returns the angle of the line tangent to the orbit
     * at the current position calculated with {@link #renderAtTime(long)}.
     * Value in radians.
     * <p>
     * This also describe the direction of the velocity vector on the orbital plane.
     * <p>
     * See: <a href="https://en.wikipedia.org/wiki/Ellipse#General_parametric_form">Wikipedia</a>
     *
     * @return angle of the tangent direction.
     */
    public double getTangentAngle() {
        Position center = new Position(-getFocusDistance(), 0);
        double theta = center.angleOfLineBetweenThisAnd(_positionFromOrbitalPlane);

        double tangent = -atan2(1 - (eccentricity * eccentricity), tan(theta));

        if (-PI_BY_TWO < theta && theta < PI_BY_TWO) {
            tangent += PI;
        }

        return tangent;
    }
    //endregion Angles

    //region Energy

    /**
     * Returns the specific orbital energy, or vis-viva energy, of the orbit the sum of the potential and kinetic energy of both orbited and orbiting bodies.
     * This value does not vary with time.
     * Expressed in J/kg.
     *
     * @return the specific orbital energy.
     */
    public double getSpecificOrbitalEnergy() {
        if (getOrbitType() == OrbitType.PARABOLIC)
            return 0;

        return -((STANDARD_GRAVITATIONAL_PARAMETER) / (2 * this.getSemiMajorAxis()));
    }
    //endregion

    //region Velocities

    /**
     * Returns the magnitude of the velocity in m/s at the given {@code radius}.
     * The velocity is relative to the reference body.
     *
     * @param radius the distance of the object from the center of mass of the reference body.
     * @return magnitude of the velocity.
     */
    public double getVelocityMagnitudeAtRadius(double radius) {
        switch (getOrbitType()) {
            case CIRCULAR:
                return getMeanVelocity();
            case PARABOLIC:
                return sqrt(2 * STANDARD_GRAVITATIONAL_PARAMETER / radius);
            default:
                return sqrt(STANDARD_GRAVITATIONAL_PARAMETER * (2 / radius - 1 / getSemiMajorAxis()));
        }
    }


    public Vector getVelocityAtAtAngle(double theta) {
//        Vector velocity = Vector.getDirectionVector(getTangentAngleFromRadius());
//
//        velocity.multiplied(getVelocityMagnitudeAtAngle(theta));
//
//        velocity = rotateOnOrbitalPlane(velocity);
//
//        return velocity;
        // TODO
        throw new NotImplementedException();
    }

    /**
     * Returns the magnitude of the velocity in m/s at the given true anomaly {@code theta}.
     *
     * @param theta the angle between the reference body and the orbiting object.
     * @return magnitude of the velocity.
     */
    public double getVelocityMagnitudeAtAngle(double theta) {
        return getVelocityMagnitudeAtRadius(getRadius(theta));
    }

    /**
     * Returns the magnitude of the velocity in m/s when the object was rendered with {@link #renderAtTime(long)}.
     * <p>
     * Note: this value is meaningless if retrieved before {@link #renderAtTime(long)} is launched!
     *
     * @return rendered magnitude of the velocity.
     */
    public double getVelocityMagnitude() {
        return getVelocityMagnitudeAtRadius(getRenderedPositionFromParent().getMagnitude());
    }

    /**
     * The speed of an equivalent circular orbit with the same period. Average speed of the actual orbit.
     *
     * @return average speed in m/s.
     */
    public double getMeanVelocity() {
        return sqrt(STANDARD_GRAVITATIONAL_PARAMETER / semiMajorAxis);
    }

    /**
     * Returns the value of the mean motion,
     * the angular speed of an equivalent circular orbit to complete the orbit
     * in the time of the orbital period.
     * Expressed in rad/s.
     *
     * @return the mean motion of the orbit.
     */
    public double getMeanMotion() {
        //return 2 * Mathematics.PI / getPeriod();
        return sqrt(STANDARD_GRAVITATIONAL_PARAMETER / pow(semiMajorAxis, 3));
    }

    /**
     * Returns the areal velocity, how fast an object "sweeps" an area of orbit with its movement, in m<sup>2</sup>/s.
     *
     * @return the areal velocity.
     */
    public double getArealVelocity() {
        switch (getOrbitType()) {

            case CIRCULAR:
                return sqrt(STANDARD_GRAVITATIONAL_PARAMETER * semiMajorAxis);
            case ELLIPTICAL:
                return sqrt(STANDARD_GRAVITATIONAL_PARAMETER * semiMajorAxis
                        * (1 + eccentricity) / (1 - eccentricity));
            case PARABOLIC:
                return sqrt(STANDARD_GRAVITATIONAL_PARAMETER * getPeriapsis() / 2);
            case HYPERBOLIC:
                return sqrt(-STANDARD_GRAVITATIONAL_PARAMETER * semiMajorAxis
                        * (1 + eccentricity) / (1 - eccentricity));
        }

        throw new Error("This is very bad.");
    }
    //endregion Velocities

    //region Anomalies

    /**
     * Returns the cosine of the eccentric anomaly given the true anomaly {@code theta}.
     *
     * @param theta the true anomaly angle.
     * @return the cosine of the eccentric anomaly.
     */
    private double cosOfEccentricAnomaly(double theta) {
        return (eccentricity + cos(theta) / (1 + eccentricity * cos(theta)));
    }

    /**
     * Returns the eccentric anomaly in radians given the true anomaly {@code theta}.
     *
     * @param theta the true anomaly angle.
     * @return the true eccentric anomaly of the orbit.
     */
    public double getEccentricAnomalyFromTrueAnomaly(double theta) {
        switch (getOrbitType()) {
            case CIRCULAR:
                return theta;
            case ELLIPTICAL:
                //    return acos(cosOfEccentricAnomaly(theta));
                return atan2(sqrt(1 - eccentricity * eccentricity) * sin(theta), eccentricity + cos(theta));
            // TODO check performances of the two.
            case PARABOLIC:
                return tan(theta / 2);
            case HYPERBOLIC:
                return acosh(cosOfEccentricAnomaly(theta));
        }

        throw new Error("oops");
    }

    /**
     * Returns the angle in an imaginary circular orbit corresponding to a planet's eccentric anomaly.
     *
     * @param time the time in milliseconds.
     * @return the mean anomaly in radians.
     */
    public double getMeanAnomaly(long time) {
        return (sqrt(STANDARD_GRAVITATIONAL_PARAMETER / pow(semiMajorAxis, 3)) * (time / 1000D)) + meanAnomalyAtEpoch;
    }

    /**
     * Calculates the mean anomaly at a given {@code time}.
     *
     * @param time time in milliseconds.
     * @return the mean anomaly.
     */
    public double calculateEccentricAnomaly(long time) {
        double meanAnomaly = getMeanAnomaly(time);
        return calculateEccentricAnomaly(meanAnomaly, eccentricity, 10);
    }

    /**
     * Calculates the mean anomaly at a given true anomaly {@code theta}.
     *
     * @param theta true anomaly in radians.
     * @return the mean anomaly.
     */
    public double getMeanAnomalyFromAngle(double theta) {
        double eccentricAnomaly = getEccentricAnomalyFromTrueAnomaly(theta);

        switch (getOrbitType()) {
            case CIRCULAR:
                return eccentricAnomaly;
            case ELLIPTICAL:
                return eccentricAnomaly - eccentricity * sin(eccentricAnomaly);
            case PARABOLIC:
                return eccentricAnomaly + pow(eccentricAnomaly, 3) / 3;
            case HYPERBOLIC:
                return eccentricity * sinh(eccentricAnomaly) - eccentricAnomaly;
        }

        throw new Error("oops");
    }

    /**
     * Returns the true anomaly expressed in radians at a given {@code time} in milliseconds.
     * From: <a href="https://en.wikipedia.org/wiki/True_anomaly#From_the_eccentric_anomaly">Wikipedia</a>
     *
     * @param time in milliseconds.
     * @return the true anomaly.
     */
    public double getTrueAnomaly(long time) {
        // Taken straight from wikipedia, should be okay
        double eccentricAnomaly = calculateEccentricAnomaly(time);

        double y = sqrt(1 - eccentricity) * cos(eccentricAnomaly / 2);
        double x = sqrt(1 + eccentricity) * sin(eccentricAnomaly / 2);

        return 2 * atan2(y, x);
    }

    /**
     * Returns the true anomaly expressed in radians at the time it was rendered with {@link #renderAtTime(long)}.
     * <p>
     * Note: this value is meaningless if use before {@link #renderAtTime(long)}!
     *
     * @return the true anomaly.
     */
    public double getTrueAnomaly() {
        return atan2(getRenderedPositionFromParent().getY(), getRenderedPositionFromParent().getX());
    }
    //endregion

    //region Times

    /**
     * Returns the period of the orbit in milliseconds.
     *
     * @return the period of the orbit.
     */
    public long getPeriod() {
        return new Double(
                2000D * PI * sqrt(pow(semiMajorAxis, 3) / STANDARD_GRAVITATIONAL_PARAMETER)
        ).longValue();
    }

    /**
     * Returns the time elapsed from the last passage from the periapsis,
     * at the given true anomaly {@code theta}.
     *
     * @param theta the true anomaly.
     * @return the time from periapsis in milliseconds.
     */
    public long getTimeFromPeriapsis(double theta) {
        if (eccentricity >= 1)
            throw new RuntimeException("Not implemented");
        return new Double(getMeanAnomalyFromAngle(theta) * 1000D * getPeriod() / (2D * PI)).longValue();
    }
    //endregion

    //region Positions

    /**
     * Returns the position from the reference body at a given {@code time} in milliseconds.
     *
     * @param time time in milliseconds.
     * @return position of the orbiting object.
     */
    @NotNull
    public Position getPositionFromParent(long time) {
        return rotatePositionOnOrbitalPlane(
                getPositionOnOrbitalPlaneFromEccentricAnomaly(
                        calculateEccentricAnomaly(time)
                )
        );
    }

    /**
     * * Returns the position from the reference body at a given the true anomaly {@code theta} in radians.
     *
     * @param theta true anomaly in radians.
     * @return position of the orbiting object.
     */
    @NotNull
    public Position getPositionFromParentAtAngle(double theta) {
        Position position = new Position();

        position.setValues(Position.getDirectionVector(theta));

        position.multiplied(getRadius(theta));

        return rotatePositionOnOrbitalPlane(position);
    }

    /**
     * Returns the position from the reference body when {@link #renderAtTime(long)} was launched.
     *
     * @return the position of the orbiting body.
     */
    public Position getRenderedPositionFromParent() {
        return this._positionFromParent;
    }

    /**
     * Returns the position from the orbital plane when {@link #renderAtTime(long)} was launched.
     *
     * @return the position of the orbiting body.
     */
    public Position getRenderedPositionFromOrbitalPlane() {
        return this._positionFromOrbitalPlane;
    }

    /**
     * Returns the position from the system root when {@link #renderAtTime(long)} was launched.
     *
     * @return the position of the orbiting body.
     */
    public Position getRenderedAbsolutePosition() {
        return (Position) this.getRenderedPositionFromParent().plus(this.parent.getPosition());
    }

    /**
     * Returns the position from the orbital plane at a given eccentric anomaly {@code E}.
     *
     * @param E the eccentric anomaly.
     * @return position of the orbiting object.
     */
    public Position getPositionOnOrbitalPlaneFromEccentricAnomaly(double E) {
        double C = cos(E);

        double S = sin(E);

        double x = semiMajorAxis * (C - eccentricity);

        double y = semiMajorAxis * sqrt(1D - eccentricity * eccentricity) * S;

        return new Position(x, y);
    }
    //endregion Positions

    //region Nodes

    /**
     * Returns the coordinates of the Periapsis from the parent body.
     *
     * @return periapsis coordinates,
     */
    public Position getPeriapsisPosition() {
        return rotatePositionOnOrbitalPlane(new Position(getPeriapsis(), 0));
    }

    /**
     * Returns the coordinates of the Apoapsis from the parent body.
     *
     * @return apoapsis coordinates.
     */
    public Position getApoapsisPosition() {
        return rotatePositionOnOrbitalPlane(new Position(-getApoapsis(), 0));
    }

    /**
     * Returns the coordinates of the northern intersection
     * between the semi-minor axis and the orbit.
     * The coordinates are relative to the parent body.
     *
     * @return northern node coordinates.
     */
    public Position getNorthernVertex() {
        return rotatePositionOnOrbitalPlane(new Position(-getFocusDistance(), getSemiMinorAxis()));
    }

    /**
     * Returns the coordinates of the southern intersection
     * between the semi-minor axis and the orbit.
     * The coordinates are relative to the parent body.
     *
     * @return northern node coordinates.
     */
    public Position getSouthernVertex() {
        return rotatePositionOnOrbitalPlane(new Position(-getFocusDistance(), -getSemiMinorAxis()));
    }

    /**
     * Returns the coordinates of the center,
     * or the intersection between the two axis.
     * The coordinates are relative to the parent body.
     *
     * @return ellipse center coordinates.
     */
    public Position getCenter() {
        return rotatePositionOnOrbitalPlane(new Position(-getFocusDistance(), 0));
    }
    //endregion

    //region Plane Rotations

    /**
     * Converts a {@link Position} from the orbital plane to the reference plane,
     * applying the three orbital rotations.
     * <p>
     * Note: the original position is not altered.
     *
     * @param position position to rotate
     * @return the rotated position.
     */
    public Position rotatePositionOnOrbitalPlane(@NotNull Position position) {
        return (Position) rotateOnOrbitalPlane(position);
    }

    /**
     * Converts a {@link Vector} from the orbital plane to the reference plane,
     * applying the three orbital rotations.
     * <p>
     * Note: the original vector is not altered.
     *
     * @param vector position to rotate
     * @return the rotated vector.
     */
    public Vector rotateOnOrbitalPlane(@NotNull Vector vector) {
        Vector rotatedVector = vector.getCopy();

        if (inclination != 0) {
            Vector nodeAxis = Vector.getDirectionVector(longitudeOfAscendingNode);
            Vector normalAxis = new Vector(0, 0, 1);

            normalAxis.rotate(nodeAxis, inclination);
            //normalAxis.rotateZ(longitudeOfAscendingNode - PI / 2);
            // TODO the normal axis calculation seems to be okay, but there might be a better way of finding it.

            rotatedVector.rotate(nodeAxis, inclination);
            rotatedVector.rotateZ(longitudeOfAscendingNode);
            rotatedVector.rotate(normalAxis, argumentOfPeriapsis);
        } else {
            rotatedVector.rotateZ(longitudeOfAscendingNode + argumentOfPeriapsis);
            // AKA longitude of periapsis
        }

        return rotatedVector;
    }
    //endregion

    //region To String
    @Override
    public String toString() {
        switch (getOrbitType()) {
            case CIRCULAR:
                return String.format(
                        "Circular orbit around %s. Radius %.2f km.",
                        getParent().getName(),
                        semiMajorAxis / 1000);
            case ELLIPTICAL:
                return String.format(
                        "Elliptical orbit around %s. Semi-major axis: %.2f km. Eccentricity: %.4f",
                        getParent().getName(),
                        semiMajorAxis / 1000,
                        eccentricity);
            default:
                return "Very odd orbit.";
        }

    }
    //endregion ToString

    //region Get Orbit type

    /**
     * Returns an {@link OrbitType} that describes the geometry of the orbit.
     *
     * @return geometry of the orbit.
     */
    public OrbitType getOrbitType() {
        if (eccentricity < 0)
            throw new Error("Eccentricity should NEVER be negative.");
        if (eccentricity == 0)
            return OrbitType.CIRCULAR;
        if (0 < eccentricity && eccentricity < 1)
            return OrbitType.ELLIPTICAL;
        if (eccentricity == 1)
            return OrbitType.PARABOLIC;
        else
            return OrbitType.HYPERBOLIC;
    }
    //endregion

    //region Render At Time

    /**
     * Renders the position of the given object at the time specified
     * and store the rendered parameters for being retrieved later
     * with methods like #getRenderedPositionFromParent.
     *
     * @param time in milliseconds.
     * @see #getRenderedAbsolutePosition()
     * @see #getRenderedPositionFromOrbitalPlane()
     * @see #getRenderedPositionFromParent()
     * @see #getEccentricity()
     */
    public void renderAtTime(long time) {
        this._eccentricAnomaly = calculateEccentricAnomaly(time);
        this._positionFromOrbitalPlane = getPositionOnOrbitalPlaneFromEccentricAnomaly(this._eccentricAnomaly);
        this._positionFromParent = rotatePositionOnOrbitalPlane(this._positionFromOrbitalPlane.getCopy());
    }
    //endregion

    //region Get Parent

    /**
     * Returns the reference body of the orbit.
     *
     * @return parent body.
     */
    public CelestialBody getParent() {
        return parent;
    }
    //endregion

    //region Orbit Type

    /**
     * An enumeration describing the geometry of the orbit.
     */
    public enum OrbitType {
        /**
         * Circular orbit.
         * Eccentricity = 0.
         */
        CIRCULAR,
        /**
         * Elliptical orbit.
         * Eccentricity &lt; 1.
         */
        ELLIPTICAL,
        /**
         * Parabolic orbit.
         * Eccentricity = 1.
         */
        PARABOLIC,
        /**
         * Hyperbolic orbit.
         * Eccentricity &gt; 1.
         */
        HYPERBOLIC
    }
    //endregion
}