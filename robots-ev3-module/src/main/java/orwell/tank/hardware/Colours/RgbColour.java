package orwell.tank.hardware.Colours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public class RgbColour {
    private final static Logger logback = LoggerFactory.getLogger(RgbColour.class);

    public final float red;
    public final float green;
    public final float blue;

    public RgbColour(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        logback.debug(this.toString());
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

    @Override
    public String toString() {
        return "RgbColour: red = " + red + "; green = " + green + "; blue = " + blue;
    }
}
