package orwell.tank.hardware.Colours;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public class RgbColour {
    public final float red;
    public final float green;
    public final float blue;

    public RgbColour(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RgbColour add(RgbColourSigma rgbColourSigma) {
        return new RgbColour(
                red + rgbColourSigma.red,
                green + rgbColourSigma.green,
                blue + rgbColourSigma.blue);
    }

    public RgbColour sub(RgbColourSigma rgbColourSigma) {
        return new RgbColour(
                red - rgbColourSigma.red,
                green - rgbColourSigma.green,
                blue - rgbColourSigma.blue);
    }

    public boolean isLowerThan(RgbColour rgbColour) {
        return red < rgbColour.red &&
                green < rgbColour.green &&
                blue < rgbColour.blue;
    }
}
