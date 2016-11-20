package orwell.tank.hardware;

import lejos.robotics.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 20/11/16.
 */
public class ColorConverter {
    private final static Logger logback = LoggerFactory.getLogger(ColorConverter.class);
    private static final float RED_THRESHOLD = 0.05f;
    private static final float GREEN_THRESHOLD = 0.05f;
    private static final float BLUE_THRESHOLD = 0.05f;
    private final float red;
    private final float green;
    private final float blue;
    private int color;

    public ColorConverter(float[] samples) {
        if (samples.length != 3) {
            logback.error("Color sample has an invalid length of " + samples.length);
            color = Color.NONE;
        }
        red = samples[0];
        green = samples[1];
        blue = samples[2];
        logback.debug("RGB value read are " + red + " " + green + " " + blue);
        setColor(samples);
    }

    public int getColor() {
        return color;
    }

    public void setColor(float[] samples) {
        color = Color.NONE;

        if (red > RED_THRESHOLD) {
            color = Color.RED;
        }
        if (green > GREEN_THRESHOLD) {
            color = Color.GREEN;
        }
        if (blue > BLUE_THRESHOLD) {
            color = Color.BLUE;
        }
    }
}
