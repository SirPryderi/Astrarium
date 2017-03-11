package astrarium.utils;

/**
 * A utility class that represents the mathematical entity of a Vector.
 * <p>
 * Implements various method for vector manipulation.
 * <p>
 * Created by Vittorio on 10-Nov-16.
 *
 * @author Vittorio
 */
public class Vector implements Cloneable {
    // Coordinates
    private double x;
    private double y;
    private double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getMagnitude() {
        return Math.sqrt(getMagnitudeSquared());
    }

    public double getMagnitudeSquared() {
        return x * x + y * y + z * z;
    }

    public double normalise() {
        double length = getMagnitude();

        this.multiplied(1D / length);

        return length;
    }

    public Vector getNormalisedVector() {
        Vector norm = this.getCopy();

        norm.normalise();

        return norm;
    }

    //region Rotation
    public void rotate(Vector axis, double theta) {
        // Normalise vector
        Double length = this.getMagnitude();
        this.normalise();

        // Normalise Axis
        axis.normalise();

        //region Evil Transformation Matrix
        // Useful vars
        double c = Math.cos(theta);
        double s = Math.sin(theta);
        double t = 1 - Math.cos(theta);

        double norm_final_x = x * (t * axis.x * axis.x + c) + y * (t * axis.x * axis.y - s * axis.z) + z * (t * axis.x * axis.z + s * axis.y);
        double norm_final_y = x * (t * axis.x * axis.y + s * axis.z) + y * (t * axis.y * axis.y + c) + z * (t * axis.y * axis.z - s * axis.x);
        double norm_final_z = x * (t * axis.x * axis.z - s * axis.y) + y * (t * axis.y * axis.z + s * axis.x) + z * (t * axis.z * axis.z + c);
        //endregion

        // Set new (normalised) values to the vector
        this.setValues(norm_final_x, norm_final_y, norm_final_z);

        // De-normalise vector
        this.multiplied(length);
    }

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
    public Vector multiplied(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;
        return this;
    }

    public Vector product(double factor) {
        return new Vector(this.x * factor, this.y * factor, this.z * factor);
    }

    public double dotProduct(Vector vector) {
        return this.getX() * vector.getX() + this.getY() * vector.getY() + this.getZ() * vector.getZ();
    }

    public Vector crossProduct(Vector vector) {
        return new Vector(
                this.y * vector.z - this.z * vector.y,
                this.z * vector.x - this.x * vector.z,
                this.x * vector.y - this.y * vector.x
        );
    }

    public Vector plus(Vector vector) {
        setValues(this.getX() + vector.getX(), this.getY() + vector.getY(), this.getZ() + vector.getZ());
        return this;
    }

    public Vector minus(Vector vector) {
        setValues(this.getX() - vector.getX(), this.getY() - vector.getY(), this.getZ() - vector.getZ());
        return this;
    }
    //endregion Operations

    //region Getters and Setters
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setValues(double x, double y) {
        setValues(x, y, 0);
    }

    public void setValues(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setValues(Vector vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }
    //endregion

    public Vector getCopy() {
        return new Vector(x, y, z);
    }

    //region toMatrix
    public Matrix toMatrix() {
        return new Matrix(new double[][]{{x}, {y}, {z}});
    }
    //endregion

    //region toString
    @Override
    public String toString() {
        return String.format("<%f, %f, %f>", this.getX(), this.getY(), this.getZ());
    }
    //endregion toString
}
