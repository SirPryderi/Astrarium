import astrarium.utils.Matrix;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created on 16/04/2017.
 *
 * @author Vittorio
 */
@SuppressWarnings("JavaDoc")
class MatrixTest {
    @Test
    void getDeterminant() {
        Matrix matrix = new Matrix(new double[][]
                {
                        {-2, 2, -3},
                        {-1, 1, 3},
                        {2, 0, -1}
                }
        );

        Assertions.assertEquals(18D, matrix.getDeterminant());
    }

    @Test
    void determinant2() {
        Matrix matrix = new Matrix(new double[][]
                {
                        {45, 21, 1564, 121},
                        {4, -4, 1, 213},
                        {132, 32, 12, 55},
                        {321, 4, 4, 6},
                }
        );

        Assertions.assertEquals(-3333637923D, matrix.getDeterminant());
    }

    @Test
    void determinant3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Matrix(new double[][]{{12, 2}}).getDeterminant());
    }

    @Test
    void getMinors() {
        Matrix matrix = new Matrix(new double[][]
                {
                        {2, -1, 5, 9},
                        {-12, 3, 2, 0},
                        {1, -1, 9, 8}
                });

        double[] minors = matrix.getMinors();

        Assertions.assertEquals(125D, minors[0]);
        Assertions.assertEquals(-478D, minors[1]);
        Assertions.assertEquals(33D, minors[2]);
        Assertions.assertEquals(-7, minors[3]);
    }
}
