package astrarium;

/**
 * A class that wraps all the other objects of the simulation.
 * Created on 20/02/2017.
 *
 * @author Vittorio
 */
public class Astrarium {
    /**
     * Newtonian constant of gravitation.
     */
    public static final double G = 6.67408e-11;

    /**
     * Main body of the system.
     */
    final private CelestialBody root;

    /**
     * Time of the simulation.
     */
    private long time = 0; // ago in a galaxy far far away...

    public Astrarium(CelestialBody root) {
        this.root = root;
    }

    //region System Factory
    public static Astrarium getSolarSystem() {
        CelestialBody sol = new CelestialBody("Sun", 1.9891e30D, 6.95700e8D);

        CelestialBody earth = new CelestialBody("Earth", 5.9736E24, 6.371E6, new Orbit(sol, 1.49598261E11, 0.01671123, Math.PI / 2, 3.0525809, 5.02829357, 6.259047404));

        CelestialBody moon = new CelestialBody("Moon", 1.9736E24, 6.371E3, new Orbit(earth, 3.84399e8, 0.01671123));

        new CelestialBody("Phobos", 5.9736E10, 6.371E3, new Orbit(moon, 0.59598261E5, 0.61671123));
        //CelestialBody earth = new CelestialBody("Earth", 5.9736E24, 6.371E6, new Orbit(sol, 1.49598261E11, 2, 0, 0, 0, 0));

        new CelestialBody("Mars", 6.4171e23, 6.371E6, new Orbit(sol, 2.279392e11, 0.0934, 0, 0, 0, 0));

        new CelestialBody("Pluto", 1.303e22, 1.187e3, new Orbit(sol, 5.90638e12, 0.2488, 0.2994985, 0, 0, 0));

        new CelestialBody("Ceres", 9.393e20, 473e3, new Orbit(sol, 414e9, 0.1161977, 0.168379107187, 0, 0, 0));

        new CelestialBody("67P/Churyumov–Gerasimenko", 9.982e12, 4e3, new Orbit(sol, 518e9, 0.64102, 0.122879906, 0.87523026, 0.22305308, 5.30073947));

        return new Astrarium(sol);
    }

    public static Astrarium getKerbolSystem() {
        CelestialBody kerbol = new CelestialBody("Kerbol", 1.756567e+28, 2.616e+05);

        new CelestialBody("Kerbin", 5.2915793e22, 6e5, new Orbit(kerbol, 1.3599840256e10D, 0.0, 0, 0, 0, 0));

        new CelestialBody("Duna", 4.5154812e21, 3.2e5, new Orbit(kerbol, 2.0726155264e10D, 0.751, 0.001, 3 * 4 / Math.PI, 0, 0));

        //new CelestialBody("Lolz", 4.5154812e21, 3.2e5, new Orbit(kerbol, 3.0726155264e10D, 0.5, 0.001, 3 * 4 / Mathematics.PI, 0, 0));

        return new Astrarium(kerbol);
    }

    public static Astrarium getTestSystem() {
        CelestialBody sun = new CelestialBody("Sun", 1e30, 1e6, null);

        CelestialBody planet = new CelestialBody("Planet", 5e9, 1e4,
                new Orbit(sun, 1e8, 0.8, Math.PI / 4, 0.0, 0, 0)
        );

        return new Astrarium(sun);
    }
    //endregion System Factory

    //region Getters
    public CelestialBody getRoot() {
        return root;
    }

    public long getTime() {
        return time;
    }
    //endregion Getters

    //region Setters
    public void setTime(long time) {
        this.time = time;

        root.renderAtTime(time);
    }
    //endregion Setters
}
