package orwell.tank.config;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.exception.FileBomException;
import orwell.tank.exception.NotFileException;
import orwell.tank.exception.ParseIniException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RobotIniFile {
    private static final Logger logback = LoggerFactory.getLogger(RobotIniFile.class);

    private static final String LEFT_MOTOR_SECTION_NAME = "leftMotor";
    private static final String PORT_OPTION_NAME = "port";
    private static final String RIGHT_MOTOR_SECTION_NAME = "rightMotor";
    private static final String RFID_SECTION_NAME = "rfidSensor";
    private static final String US_SECTION_NAME = "usSensor";
    private static final String INVERTED_OPTION_NAME = "isInverted";
    private static final String PROXY_SECTION_NAME = "proxy";
    private static final String SENSOR_DELAY_OPTION_NAME = "sensorMessageDelayMs";
    private static final String COLOUR_SECTION_NAME = "colourSensor";
    private static final String SOUNDS_SECTION_NAME = "sounds";
    private static final String GLOBAL_VOLUME_OPTION_NAME = "globalVolume";
    private static final String ENDGAME_VOLUME_OPTION_NAME = "endgameVolume";
    private static final String VICT_FILEPATH_OPTION_NAME = "victoryFilepath";
    private static final String DEAF_FILEPATH_OPTION_NAME = "defeatFilepath";
    private static final String DRAW_FILEPATH_OPTION_NAME = "drawFilepath";
    private static final String BROADCAST_PORT_OPTION_NAME = "broadcastPort";
    private static final String BROADCAST_TIMEOUT_OPTION_NAME = "broadcastTimeout";
    private static final String CAMERA_SECTION_NAME = "camera";
    private static final String START_CAMERA_SCRIPT_PATH_OPTION_NAME = "startCameraScriptPath";
    private static final String KILL_CAMERA_SCRIPT_PATH_OPTION_NAME = "killCameraScriptPath";

    private final Wini iniFile;

    public RobotIniFile(String filePath) throws IOException {
        final File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }
        if (file.isDirectory()) {
            throw new NotFileException(filePath);
        }

        iniFile = new Wini(file);
    }

    public RobotFileBom parse() throws ExceptionInInitializerError, ParseIniException, FileBomException {
        RobotFileBom robotFileBom = new RobotFileBom();
        robotFileBom.setLeftMotorPort(charToPort(getLeftMotorPort()));
        robotFileBom.setIsLeftMotorInverted(getIsLeftMotorInverted());
        robotFileBom.setRightMotorPort(charToPort(getRightMotorPort()));
        robotFileBom.setIsRightMotorInverted(getIsRightMotorInverted());
        robotFileBom.setRfidSensorPort(charToPort(getRfidSensorPort()));
        robotFileBom.setUsSensorPort(charToPort(getUsSensorPort()));
        robotFileBom.setSensorMessageDelayMs(getSensorMessageDelay());
        robotFileBom.setColourSensorPort(charToPort(getColourSensorPort()));
        robotFileBom.setGlobalVolume(getGlobalVolume());
        robotFileBom.setEndGameVolume(getEndgameVolume());
        robotFileBom.setSoundVictoryFilepath(getVictoryFilepath());
        robotFileBom.setSoundDefeatFilepath(getDefeatFilepath());
        robotFileBom.setSoundDrawFilepath(getDrawFilepath());
        robotFileBom.setBroadcastPort(getBroadcastPort());
        robotFileBom.setBroadcastTimeout(getBroadcastTimeout());

        if (!robotFileBom.isModelValid()) {
            throw new FileBomException(robotFileBom);
        }
        return robotFileBom;
    }

    private String getStartCameraScriptPath() {
        return iniFile.get(CAMERA_SECTION_NAME, START_CAMERA_SCRIPT_PATH_OPTION_NAME, String.class);
    }

    private String getKillCameraScriptPath() {
        return iniFile.get(CAMERA_SECTION_NAME, KILL_CAMERA_SCRIPT_PATH_OPTION_NAME, String.class);
    }

    private int getBroadcastPort() {
        return iniFile.get(PROXY_SECTION_NAME, BROADCAST_PORT_OPTION_NAME, int.class);
    }

    private int getBroadcastTimeout() {
        return iniFile.get(PROXY_SECTION_NAME, BROADCAST_TIMEOUT_OPTION_NAME, int.class);
    }

    private int getGlobalVolume() {
        return iniFile.get(SOUNDS_SECTION_NAME, GLOBAL_VOLUME_OPTION_NAME, int.class);
    }

    private int getEndgameVolume() {
        return iniFile.get(SOUNDS_SECTION_NAME, ENDGAME_VOLUME_OPTION_NAME, int.class);
    }

    private String getVictoryFilepath() {
        return iniFile.get(SOUNDS_SECTION_NAME, VICT_FILEPATH_OPTION_NAME, String.class);
    }

    private String getDefeatFilepath() {
        return iniFile.get(SOUNDS_SECTION_NAME, DEAF_FILEPATH_OPTION_NAME, String.class);
    }

    private String getDrawFilepath() {
        return iniFile.get(SOUNDS_SECTION_NAME, DRAW_FILEPATH_OPTION_NAME, String.class);
    }

    private int getSensorMessageDelay() {
        return iniFile.get(PROXY_SECTION_NAME, SENSOR_DELAY_OPTION_NAME, int.class);
    }

    private boolean getIsLeftMotorInverted() {
        return iniFile.get(LEFT_MOTOR_SECTION_NAME, INVERTED_OPTION_NAME, boolean.class);
    }

    private char getUsSensorPort() {
        try {
            return iniFile.get(US_SECTION_NAME, PORT_OPTION_NAME, char.class);
        } catch (IllegalArgumentException e) {
            logback.info("UsSensor Port argument not understood, defaulting to null");
            return 0;
        }
    }

    private char getRfidSensorPort() {
        try {
            return iniFile.get(RFID_SECTION_NAME, PORT_OPTION_NAME, char.class);
        } catch (IllegalArgumentException e) {
            logback.info("RfidSensor Port argument not understood, defaulting to null");
            return 0;
        }
    }

    private char getColourSensorPort() {
        try {
            return iniFile.get(COLOUR_SECTION_NAME, PORT_OPTION_NAME, char.class);
        } catch (IllegalArgumentException e) {
            logback.info("ColourSensor Port argument not understood, defaulting to null");
            return 0;
        }
    }

    private char getRightMotorPort() {
        return iniFile.get(RIGHT_MOTOR_SECTION_NAME, PORT_OPTION_NAME, char.class);
    }

    private char getLeftMotorPort() {
        return iniFile.get(LEFT_MOTOR_SECTION_NAME, PORT_OPTION_NAME, char.class);
    }

    private Port charToPort(char c) throws ParseIniException, ExceptionInInitializerError {
        switch (c) {
            case 'A':
                return MotorPort.A;
            case 'B':
                return MotorPort.B;
            case 'C':
                return MotorPort.C;
            case 'D':
                return MotorPort.D;
            case '1':
                return SensorPort.S1;
            case '2':
                return SensorPort.S2;
            case '3':
                return SensorPort.S3;
            case '4':
                return SensorPort.S4;
            case 0:
                return null;
            default:
                throw new ParseIniException("Port value [" + c + "] is not valid. " +
                        "Should be A, B, C, D (motors) or 1, 2, 3, 4 (sensors)");
        }
    }

    public boolean getIsRightMotorInverted() {
        return iniFile.get(RIGHT_MOTOR_SECTION_NAME, INVERTED_OPTION_NAME, boolean.class);
    }
}
