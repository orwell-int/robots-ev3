package orwell.tank.config;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.hardware.Colours.EnumColours;
import orwell.tank.hardware.Colours.RgbColour;
import orwell.tank.hardware.Colours.RgbColourSigma;

import static org.junit.Assert.assertEquals;

public class RobotColourConfigFileBomTest {
    private static final float SIGMA_FACTOR_TEST = 2;
    private static final float RED_CHANNEL_TEST = 0.02f;
    private static final float GREEN_CHANNEL_TEST = 1f;
    private static final float BLUE_CHANNEL_TEST = 0.1f;

    private RobotColourConfigFileBom configFileBom;

    @Before
    public void setup() {
        configFileBom = new RobotColourConfigFileBom();
    }

    @Test
    public void sigmaFactor() {
        configFileBom.setSigmaFactor(SIGMA_FACTOR_TEST);

        assertEquals(SIGMA_FACTOR_TEST, configFileBom.getSigmaFactor(), 0.001);
    }

    @Test
    public void colour() {
        RgbColour rgbColour = new RgbColour(RED_CHANNEL_TEST, GREEN_CHANNEL_TEST, BLUE_CHANNEL_TEST);

        configFileBom.setRgbColour(EnumColours.GREEN, rgbColour);

        assertEquals(rgbColour, configFileBom.getRgbColour(EnumColours.GREEN));
    }

    @Test
    public void colourSigma() {
        RgbColourSigma rgbColourSigma = new RgbColourSigma(RED_CHANNEL_TEST, GREEN_CHANNEL_TEST, BLUE_CHANNEL_TEST);

        configFileBom.setRgbColourSigma(EnumColours.GREEN, rgbColourSigma);

        assertEquals(rgbColourSigma, configFileBom.getRgbColourSigma(EnumColours.GREEN));
    }
}
