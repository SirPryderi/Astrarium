package fx;


import javafx.scene.paint.Color;

import java.util.Random;

/**
 * A set of utilities method that allows the dynamic creation of pastel colors.
 * Created on 15/03/2017.
 *
 * @author Vittorio
 */
@SuppressWarnings("WeakerAccess")
final public class Colors {
    /**
     * Prevents the class from being instantiated
     */
    private Colors() {
    }

    /**
     * Generates a random pastel color. It is not guaranteed to be unique.
     *
     * @return random color
     */
    public static Color randomPastelColor() {
        Random random = new Random();
        final float hue = random.nextFloat();
        // Saturation between 0.7 and 0.9
        final float saturation = (random.nextInt(2000) + 7000) / 10000f;
        final float luminance = 0.7f;

        return Color.hsb(hue, saturation, luminance);
    }

    /**
     * Returns a unique color generated from the object's hash.
     * Objects with the same hash will have the same color.
     *
     * @param object the object to hash
     * @return a unique color generated from the hash
     */
    public static Color hashColor(Object object) {
        double hue = object.hashCode();
        // Intensity of the color
        final float saturation = 0.8f;
        // Brightness of the color
        final float luminance = 0.7f;

        return Color.hsb(hue, saturation, luminance);
    }
}
