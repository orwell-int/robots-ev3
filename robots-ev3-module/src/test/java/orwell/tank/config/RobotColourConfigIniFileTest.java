package orwell.tank.config;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.exception.FileBomException;
import orwell.tank.exception.ParseIniException;
import orwell.tank.hardware.Colours.EnumColours;
import orwell.tank.hardware.Colours.RgbColour;
import orwell.tank.hardware.Colours.RgbColourSigma;

import java.io.IOException;

import static org.junit.Assert.*;
/**
 * Created by Michaël Ludmann on 05/02/17.
 */
public class RobotColourConfigIniFileTest {
    private static final Logger logback = LoggerFactory.getLogger(RobotColourConfigIniFileTest.class);

    private static final String COLOUR_INI_FILENAME = "src/test/resources/colours.TEST.ini";
    private static final float DOUBLE_COMPARE_TOLERANCE = 1E-4f;
    private static final double SIGMA_FACTOR_TEST = 5;
    private static final float RED_RED_CHANNEL_TEST = 0.3f;
    private static final float RED_GREEN_CHANNEL_TEST = 0.1f;
    private static final float RED_BLUE_CHANNEL_TEST = 0.0f;
    private static final float RED_RED_SIGMA_CHANNEL_TEST = 0.1f;
    private static final float RED_GREEN_SIGMA_CHANNEL_TEST = 1E-4f;
    private static final float RED_BLUE_SIGMA_CHANNEL_TEST = 0f;
    private static final float YELLOW_RED_CHANNEL_TEST = 0.5f;
    private static final float YELLOW_GREEN_CHANNEL_TEST = 0.5f;
    private static final float YELLOW_BLUE_CHANNEL_TEST = 0.5f;
    private static final float YELLOW_RED_SIGMA_CHANNEL_TEST = 0f;
    private static final float YELLOW_GREEN_SIGMA_CHANNEL_TEST = 0f;
    private static final float YELLOW_BLUE_SIGMA_CHANNEL_TEST = 0f;
    private RobotColourConfigIniFile configIniFile;
    private RobotColourConfigFileBom configFileBom;

    @Before
    public void setup() throws IOException {
        configIniFile = new RobotColourConfigIniFile(COLOUR_INI_FILENAME);
    }

    @Test
    public void testConstructor() {
        assertNotNull(configIniFile);
    }

    @Test
    public void testParseSigmaFactor() {
        parseFile();

        assertEquals(SIGMA_FACTOR_TEST, configFileBom.getSigmaFactor(), DOUBLE_COMPARE_TOLERANCE);
    }

    @Test
    public void testParseRedSection_Averages() {
        parseFile();

        RgbColour redColour =
                new RgbColour(RED_RED_CHANNEL_TEST, RED_GREEN_CHANNEL_TEST, RED_BLUE_CHANNEL_TEST);

        assertEquals(redColour, configFileBom.getRgbColour(EnumColours.RED));
    }

    @Test
    public void testParseRedSection_Sigmas() {
        parseFile();

        RgbColourSigma redColourSigma =
                new RgbColourSigma(RED_RED_SIGMA_CHANNEL_TEST, RED_GREEN_SIGMA_CHANNEL_TEST, RED_BLUE_SIGMA_CHANNEL_TEST);

        assertEquals(redColourSigma, configFileBom.getRgbColourSigma(EnumColours.RED));
    }

    @Test
    public void testParseYellowSection_Averages() {
        parseFile();

        RgbColour yellowColour =
                new RgbColour(YELLOW_RED_CHANNEL_TEST, YELLOW_GREEN_CHANNEL_TEST, YELLOW_BLUE_CHANNEL_TEST);

        assertEquals(yellowColour, configFileBom.getRgbColour(EnumColours.YELLOW));
    }

    @Test
    public void testParseYellowSection_Sigmas() {
        parseFile();

        RgbColourSigma yellowColourSigma =
                new RgbColourSigma(YELLOW_RED_SIGMA_CHANNEL_TEST, YELLOW_GREEN_SIGMA_CHANNEL_TEST, YELLOW_BLUE_SIGMA_CHANNEL_TEST);

        assertEquals(yellowColourSigma, configFileBom.getRgbColourSigma(EnumColours.YELLOW));
    }

    @Test
    public void testParseGreenSection_Averages() {
        parseFile();

        // Green config is not in ini colour file
        RgbColour greenColour =
                new RgbColour(0, 0, 0);

        assertEquals(greenColour, configFileBom.getRgbColour(EnumColours.GREEN));
    }

    @Test
    public void testParseGreenSection_Sigmas() {
        parseFile();

        // Green config is not in ini colour file
        RgbColourSigma greenColourSigma =
                new RgbColourSigma(0, 0, 0);

        assertEquals(greenColourSigma, configFileBom.getRgbColourSigma(EnumColours.GREEN));
    }

    private void parseFile() {
        try {
            configFileBom = configIniFile.parse();
        } catch (Exception e) {
            logback.error("Colour config file BOM is null, parsing file " + COLOUR_INI_FILENAME + " failed", e);
            fail();
        }
    }

}
