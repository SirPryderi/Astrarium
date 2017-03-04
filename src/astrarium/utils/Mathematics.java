package astrarium.utils;

/**
 * A utility class containing often-used mathematical utilities.
 * <p>
 * Created on 23/02/2017.
 *
 * @author Vittorio
 */
public abstract class Mathematics {
    public static double acosh(double x) {
        return Math.log(x + Math.sqrt(x * x - 1));
    }

    public static double acos2(double alpha, double beta) {

        double signum = Math.signum(beta);

        return Math.acos(alpha) * signum;
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

    public static void main(String args[]) {
        System.out.println(normaliseAngle(-6.30));
    }
}
