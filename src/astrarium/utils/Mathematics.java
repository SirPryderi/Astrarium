package astrarium.utils;

/**
 * A utility class containing often-used mathematical utilities.
 * <p>
 * Created on 23/02/2017.
 *
 * @author Vittorio
 */
public final class Mathematics {
    private Mathematics() {
    }

    public static double acosh(double x) {
        return Math.log(x + Math.sqrt(x * x - 1));
    }

    public static double acos2(double alpha, double beta) {
        return Math.acos(alpha) * Math.signum(beta);
    }

    public static double hypotenuse(double a, double b) {
        return Math.sqrt(a * a + b * b);
    }

    public static double hypotenuseApprox(double a, double b) {
        return b + 0.337 * a;
    }

    public static double normaliseAngle(double angle) {
        return angle % Math.PI * 2;
    }
}
