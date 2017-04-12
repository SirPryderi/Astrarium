package astrarium.utils;

/**
 * A utility class containing often-used mathematical utilities.
 * <p>
 * Created on 23/02/2017.
 *
 * @author Vittorio
 */
public final class Mathematics {
    /**
     * Makes the class non-instantiable.
     */
    private Mathematics() {
    }

    /**
     * Returns the result of the inverse hyperbolic cosine.
     *
     * @param x value.
     * @return inverse hyperbolic cosine of value.
     */
    public static double acosh(double x) {
        return Math.log(x + Math.sqrt(x * x - 1));
    }

    /**
     * Returns the inverse sine function of alpha with the same sign of beta.
     *
     * @param alpha angle to determine the inverse of the cosine.
     * @param beta  angle to determine the sign of the result.
     * @return final result.
     */
    public static double acos2(double alpha, double beta) {
        return Math.acos(alpha) * Math.signum(beta);
    }

    /**
     * A quick algorithm to compute an approximation of sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>).
     * <p>
     * Not to use for high-precision calculations, use {@link Math#hypot(double, double)} instead.
     *
     * @param x first side.
     * @param y second side.
     * @return approximated hypotenuse.
     */
    public static double hypotenuseApprox(double x, double y) {
        return y + 0.337 * x;
    }

    /**
     * Normalise an angle in radians between the range -PI, PI.
     *
     * @param angle angle to normalise.
     * @return normalise angle.
     */
    public static double normaliseAngle(double angle) {
        if (Math.abs(angle) <= Math.PI) return angle;

        final double TWO_PI = Math.PI * 2;
        return angle - TWO_PI * Math.floor((angle + Math.PI) / TWO_PI);
    }
}
