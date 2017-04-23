package astrarium.utils;

import static java.lang.Math.pow;

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
    private final double[][] matrix;

    /**
     * Creates a matrix from an array of array as initialisation.
     *
     * @param matrix values of the matrix
     */
    public Matrix(double[][] matrix) {
        this.matrix = matrix;
    }

    /**
     * Creates an empty matrix with the given sizes.
     *
     * @param x width of the matrix
     * @param y height of the matrix.
     */
    public Matrix(int x, int y) {
        this(new double[y][x]);
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
     * Returns the determinant of the matrix.
     *
     * @param a the matrix.
     * @param n size of the matrix.
     * @return the determinant of the matrix.
     */
    private static double getDeterminant(double a[][], int n) {
        double det = 0;

        int p, h, k, i, j;

        double[][] temp = new double[a.length][a[1].length];

        if (n == 1) {
            return a[0][0];
        } else if (n == 2) {
            det = (a[0][0] * a[1][1] - a[0][1] * a[1][0]);
            return det;
        } else {
            for (p = 0; p < n; p++) {
                h = 0;
                k = 0;
                for (i = 1; i < n; i++) {
                    for (j = 0; j < n; j++) {
                        if (j == p) {
                            continue;
                        }
                        temp[h][k] = a[i][j];
                        k++;
                        if (k == n - 1) {
                            h++;
                            k = 0;
                        }
                    }
                }
                det = det + a[0][p] * pow(-1, p) * getDeterminant(temp, n - 1);
            }
            return det;
        }
    }

    /**
     * Removes one element from the array.
     *
     * @param a   array.
     * @param del index to remove.
     * @return returns the array without the removed element.
     */
    public static double[] removeElement(double[] a, int del) {
        double b[] = new double[a.length - 1];
        System.arraycopy(a, 0, b, 0, del);
        System.arraycopy(a, del + 1, b, del, a.length - del - 1);
        return b;
    }

    /**
     * Returns the determinant of the matrix. Hopefully.
     *
     * @return the determinant of the matrix.
     */
    public double getDeterminant() {
        if (this.matrix.length != this.matrix[0].length) {
            throw new IllegalArgumentException("The matrix must be square to calculate its determinant.");
        }

        return Matrix.getDeterminant(this.matrix, this.matrix.length);
    }

    /**
     * Return the minors of the matrix.
     * <p>
     * The matrix must be of sizes n - 1 by n.
     *
     * @return the minors of the matrix.
     */
    public double[] getMinors() {
        if (matrix[0].length != matrix.length + 1)
            throw new IllegalArgumentException();

        int order = matrix[0].length - 1;

        int count = matrix[0].length;

        double[] minors = new double[count];

        for (int i = 0; i < count; i++) {
            Matrix submatrix = new Matrix(order, order);
            for (int y = 0; y < matrix.length; y++) submatrix.matrix[y] = removeElement(matrix[y], i);
            minors[i] = submatrix.getDeterminant();
        }

        return minors;
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
