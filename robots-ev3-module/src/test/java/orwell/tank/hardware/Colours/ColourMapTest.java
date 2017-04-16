package orwell.tank.hardware.Colours;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class ColourMapTest {
    private final static Logger logback = LoggerFactory.getLogger(ColourMapTest.class);
    private static final float SIGMA = 0.001f;
    private ColourMap colourMap = new ColourMap();
    private static final float sigmaFactor = 3.5f;

    private void initColourMap() {
        colourMap.addColour(
                EnumColours.RED,
                new ColourMatcher(
                        new RgbColour(1f, 0f, 0f),
                        new RgbColourSigma(SIGMA, SIGMA, SIGMA),
                        sigmaFactor));
        colourMap.addColour(
                EnumColours.BLUE,
                new ColourMatcher(
                        new RgbColour(0f, 0f, 1f),
                        new RgbColourSigma(SIGMA, SIGMA, SIGMA),
                        sigmaFactor));
        colourMap.addColour(
                EnumColours.GREEN,
                new ColourMatcher(
                        new RgbColour(0f, 1f, 0f),
                        new RgbColourSigma(SIGMA, SIGMA, SIGMA),
                        sigmaFactor));
    }

    @Test
    public void getColour_None() {
        initColourMap();
        RgbColour rgbColour = new RgbColour(0, 0, 0);

        EnumColours colour = colourMap.getColour(rgbColour);

        assertEquals(EnumColours.NONE, colour);
    }

    @Test
    public void getColour_Red() {
        initColourMap();
        RgbColour rgbColour = new RgbColour(0.9990f, 0, 0);

        EnumColours colour = colourMap.getColour(rgbColour);

        assertEquals(EnumColours.RED, colour);
    }

    @Test
    public void getColour_Green() {
        initColourMap();
        RgbColour rgbColour = new RgbColour(0, 0.9990f, 0);

        EnumColours colour = colourMap.getColour(rgbColour);

        assertEquals(EnumColours.GREEN, colour);
    }

    @Test
    public void getColour_Blue() {
        initColourMap();
        RgbColour rgbColour = new RgbColour(0, 0, 0.9990f);

        EnumColours colour = colourMap.getColour(rgbColour);

        assertEquals(EnumColours.BLUE, colour);
    }
}