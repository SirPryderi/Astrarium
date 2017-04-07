package astrarium.utils;

/**
 * A class representing the mathematical entity of a matrix. Provides facilities for the most used operations.
 * <p>
 * Created on 26/02/2017.
 *
 * @author Vittorio
 */
@SuppressWarnings("WeakerAccess")
public class Matrix {
    /**
     * Values of the matrix.
     */
    private double[][] matrix;

    /**
     * Creates a matrix from an array of array as initialisation.
     *
     * @param matrix values of the matrix
     */
    public Matrix(double[][] matrix) {
        this.matrix = matrix;
    }

    /**
     * Returns the rotation matrix around an axis defined as a vector and the angle theta.
     *
     * @param axis  the normalised axis vector.
     * @param theta the angle of the rotation.
     * @return rotation matrix.
     */
    public static Matrix getRotationMatrix(Vector axis, double theta) {
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);

        double x = axis.getX();
        double y = axis.getY();
        double z = axis.getZ();

        double xSquared = x * x;
        double ySquared = y * y;
        double zSquared = z * z;

        double xy = x * y;
        double xz = x * z;

        double yz = y * z;

        double oneMinusCosTheta = 1D - cosTheta;

        return new Matrix(new double[][]{
                {cosTheta + xSquared * oneMinusCosTheta, xy * oneMinusCosTheta - z * sinTheta, xz * oneMinusCosTheta + y * sinTheta},
                {xy * oneMinusCosTheta + z * sinTheta, cosTheta + ySquared * oneMinusCosTheta, yz * oneMinusCosTheta - x * sinTheta},
                {xz * oneMinusCosTheta - y * sinTheta, yz * oneMinusCosTheta + x * sinTheta, cosTheta + zSquared * oneMinusCosTheta}
        });
    }

    /**
     * Calculates the product between to matrices.
     * <p>
     * From <a href="http://stackoverflow.com/a/23817349">http://stackoverflow.com/a/23817349</a>
     *
     * @param that second factor of the product.
     * @return matrix product.
     * @throws IllegalArgumentException if the matrices cannot be multiplied
     */
    public Matrix product(Matrix that) {
        double[][] m1 = this.matrix;
        double[][] m2 = that.matrix;

        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if (m1ColLength != m2RowLength) // matrix multiplication is not possible
            throw new IllegalArgumentException("Matrix multiplication not possible.");

        int mRRowLength = m1.length;    // m result rows length
        int mRColLength = m2[0].length; // m result columns length
        double[][] mResult = new double[mRRowLength][mRColLength];
        for (int i = 0; i < mRRowLength; i++) {         // rows from m1
            for (int j = 0; j < mRColLength; j++) {     // columns from m2
                for (int k = 0; k < m1ColLength; k++) { // columns from m1
                    mResult[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }

        return new Matrix(mResult);
    }

    /**
     * Converts a 1x3 {@link Matrix} to a tridimensional {@link Vector}.
     * Throws an exception if the matrix is a different size.
     *
     * @return the {@link Vector} equivalent to the matrix.
     * @throws IllegalStateException if the Matrix is not 1x3.
     */
    public Vector toVector() {
        if (this.matrix.length != 3 || this.matrix[0].length != 1) {
            throw new IllegalStateException("The matrix must be 1x3.");
        }

        return new Vector(this.matrix[0][0], this.matrix[1][0], this.matrix[2][0]);
    }

    /**
     * Returns the value at the given coordinates.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     * @return the value.
     * @throws IndexOutOfBoundsException if the coordinates are out of the matrix range.
     */
    public double get(int x, int y) {
        return matrix[y][x];
    }
}
