package orwell.tank.hardware.Colours;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public class RgbColourSigma extends RgbColour {

    public RgbColourSigma(float red, float green, float blue) {
        super(red, green, blue);
    }

    public RgbColourSigma mult(float factor) {
        return new RgbColourSigma(red * factor, green * factor, blue * factor);
    }
}
