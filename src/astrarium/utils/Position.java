package astrarium.utils;

/**
 * An extension of {@link Vector} specified for being used as coordinates for other objects.
 * <p>
 * Created on 26/02/2017.
 *
 * @author Vittorio
 */
public class Position extends Vector {
    public Position(double x, double y, int z) {
        super(x, y, z);
    }

    public Position(double x, double y) {
        super(x, y, 0);
    }

    public double angleOfLineBetweenThisAnd(Position position) {
        if (this.getZ() != 0 && position.getZ() != 0)
            throw new RuntimeException("Function not supported yet. Z must be zero");

        return Math.atan2(position.getY() - this.getY(), position.getX() - this.getX());
    }
}
