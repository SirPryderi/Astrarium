package astrarium.utils;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * A utility class that represents the mathematical entity of a Vector.
 * <p>
 * Implements various method for vector manipulation.
 * <p>
 * Created by Vittorio on 10-Nov-16.
 *
 * @author Vittorio
 */
public class Vector {
    /**
     * X value.
     */
    private double x;
    /**
     * Y value.
     */
    private double y;
    /**
     * Z value.
     */
    private double z;

    /**
     * Creates a new vector with the given values.
     *
     * @param x the X value of the newly constructed {@link Vector}.
     * @param y the Y value of the newly constructed {@link Vector}.
     * @param z the Z value of the newly constructed {@link Vector}.
     */
    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns a two-dimensional unit vector representing the given {@code angle}.
     *
     * @param angle the direction of the vector.
     * @return the direction vector.
     */
    public static Vector getDirectionVector(double angle) {
        return new Vector(cos(angle), sin(angle), 0);
    }

    //region Magnitude

    /**
     * Returns the magnitude of the vector.
     * <p>
     * The magnitude is defined by the distance of the end of the vector from the origin,
     * hence it is always non-negative.
     *
     * @return magnitude of the vector.
     */
    public double getMagnitude() {
        return Math.sqrt(getMagnitudeSquared());
    }

    /**
     * Returns the squared magnitude of the vector.
     * <p>
     * It is useful for performance reason, to avoid the use of the time-consuming square root,
     * for instance where comparing two distances, the squared value can be used instead.
     *
     * @return the squared magnitude of the vector.
     */
    public double getMagnitudeSquared() {
        return x * x + y * y + z * z;
    }
    //endregion Magnitude

    //region Normalisation

    /**
     * Converts the current vector to a unit vector, i.e. a vector with magnitude equals 1.
     * <p>
     * It returns the former magnitude of the vector.
     * <p>
     * NOTE: the original values will be lost!
     *
     * @return former magnitude of the vector.
     */
    public double normalise() {
        double length = getMagnitude();

        this.divided(length);

        return length;
    }

    /**
     * Returns a normalised instance of the vector, without altering the original one.
     *
     * @return unit vector.
     */
    public Vector getNormalisedVector() {
        Vector norm = this.getCopy();

        norm.normalise();

        return norm;
    }
    //endregion Normalisation

    //region Rotation

    /**
     * Rotates a vector along {@code axis} of the amount of {@code theta}.
     * <p>
     * Uses an optimised algorithm to calculate the rotation vector.
     *
     * @param axis  rotation axis.
     * @param theta rotation angle.
     */
    public void rotate(Vector axis, double theta) {
        // If the angle is zero, there is no need to do anything. Day off, guys!
        if (theta == 0)
            return;

        // Normalise vector
        Double length = this.getMagnitude();
        this.normalise();

        // Normalise Axis
        axis.normalise();

        //region Evil Transformation Matrix
        // Useful vars
        double c = cos(theta);
        double s = sin(theta);
        double t = 1 - cos(theta);

        double norm_final_x = x * (t * axis.x * axis.x + c) + y * (t * axis.x * axis.y - s * axis.z) + z * (t * axis.x * axis.z + s * axis.y);
        double norm_final_y = x * (t * axis.x * axis.y + s * axis.z) + y * (t * axis.y * axis.y + c) + z * (t * axis.y * axis.z - s * axis.x);
        double norm_final_z = x * (t * axis.x * axis.z - s * axis.y) + y * (t * axis.y * axis.z + s * axis.x) + z * (t * axis.z * axis.z + c);
        //endregion

        // Set new (normalised) values to the vector
        this.setValues(norm_final_x, norm_final_y, norm_final_z);

        // De-normalise vector
        this.multiplied(length);
    }

    /**
     * Rotates the current vector on the Z axis (0, 0, 1) with angle {@code theta}.
     *
     * @param theta angle of rotation.
     */
    public void rotateZ(double theta) {
        if (theta == 0)
            return;

        double c = cos(theta);
        double s = sin(theta);

        this.setValues(x * c - y * s, y * c + x * s, z);
    }

    /**
     * Rotates the current vector on the X axis (1, 0, 0) with angle {@code theta}.
     *
     * @param theta angle of rotation.
     */
    public void rotateX(double theta) {
        if (theta == 0)
            return;

        double c = cos(theta);
        double s = sin(theta);

        this.setValues(x, y * c - z * s, z * c + y * s);
    }
    //endregion Rotation

    //region Operations

    /**
     * Rotates a vector along {@code axis} of the amount of {@code theta}.
     * <p>
     * It uses the {@link Matrix} class to achieve this.
     *
     * @param axis  rotation axis.
     * @param theta rotation angle.
     * @deprecated {@link #rotate(Vector, double)} is a faster way of achieving the same result.
     */
    @Deprecated
    public void rotateWithMatrix(Vector axis, double theta) {
        // Normalise vector
        double length = this.normalise();

        // Normalise rotation axis
        axis.normalise();

        // Calculates rotation
        Vector vector = Matrix.getRotationMatrix(axis, theta).product(toMatrix()).toVector();

        // Set the rotation vector values
        this.setValues(vector);

        // De-normalise vector multiplying by magnitude
        this.multiplied(length);
    }
    //endregion Rotation

    //region Operations

    /**
     * Multiplies every component of the vector by the specified scalar {@code factor}.
     * <p>
     * Note: the original vector is overwritten!
     *
     * @param factor the scalar factor to multiply the vector with.
     * @return the {@link Vector} after being multiplied.
     */
    public Vector multiplied(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        return this;
    }

    /**
     * Divides every component of the vector by the specified scalar {@code factor}.
     * <p>
     * Note: the original vector is overwritten!
     *
     * @param factor the scalar factor to divide the vector with.
     * @return the {@link Vector} after being divided.
     */
    public Vector divided(double factor) {
        this.x /= factor;
        this.y /= factor;
        this.z /= factor;
        return this;
    }

    /**
     * Returns an instance multiplied by the specified scalar {@code factor} of the original vector,
     * without altering the former.
     *
     * @param factor the scalar factor to multiply the vector with.
     * @return the multiplied instance of the vector.
     */
    public Vector product(double factor) {
        return new Vector(this.x * factor, this.y * factor, this.z * factor);
    }

    /**
     * Returns the dot or scalar product between this and another {@link Vector}.
     *
     * @param vector the second factor of the dot product.
     * @return the result value.
     */
    public double dotProduct(Vector vector) {
        return this.getX() * vector.getX() + this.getY() * vector.getY() + this.getZ() * vector.getZ();
    }

    /**
     * Returns the cross or vector product between this and another {@link Vector}.
     *
     * @param vector the second factor of the cross product.
     * @return the vector resulting from the cross product.
     */
    public Vector crossProduct(Vector vector) {
        return new Vector(
                this.y * vector.z - this.z * vector.y,
                this.z * vector.x - this.x * vector.z,
                this.x * vector.y - this.y * vector.x
        );
    }

    /**
     * Sums the first vector with a second one.
     * <p>
     * Note: the original values will be overwritten!
     *
     * @param vector the second addend of the sum.
     * @return the resulting vector.
     */
    public Vector plus(Vector vector) {
        setValues(this.getX() + vector.getX(), this.getY() + vector.getY(), this.getZ() + vector.getZ());
        return this;
    }
    //endregion Operations

    //region Getters and Setters

    /**
     * Subtract the second {@link Vector} form the first one.
     * <p>
     * Note: the original values will be overwritten!
     *
     * @param vector the subtrahend of the subtraction.
     * @return the resulting vector.
     */
    public Vector minus(Vector vector) {
        setValues(this.getX() - vector.getX(), this.getY() - vector.getY(), this.getZ() - vector.getZ());
        return this;
    }
    //endregion Operations

    //region Getters and Setters

    /**
     * Gets the X value of the {@link Vector}.
     *
     * @return X value.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the X value of the {@link Vector}.
     *
     * @param x new X value.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the Y value of the {@link Vector}.
     *
     * @return Y value.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the Y value of the {@link Vector}.
     *
     * @param y new Y value.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the Z value of the {@link Vector}.
     *
     * @return Z value.
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the Z value of the {@link Vector}.
     *
     * @param z new Z value.
     */
    public void setZ(double z) {
        this.z = z;
    }

    /**
     * Overloaded version of {@link #setValues(double, double, double)} that automatically sets Z to 0.
     *
     * @param x new X value.
     * @param y new Z value.
     * @see #setValues(double, double, double)
     */
    public void setValues(double x, double y) {
        setValues(x, y, 0);
    }

    /**
     * Sets all the components of the {@link Vector}.
     *
     * @param x new X value.
     * @param y new Y value.
     * @param z new Z value.
     */
    public void setValues(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Copies the values from another vector.
     *
     * @param vector {@link Vector} to copy the values from.
     */
    public void setValues(Vector vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }
    //endregion

    /**
     * Returns an instance with the same values of the original {@link Vector}.
     *
     * @return a copy of the original vector.
     */
    public Vector getCopy() {
        return new Vector(x, y, z);
    }

    /**
     * Converts the {@link Vector} into a 1x3 {@link Matrix}.
     *
     * @return result {@link Matrix}.
     */
    public Matrix toMatrix() {
        return new Matrix(new double[][]{{x}, {y}, {z}});
    }

    @Override
    public String toString() {
        return String.format("<%f, %f, %f>", this.getX(), this.getY(), this.getZ());
    }
}
