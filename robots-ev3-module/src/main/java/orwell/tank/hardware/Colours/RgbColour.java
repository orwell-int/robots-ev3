package orwell.tank.hardware.Colours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public class RgbColour {
    private final static Logger logback = LoggerFactory.getLogger(RgbColour.class);

    public enum RgbColourChannels {Red, Green, Blue};

    public float red;
    public float green;
    public float blue;

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

    public void setChannel(RgbColourChannels channel, float value) {
        switch (channel) {
            case Red:
                red = value;
                break;
            case Green:
                green = value;
                break;
            case Blue:
                blue = value;
                break;
        }
    }

    @Override
    public String toString() {
        return "RgbColour: red = " + red + "; green = " + green + "; blue = " + blue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;

        RgbColour rgbColour = (RgbColour) obj;

        return (this.red == rgbColour.red &&
                this.green == rgbColour.green &&
                this.blue == rgbColour.blue);
    }
}
