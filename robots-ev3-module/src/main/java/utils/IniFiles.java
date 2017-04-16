package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.config.RobotColourConfigIniFile;
import orwell.tank.config.RobotIniFile;

import java.io.IOException;

public class IniFiles {
    private final static Logger logback = LoggerFactory.getLogger(IniFiles.class);
    private final String robotIniFileName;
    private final String colourConfigIniFileName;

    public RobotIniFile robotIniFile;
    public RobotColourConfigIniFile colourConfigIniFile;

    public IniFiles(String robotIniFileName, String colourConfigIniFileName) {
        this.robotIniFileName = robotIniFileName;
        this.colourConfigIniFileName = colourConfigIniFileName;

        robotIniFile = robotConfigurationFromFile(robotIniFileName);
        colourConfigIniFile = colourConfigurationFromFile(colourConfigIniFileName);
    }

    public boolean isEmpty() {
        return robotIniFile == null && colourConfigIniFile == null;
    }

    public boolean isPartiallyEmpty() {
        return robotIniFile == null || colourConfigIniFile == null;
    }

    private RobotIniFile robotConfigurationFromFile(final String filePath) {
        try {
            return new RobotIniFile(filePath);
        } catch (final IOException e) {
            logback.error(e.getStackTrace().toString());
            return null;
        }
    }

    private RobotColourConfigIniFile colourConfigurationFromFile(final String filePath) {
        try {
            return new RobotColourConfigIniFile(filePath);
        } catch (final IOException e) {
            logback.error(e.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public String toString() {
        if (robotIniFile != null && colourConfigIniFile != null) {
            return "[" + robotIniFileName + "] and [" + colourConfigIniFileName + "] exist.";
        }
        else if (robotIniFile == null && colourConfigIniFile != null) {
            return "[" + colourConfigIniFileName + "] exists, but [" + robotIniFileName + "] does not exist.";
        }
        else if (robotIniFile != null && colourConfigIniFile == null) {
            return "[" + robotIniFileName + "] exists, but [" + colourConfigIniFileName + "] does not exist.";
        }
        else {
            return "[" + robotIniFileName + "] and [" + colourConfigIniFileName + "] do not exist.";
        }
    }
}
