//Luminance.java
//simulates how bright we see colors and contrast
import java.awt.Color;

public class Luminance {

    // return the monochrome luminance of given color
    public static double lum(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return .299*r + .587*g + .114*b;
    }

    // are the two colors compatible?
    public static boolean compatible(Color a, Color b) {
        return Math.abs(lum(a) - lum(b)) >=200.0;
    }
}