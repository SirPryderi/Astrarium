package astrarium.utils;

/**
 * An extension of {@link Vector} specified for being used as coordinates for other objects.
 * <p>
 * Created on 26/02/2017.
 *
 * @author Vittorio
 */
public class Position extends Vector {
    /**
     * Creates a new position with values &lt;0, 0, 0&gt;.
     */
    public Position() {
        super(0, 0, 0);
    }

    /**
     * Creates a new position with the given coordinates.
     *
     * @param x the X value of the newly created {@link Position}.
     * @param y the Y value of the newly created {@link Position}.
     * @param z the Z value of the newly created {@link Position}.
     */
    public Position(double x, double y, double z) {
        super(x, y, z);
    }

    /**
     * Creates a new position with the given coordinates and Z equals 0.
     *
     * @param x the X value of the newly created {@link Position}.
     * @param y the Y value of the newly created {@link Position}.
     */
    public Position(double x, double y) {
        super(x, y, 0);
    }

    /**
     * Calculates the angle of the line between this {@link Position} and another.
     * <p>
     * Note: It will throw an exception if one of the two positions has a Z value different from 0.
     *
     * @param position the other point to calculate the angle.
     * @return The angle of the line expressed in radians.
     */
    public double angleOfLineBetweenThisAnd(Position position) {
        if (this.getZ() != 0 && position.getZ() != 0)
            throw new IllegalArgumentException("Function not supported yet. Both Z values must be zero.");

        return Math.atan2(position.getY() - this.getY(), position.getX() - this.getX());
    }

    @Override
    public Position getCopy() {
        return new Position(this.getX(), this.getY(), this.getZ());
    }
}
