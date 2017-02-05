package utils;

import orwell.tank.config.RobotColourConfigIniFile;
import orwell.tank.config.RobotIniFile;

/**
 * Created by Michaël Ludmann on 05/02/17.
 */
public class IniFiles {
    public RobotIniFile robotIniFile;
    public RobotColourConfigIniFile colourConfigIniFile;

    public boolean isEmpty() {
        return robotIniFile == null && colourConfigIniFile == null;
    }

    public boolean isPartiallyEmpty() {
        return robotIniFile == null || colourConfigIniFile == null;
    }
}
