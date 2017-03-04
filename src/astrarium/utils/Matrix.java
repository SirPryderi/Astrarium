package astrarium.utils;

/**
 * A class representing the mathematical entity of a matrix. Provides facilities for basic operations.
 * <p>
 * Created on 26/02/2017.
 *
 * @author Vittorio
 */
public class Matrix {
    private double[][] matrix;

    public Matrix(double[][] matrix) {
        this.matrix = matrix;
    }

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

    public Matrix product(Matrix that) {
        // http://stackoverflow.com/a/23817349

        double[][] m1 = this.matrix;
        double[][] m2 = that.matrix;

        int m1ColLength = m1[0].length; // m1 columns length
        int m2RowLength = m2.length;    // m2 rows length
        if (m1ColLength != m2RowLength) // matrix multiplication is not possible
            throw new RuntimeException("Matrix multiplication not possible.");

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

    public Vector toVector() {
        if (this.matrix.length != 3 || this.matrix[0].length != 1) {
            throw new RuntimeException("Invalid matrix.");
        }

        return new Vector(this.matrix[0][0], this.matrix[1][0], this.matrix[2][0]);
    }

    public double get(int x, int y) {
        return matrix[y][x];
    }
}
