package astrarium.utils;

import static java.lang.Math.*;

/**
 * A utility class containing often-used mathematical utilities.
 * <p>
 * Created on 23/02/2017.
 *
 * @author Vittorio
 */
public final class Mathematics {
    /**
     * Value of  &pi;/2.
     */
    public final static double PI_BY_TWO = 1.5707963267948966;
    /**
     * Value of  &pi; * 2.
     */
    public final static double TWO_PI = 6.283185307179586;
    /**
     * Value of  &pi; 3/2.
     */
    public final static double THREE_HALVES_PI = 4.71238898038469;

    /**
     * Default precision to compare two doubles.
     */
    final static double EPSILON = 1E-5;

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
        if (abs(angle) <= PI) return angle;

        return angle - (TWO_PI * floor((angle + PI) / TWO_PI));
    }

    /**
     * Compares two doubles to check for equality with a given delta.
     *
     * @param a       first double to compare.
     * @param b       second double to compare.
     * @param epsilon desired precision, e.g. 0.001.
     * @return true if they are the same according two the epsilon, false otherwise.
     */
    public static boolean equals(double a, double b, double epsilon) {
        return (Math.abs(a - b) < epsilon);
    }

    /**
     * Compares two doubles to check for equality with a precision of 1E-5.
     *
     * @param a first double to compare.
     * @param b second double to compare.
     * @return true if their difference is less then 1E-5, false otherwise.
     */
    public static boolean equals(double a, double b) {
        return equals(a, b, EPSILON);
    }
}
