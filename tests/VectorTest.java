import astrarium.utils.Mathematics;
import astrarium.utils.Matrix;
import astrarium.utils.Position;
import astrarium.utils.Vector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static astrarium.utils.Mathematics.TWO_PI;
import static java.lang.Math.PI;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Created on 26/02/2017.
 *
 * @author Vittorio
 */
@SuppressWarnings("JavaDoc")
class VectorTest {
    @Test
    void isInsideRadius() {
        Position v = new Position(0, 0, 0);

        Assertions.assertTrue(v.isInsideRadius(v, 1));
    }

    @Test
    void isInsideRadius2() {
        Position v = new Position(2, 0, 0);
        Position c = new Position(0, 0, 0);

        Assertions.assertFalse(v.isInsideRadius(c, 1));
    }

    @Test
    void isInsideRadius3() {
        Position v = new Position(1, 0, 0);
        Position c = new Position(0, 0, 0);

        Assertions.assertTrue(v.isInsideRadius(c, 1));
    }

    @Test
    void isInsideRadius4() {
        Position v = new Position(1, 0, 0);
        Position c = new Position(1, 1, 0);

        Assertions.assertTrue(v.isInsideRadius(c, 1));
    }

    @Test
    void isInsideRadius5() {
        Position v = new Position(1.1, 0, 0);
        Position c = new Position(1, 1, 0);

        Assertions.assertFalse(v.isInsideRadius(c, 1));
    }

    @Test
    void isInsideRadius6() {
        Position v = new Position(-1, 1, 0);
        Position c = new Position(-2, 2, 0);

        Assertions.assertTrue(v.isInsideRadius(c, 2));
    }

    @Test
    void getAngleWith1() {
        Vector v1 = new Vector(1, 0, 0);
        Vector v2 = new Vector(0, 1, 0);

        Assertions.assertEquals(Mathematics.PI_BY_TWO, v1.getAngleWith(v2));
    }

    @Test
    void getAngleWith2() {
        Vector v1 = new Vector(1, 0, 0);
        Vector v2 = new Vector(0, 2, 0);

        Assertions.assertEquals(Mathematics.PI_BY_TWO, v1.getAngleWith(v2));
    }

    @Test
    void getAngleWith3() {
        Vector v1 = new Vector(1, 0, 0);
        Vector v2 = new Vector(1, 1, 0);

        Assertions.assertEquals(Mathematics.PI_BY_TWO / 2, v1.getAngleWith(v2), 1e-5);
    }

    @Test
    void getAngleWith4() {
        Vector v1 = new Vector(22.5, 66, 789);
        Vector v2 = new Vector(22, 565, 77);

        Assertions.assertEquals(Math.toRadians(77.41), v1.getAngleWith(v2), 1e-2);
    }

    @Test
    void toMatrix() {
        Vector position = new Vector(1, 2, 3);

        Matrix matrix = position.toMatrix();

        assertEquals(1D, matrix.get(0, 0), "X value");
        assertEquals(2D, matrix.get(0, 1), "Y value");
        assertEquals(3D, matrix.get(0, 2), "Z value");
    }

    @Test
    void rotate() {
        Vector position1 = new Vector(1, 2, 3);
        Vector position2 = position1.getCopy();

        double theta = toRadians(90D);

        Vector axis = new Vector(1, 2, 1);

        //noinspection deprecation
        position1.rotateWithMatrix(axis, theta);
        position2.rotate(axis, theta);

        Assertions.assertEquals(true, position1.equals(position2));
    }

    @Test
    void rotateZ() {
        Vector position1 = new Vector(1, 2, 3);
        Vector position2 = position1.getCopy();

        double theta = toRadians(90D);

        Vector axis = new Vector(0, 0, 1);

        position1.rotate(axis, theta);
        position2.rotateZ(theta);

        Assertions.assertEquals(true, position1.equals(position2));
    }

    @Test
    void rotateX() {
        Vector position1 = new Vector(1, 2, 3);
        Vector position2 = position1.getCopy();

        double theta = toRadians(90D);

        Vector axis = new Vector(1, 0, 0);

        position1.rotate(axis, theta);
        position2.rotateX(theta);

        Assertions.assertEquals(true, position1.equals(position2));
    }

    @Test
    void crossProduct() {
        Vector a = new Vector(-4, -4, 9);
        Vector b = new Vector(-13, -2, 0);

        Vector c = a.crossProduct(b);

        Vector r = new Vector(18, -117, -44);
        Assertions.assertEquals(true, c.equals(r));
    }

    @Test
    void equals1() {
        Vector a = new Vector(0, 0, 0);

        Assertions.assertEquals(true, a.equals(a));
    }

    @Test
    void equals2() {
        Vector a = new Vector(0, 0, 0);
        Object b = new Object();

        Assertions.assertEquals(false, a.equals(b));
    }

    @Test
    void equals3() {
        Vector a = new Vector(0, 0, 10.1001);
        Vector b = new Vector(0, 0, 10.1001);

        Assertions.assertEquals(true, a.equals(b));
    }

    @Test
    void equals4() {
        Vector a = new Vector(0, 0, -10.1001);
        Vector b = new Vector(0, 0, 10.1001);

        Assertions.assertEquals(false, a.equals(b));
    }

    @Test
    void equalsRotate1() {
        Vector a = new Vector(1, 0, 0);
        Vector b = a.getCopy();
        b.rotateZ(TWO_PI);

        Assertions.assertEquals(true, a.equals(b));
    }

    @Test
    void equalsRotate2() {
        Vector a = new Vector(1, 0, 0);
        Vector b = a.getCopy();
        b.rotateZ(PI);

        Assertions.assertEquals(false, a.equals(b));
    }
}