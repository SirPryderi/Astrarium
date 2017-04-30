import astrarium.Astrarium;
import astrarium.CelestialBody;
import astrarium.Orbit;
import astrarium.utils.Position;
import io.JsonHub;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created on 01/03/2017.
 *
 * @author Vittorio
 */
@SuppressWarnings("JavaDoc")
class CelestialBodyTest {
    static CelestialBody getEarth() throws IOException {
        Astrarium astrarium = JsonHub.importDefaultMap("SolSystem");

        return astrarium.getRoot().getChildren().get(2);
    }

    @Test
    void getAllChildren() {
        CelestialBody first = new CelestialBody("First", 0, 0);

        CelestialBody second = new CelestialBody("Second", 0, 0, new Orbit(first, 0, 0));

        CelestialBody third = new CelestialBody("third", 0, 0, new Orbit(second, 0, 0));

        CelestialBody fourth = new CelestialBody("fourth", 0, 0, new Orbit(third, 0, 0));

        List<CelestialBody> allChildren = first.getAllChildren();

        assertFalse(allChildren.contains(first));
        assertTrue(allChildren.contains(second));
        assertTrue(allChildren.contains(third));
        assertTrue(allChildren.contains(fourth));
    }

    @Test
    void getChildren() {
        CelestialBody father = new CelestialBody("Father", 0, 0);
        // And why not mother? You sexist scum!

        CelestialBody child = new CelestialBody("Child", 0, 0);

        father.getChildren().add(child);

        assertEquals(true, father.getChildren().contains(child));

        father.getChildren().remove(child);

        assertEquals(false, father.getChildren().contains(child));
    }

    @Test
    void getParent() {
        CelestialBody parent = new CelestialBody("Parent", 0, 0);
        CelestialBody child = new CelestialBody("Child", 0, 0, new Orbit(parent, 0, 0));

        assertEquals(null, parent.getParent());
        assertEquals(parent, child.getParent());
    }

    @Test
    void getOrbit() {
        CelestialBody parent = new CelestialBody("Parent", 0, 0);
        Orbit orbit = new Orbit(parent, 0, 0);

        CelestialBody child = new CelestialBody("Child", 0, 0, orbit);

        assertEquals(orbit, child.getOrbit());

        assertEquals(null, parent.getOrbit());
    }

    @Test
    void getOrbitAtTime() {
        CelestialBody parent = new CelestialBody("Parent", 0, 0);
        Orbit orbit = new Orbit(parent, 0, 0);

        CelestialBody child = new CelestialBody("Child", 0, 0, orbit);

        assertEquals(orbit, child.getOrbitAtTime(0));

        assertEquals(null, parent.getOrbitAtTime(0));
    }

    @Test
    void getStandardGravitationalParameter() throws IOException {
        CelestialBody earth = getEarth();

        assertEquals(3.986004418E14, earth.getStandardGravitationalParameter(), 1E13);
    }

    @Test
    void getEscapeVelocity() throws IOException {
        CelestialBody earth = getEarth();

        assertEquals(11179.98, earth.getEscapeVelocity(6378000), 1);
    }

    @Test
    void getHillSphere() throws IOException {
        CelestialBody earth = getEarth();

        assertEquals(1.5E9, earth.getHillSphere(), 5e8);
    }

    @Test
    void getCircularOrbitVelocity() throws IOException {
        CelestialBody earth = getEarth();

        assertEquals(7.9e3, earth.getCircularOrbitVelocity(new Position(6.378E6, 0)).getMagnitude(), 10);
    }

    @Test
    void getSphereOfInfluence() throws IOException {
        Astrarium astrarium = JsonHub.importDefaultMap("SolSystem");

        assertEquals(9.24e8D, astrarium.getRoot().getChildren().get(2).getSphereOfInfluence(), 1e6, "Sphere of influence");
    }
}
