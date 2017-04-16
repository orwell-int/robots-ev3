package orwell.tank.config;

import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.exception.NotFileException;
import orwell.tank.exception.ParseIniException;
import orwell.tank.exception.FileBomException;
import orwell.tank.hardware.Colours.EnumColours;
import orwell.tank.hardware.Colours.RgbColour;
import orwell.tank.hardware.Colours.RgbColourSigma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Michaël Ludmann on 05/02/17.
 */
public class RobotColourConfigIniFile {
    private final static Logger logback = LoggerFactory.getLogger(RobotColourConfigIniFile.class);

    private static final String GLOBAL_SECTION_NAME = "global";
    private static final String AVERAGE_OPTION_PREFIX = "average";
    private static final String SIGMA_OPTION_PREFIX = "sigma";
    private static final String SIGMA_FACTOR_OPTION_NAME = "sigmaFactor";
    private RobotColourConfigFileBom configFileBom = new RobotColourConfigFileBom();

    private final Wini colourIniFile;

    public RobotColourConfigIniFile(String filePath) throws IOException {
        final File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }
        if (file.isDirectory()) {
            throw new NotFileException(filePath);
        }

        logback.info("Correctly loaded colour ini file " + filePath);

        colourIniFile = new Wini(file);
    }

    public RobotColourConfigFileBom parse() throws ExceptionInInitializerError, ParseIniException, FileBomException {
        configFileBom.setSigmaFactor(getSigmaFactor());
        setColourSectionsValues();

        logback.info("Correctly parsed colour ini file ");

        return configFileBom;
    }

    private void setColourSectionsValues() {
        for (EnumColours colour : EnumColours.values()) {
            setAverageColourSectionValues(colour);
            setSigmaColourSectionValues(colour);
        }
    }

    private void setAverageColourSectionValues(EnumColours colour) {
        String colourName = colour.name();
        RgbColour rgbColour = new RgbColour(-1, -1, -1);

        for (RgbColour.RgbColourChannels channel : RgbColour.RgbColourChannels.values()) {
            String channelName = channel.name();
            rgbColour.setChannel(channel, colourIniFile.get(colourName, AVERAGE_OPTION_PREFIX + channelName, float.class));
        }

        configFileBom.setRgbColour(colour, rgbColour);
    }

    private void setSigmaColourSectionValues(EnumColours colour) {
        String colourName = colour.name();
        RgbColourSigma rgbColourSigma = new RgbColourSigma(-1, -1, -1);

        for (RgbColourSigma.RgbColourChannels channel : RgbColourSigma.RgbColourChannels.values()) {
            String channelName = channel.name();
            rgbColourSigma.setChannel(channel, colourIniFile.get(colourName, SIGMA_OPTION_PREFIX + channelName, float.class));
        }

        configFileBom.setRgbColourSigma(colour, rgbColourSigma);
    }

    private float getSigmaFactor() {
        return colourIniFile.get(GLOBAL_SECTION_NAME, SIGMA_FACTOR_OPTION_NAME, float.class);
    }
}