package astrarium;

import astrarium.utils.Mathematics;
import astrarium.utils.Position;
import astrarium.utils.Vector;
import org.jetbrains.annotations.NotNull;

import static astrarium.utils.Mathematics.acosh;

/**
 * A class that calculates all the required information to compute the _positionFromParent of something at a given time.
 * <p>
 * Created on 03-Nov-16.
 *
 * @author Vittorio
 */
@SuppressWarnings("WeakerAccess")
public final class Orbit {
    // Standard Gravitational Parameter
    private final double STANDARD_GRAVITATIONAL_PARAMETER;

    // Hierarchy
    private CelestialBody parent = null;

    // Orbital Parameters
    private double semiMajorAxis; // m
    private double eccentricity;

    // Orbital Angles
    private double inclination; // rad
    private double longitudeOfAscendingNode; // rad
    private double argumentOfPeriapsis; // rad
    private double meanAnomalyAtEpoch; // rad

    // Values that are rendered and cached
    private Position _positionFromParent;
    private Position _positionFromOrbitalPlane;
    private double _eccentricAnomaly;

    //region Constructors
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

    public Orbit(CelestialBody parent, double semiMajorAxis, double eccentricity) {
        this(parent, semiMajorAxis, eccentricity, 0, 0, 0, 0);
    }
    //endregion Constructor

    //region calculateEccentricAnomaly

    /**
     * Uses Newton's method to calculate the Eccentric Anomaly from the Mean Anomaly
     *
     * @param meanAnomaly  The Mean Anomaly
     * @param eccentricity Eccentricity of the orbit
     * @param precision    the number of decimal places
     * @return Eccentric Anomaly
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

        double delta = Math.pow(10, -precision);

        double eccentricAnomaly;
        double zero;

        eccentricAnomaly = meanAnomaly;

        zero = eccentricAnomaly - eccentricity * Math.sin(meanAnomaly) - meanAnomaly;

        // Evil loop here
        for (int i = 0; (Math.abs(zero) > delta) && (i < MAX_ITERATIONS); i++) {
            // TODO Fix possible division by zero.
            eccentricAnomaly = eccentricAnomaly - zero / (1 - eccentricity * Math.cos(eccentricAnomaly));
            zero = eccentricAnomaly - eccentricity * Math.sin(eccentricAnomaly) - meanAnomaly;
        }

        // TODO This value should be normalised.
        return eccentricAnomaly;
    }
    //endregion calculateEccentricAnomaly

    //region Calculate Eccentricity
    public static double calculateEccentricity(double a, double b) {
        return Math.sqrt(1 - (Math.pow(b, 2) / Math.pow(a, 2)));
    }
    //endregion Calculate Eccentricity

    //region Calculate Orbit from Radius and Velocity
    // FIXME
    @NotNull
    public static Orbit calculateOrbitFromPositionAndVelocity(CelestialBody body, Position position, Vector velocity) {
        boolean isEquatorial = false;

        if (position.getZ() == 0 && velocity.getZ() == 0)
            isEquatorial = true;

        Vector angularMomentum = position.crossProduct(velocity);

        double distance = position.getMagnitude();
        double speedSquared = velocity.getMagnitudeSquared();

        double standardGravitationalParameter = body.getMass() * Astrarium.G;

        Vector eccentricityVector =
                position.product(speedSquared - standardGravitationalParameter / distance)
                        .minus(velocity.product(position.dotProduct(velocity)))
                        .multiplied(1 / standardGravitationalParameter);

        double eccentricity = eccentricityVector.getMagnitude();


        double longitudeOfAscendingNode = 0;
        double argumentOfPeriapsis = 0;
        if (!isEquatorial) {
            Vector zAxisUnitVector = new Vector(0, 0, 1);

            Vector nodeAxisVector = zAxisUnitVector.crossProduct(angularMomentum);

            longitudeOfAscendingNode = Math.acos(nodeAxisVector.getX() / nodeAxisVector.getMagnitude());

            if (longitudeOfAscendingNode == Double.NaN) {
                throw new RuntimeException("Longitude of ascending node is NaN!");
            }

            argumentOfPeriapsis = Math.acos(nodeAxisVector.dotProduct(eccentricityVector) / nodeAxisVector.getMagnitude() / eccentricity);

            if (nodeAxisVector.getY() < 0)
                longitudeOfAscendingNode = Math.PI * 2 - longitudeOfAscendingNode;

            System.out.println("Node axis " + nodeAxisVector);

            System.out.println("LoAN " + longitudeOfAscendingNode);

            System.out.println("AoP " + argumentOfPeriapsis);
        } else {
            longitudeOfAscendingNode = 0;
        }

        double specificOrbitalEnergy = speedSquared / 2 - standardGravitationalParameter / distance;

        double semiMajorAxis = -standardGravitationalParameter / (2 * specificOrbitalEnergy);

        double inclination = Math.acos(angularMomentum.getZ() / angularMomentum.getMagnitude());

        System.out.println("Inclination " + inclination);

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
    public double getEccentricity() {
        return eccentricity;
    }
    //endregion

    //region Axis
    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public double getSemiMinorAxis() {
        return Math.sqrt(semiMajorAxis * getSemiLatusRectum());
    }

    public double getFocusDistance() {
        return this.semiMajorAxis * this.eccentricity;
    }
    //endregion Axis

    //region Radii
    public double getSemiLatusRectum() {
        switch (getOrbitType()) {
            case CIRCULAR:
                return this.getSemiMajorAxis();
            case PARABOLIC:
                return 2 * getPeriapsis();
            default:
                return Math.abs(this.getSemiMajorAxis() * (1 - Math.pow(this.getEccentricity(), 2)));
        }

    }

    public double getRadius(double theta) {
        switch (getOrbitType()) {
            case CIRCULAR:
                return semiMajorAxis;
            case PARABOLIC:
                return 2 * getPeriapsis() / (1 + Math.cos(theta));
            default:
                return (getSemiLatusRectum()) / (1 + eccentricity * Math.cos(theta));
        }

    }

    public double getApoapsis() {
        return this.getSemiMajorAxis() * (1 + this.getEccentricity());
    }

    public double getPeriapsis() {
        return this.getSemiMajorAxis() * (1 - this.getEccentricity());
    }
    //endregion

    //region Angles
    public double getInclination() {
        return inclination;
    }

    public double getLongitudeOfAscendingNode() {
        return longitudeOfAscendingNode;
    }

    public double getArgumentOfPeriapsis() {
        return argumentOfPeriapsis;
    }

    public double getMeanAnomalyAtEpoch() {
        return meanAnomalyAtEpoch;
    }

    public double getTangentVector() {
        return getTangentVector3();
    }

    public double getTangentVector0() {
        return _eccentricAnomaly + Math.PI / 2;
    }

    public double getTangentVector1() {
        double vx = this.getSemiMajorAxis() * Math.sqrt(1D + Math.pow(Math.tan(_eccentricAnomaly), 2D));


        if (getRenderedPositionFromParent().getX() > 0) {

        } else if (getRenderedPositionFromParent().getX() < 0) {
            vx = -vx;
        } else {
            return _eccentricAnomaly + Math.PI / 2;
        }

        vx -= getFocusDistance();

        Position vPoint = new Position(vx, 0, 0);

        double v = vPoint.angleOfLineBetweenThisAnd(getRenderedPositionFromParent());

        //return getPositionFromParent().angleOfLineBetweenThisAnd(vPoint);
        //return e;

        if (getRenderedPositionFromParent().getX() > 0 && getRenderedPositionFromParent().getY() > 0) {
            v += Math.PI;
        }

        if (getRenderedPositionFromParent().getX() < 0 && getRenderedPositionFromParent().getY() < 0) {
            v += Math.PI;
        }

        v += Math.PI;

        return v;
//
//
    }

    public double getTangentVector2() {
        return -Math.atan2(getSemiMinorAxis(), getSemiMajorAxis() * Math.tan(_eccentricAnomaly));
    }

    public double getTangentVector3() {
        Position center = new Position(-getFocusDistance(), 0);
        double theta = center.angleOfLineBetweenThisAnd(_positionFromOrbitalPlane);

        // https://en.wikipedia.org/wiki/Ellipse#General_parametric_form

        double tangent = -Math.atan2(1 - (eccentricity * eccentricity), Math.tan(theta));

        double PI_BY_TWO = Math.PI / 2;

        if (-PI_BY_TWO < theta && theta < PI_BY_TWO) {
            tangent += Math.PI;
        }

        return tangent;
    }

    public double getTangentVector4() {

        return Math.atan2(1 - (eccentricity * eccentricity), Math.tan(_eccentricAnomaly));
    }
    //endregion Angles

    //region Energy
    public double getSpecificOrbitalEnergy() {
        if (getOrbitType() == orbitType.PARABOLIC)
            return 0;

        return -((STANDARD_GRAVITATIONAL_PARAMETER) / (2 * this.getSemiMajorAxis()));
    }
    //endregion

    //region Velocities
    public double getVelocityAtRadius(double radius) {
        switch (getOrbitType()) {
            case CIRCULAR:
                return getMeanVelocity();
            case PARABOLIC:
                return Math.sqrt(2 * STANDARD_GRAVITATIONAL_PARAMETER / radius);
            default:
                return Math.sqrt(STANDARD_GRAVITATIONAL_PARAMETER * (2 / radius - 1 / getSemiMajorAxis()));
        }
    }

    public double getVelocityAtAngle(double theta) {
        return getVelocityAtRadius(getRadius(theta));
    }

    public double getVelocity() {
        return getVelocityAtRadius(getRenderedPositionFromParent().getMagnitude());
    }

    @Deprecated
    public double getVelocityAtAngleLegacy(double theta) {
        switch (getOrbitType()) {
            case CIRCULAR:
                return getMeanVelocity();
            case PARABOLIC:
                return Math.sqrt(STANDARD_GRAVITATIONAL_PARAMETER / getPeriapsis()) * (1 + Math.cos(theta));
            default:
                return Math.sqrt(
                        STANDARD_GRAVITATIONAL_PARAMETER
                                / getSemiLatusRectum()) *
                        (1 + Math.pow(eccentricity, 2) - 2 * Math.cos(theta));
        }
    }

    /**
     * Get the angle of the velocity Position, perpendicular to the radial direction
     *
     * @param theta true anomaly of the object
     * @return the angle of the velocity
     */
    public double getVelocityAngle(double theta) {
        switch (getOrbitType()) {
            case CIRCULAR:
                return 0;
            case PARABOLIC:
                return theta / 2;
            default:
                // TODO Check if correct
                return Math.atan2(eccentricity * Math.sin(theta), 1 + Math.cos(theta));
        }
    }

    public double getVelocityAngle() {
        double trueAnomaly = getTrueAnomaly();

        return trueAnomaly + Math.PI / 2 + getVelocityAngle(trueAnomaly);
    }

    /**
     * The speed of an equivalent circular orbit
     *
     * @return speed in m/s
     */
    public double getMeanVelocity() {
        return Math.sqrt(STANDARD_GRAVITATIONAL_PARAMETER / semiMajorAxis);
    }

    // Anomalies

    public double getMeanMotion() {
        //return 2 * Mathematics.PI / getPeriod();
        return Math.sqrt(STANDARD_GRAVITATIONAL_PARAMETER / Math.pow(semiMajorAxis, 3));
    }

    public double getArealVelocity() {
        switch (getOrbitType()) {

            case CIRCULAR:
                return Math.sqrt(STANDARD_GRAVITATIONAL_PARAMETER * semiMajorAxis);
            case ELLIPTICAL:
                return Math.sqrt(STANDARD_GRAVITATIONAL_PARAMETER * semiMajorAxis * (1 + eccentricity) / (1 - eccentricity));
            case PARABOLIC:
                return Math.sqrt(STANDARD_GRAVITATIONAL_PARAMETER * getPeriapsis() / 2);
            case HYPERBOLIC:
                return Math.sqrt(-STANDARD_GRAVITATIONAL_PARAMETER * semiMajorAxis * (1 + eccentricity) / (1 - eccentricity));
        }

        throw new Error("This is very bad.");
    }
    //endregion Velocities

    //region Anomalies
    private double cosOfEccentricAnomaly(double theta) {
        return (eccentricity + Math.cos(theta) / (1 + eccentricity * Math.cos(theta)));
    }

    public double getEccentricAnomalyFromTrueAnomaly(double theta) {
        switch (getOrbitType()) {
            case CIRCULAR:
                return theta;
            case ELLIPTICAL:
                return Mathematics.acos2(cosOfEccentricAnomaly(theta), theta);
            case PARABOLIC:
                return Math.tan(theta / 2);
            case HYPERBOLIC:
                return acosh(cosOfEccentricAnomaly(theta));
        }

        throw new Error("oops");
    }

    /**
     * Returns the angle in an imaginary circular orbit corresponding to a planet's eccentric anomaly.
     *
     * @param time in milliseconds
     * @return in radians
     */
    public double getMeanAnomaly(long time) {
        return Math.sqrt(STANDARD_GRAVITATIONAL_PARAMETER / Math.pow(semiMajorAxis, 3)) * (time / 1000D) + meanAnomalyAtEpoch;
    }

    public double calculateEccentricAnomaly(long time) {
        double meanAnomaly = getMeanAnomaly(time);
        return calculateEccentricAnomaly(meanAnomaly, eccentricity, 10);
    }

    public double getMeanAnomalyFromAngle(double theta) {
        double eccentricAnomaly = getEccentricAnomalyFromTrueAnomaly(theta);

        switch (getOrbitType()) {
            case CIRCULAR:
                return eccentricAnomaly;
            case ELLIPTICAL:
                return eccentricAnomaly - eccentricity * Math.sin(eccentricAnomaly);
            case PARABOLIC:
                return eccentricAnomaly + Math.pow(eccentricAnomaly, 3) / 3;
            case HYPERBOLIC:
                return eccentricity * Math.sinh(eccentricAnomaly) - eccentricAnomaly;
        }

        throw new Error("oops");
    }

    /**
     * https://en.wikipedia.org/wiki/True_anomaly#From_the_eccentric_anomaly
     *
     * @param time in SECONDS
     * @return
     */
    public double getTrueAnomaly(long time) {
        // Taken straight from wikipedia, should be okay
        double eccentricAnomaly = calculateEccentricAnomaly(time);

        double y = Math.sqrt(1 - eccentricity) * Math.cos(eccentricAnomaly / 2);
        double x = Math.sqrt(1 + eccentricity) * Math.sin(eccentricAnomaly / 2);

        return 2 * Math.atan2(y, x);
    }

    public double getTrueAnomaly() {
        return Math.atan2(getRenderedPositionFromParent().getY(), getRenderedPositionFromParent().getX());
    }
    //endregion

    //region Times

    public long getPeriod() {
        return new Double(2 * Math.PI * Math.sqrt(Math.pow(semiMajorAxis, 3) / STANDARD_GRAVITATIONAL_PARAMETER)).longValue();
    }

    public double getTimeFromPeriapsis(double theta) {
        if (eccentricity >= 1)
            throw new RuntimeException("Not implemented");
        return getMeanAnomalyFromAngle(theta) * getPeriod() / (2 * Math.PI);
    }
    //endregion

    //region Position
    public Position getPositionFromParent(long time) {
        return rotatePositionOnOrbitalPlane(
                getPositionOnOrbitalPlaneFromEccentricAnomaly(
                        calculateEccentricAnomaly(time)
                )
        );
    }

    public Position getRenderedPositionFromParent() {
        return this._positionFromParent;
    }

    public Position getRenderedPositionFromOrbitalPlane() {
        return this._positionFromOrbitalPlane;
    }

    public Position getRenderedAbsolutePosition() {
        return (Position) this.getRenderedPositionFromParent().plus(this.parent.getPosition());
    }

    public Position getPositionOnOrbitalPlaneFromEccentricAnomaly(double E) {
        double C = Math.cos(E);

        double S = Math.sin(E);

        double x = semiMajorAxis * (C - eccentricity);

        double y = semiMajorAxis * Math.sqrt(1D - eccentricity * eccentricity) * S;

        return new Position(x, y, 0);
    }

    public Position rotatePositionOnOrbitalPlane(Position position) {
        Vector longitudeOfAscendingNodeAxis = new Vector(0, 0, 1);

        position.rotate(longitudeOfAscendingNodeAxis, longitudeOfAscendingNode);
        //Vector inclinationAxis = new Vector(Math.cos(longitudeOfAscendingNode), Math.sin(longitudeOfAscendingNode), 0);
        //_positionFromParent.rotate(inclinationAxis, inclination);
        // Todo argument of periapsis
        return position;
    }
    //endregion Position

    //region ToString
    @Override
    public String toString() {
        return String.format("%s orbit. Semimajor-Axis %f, Eccentricity %f", getOrbitType().toString().toLowerCase(), semiMajorAxis, eccentricity);
    }
    //endregion ToString

    //region Orbit type
    public orbitType getOrbitType() {
        if (eccentricity < 0)
            throw new Error("Eccentricity should NEVER be negative.");
        if (eccentricity == 0)
            return orbitType.CIRCULAR;
        if (0 < eccentricity && eccentricity < 1)
            return orbitType.ELLIPTICAL;
        if (eccentricity == 1)
            return orbitType.PARABOLIC;
        else
            return orbitType.HYPERBOLIC;
    }

    public CelestialBody getParent() {
        return parent;
    }

    public void renderAtTime(long time) {
        this._eccentricAnomaly = calculateEccentricAnomaly(time);
        this._positionFromOrbitalPlane = getPositionOnOrbitalPlaneFromEccentricAnomaly(this._eccentricAnomaly);
        this._positionFromParent = rotatePositionOnOrbitalPlane(this._positionFromOrbitalPlane.getCopy());
    }

    public enum orbitType {
        CIRCULAR,
        ELLIPTICAL,
        PARABOLIC,
        HYPERBOLIC
    }

    //endregion
}
