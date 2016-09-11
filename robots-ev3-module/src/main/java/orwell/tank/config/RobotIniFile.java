package orwell.tank.config;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import org.ini4j.Wini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.exception.NotFileException;
import orwell.tank.exception.ParseIniException;
import orwell.tank.exception.RobotFileBomException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Michaël Ludmann on 07/09/16.
 */
public class RobotIniFile {
    private final static Logger logback = LoggerFactory.getLogger(RobotIniFile.class);

    private static final String LEFT_MOTOR_SECTION_NAME = "leftMotor";
    private static final String PORT_OPTION_NAME = "port";
    private static final String RIGHT_MOTOR_SECTION_NAME = "rightMotor";
    private static final String RFID_SECTION_NAME = "rfidSensor";
    private static final String US_SECTION_NAME = "usSensor";
    private static final String INVERTED_OPTION_NAME = "isInverted";
    private static final String PROXY_SECTION_NAME = "proxy";
    private static final String PUSH_PORT_OPTION_NAME = "pushPort";
    private static final String PULL_PORT_OPTION_NAME = "pullPort";
    private static final String IP_OPTION_NAME = "ip";
    private static final String SENSOR_DELAY_OPTION_NAME = "sensorMessageDelayMs";
    private static final String VOLUME_OPTION_NAME = "volume";
    private static final String MISC_SECTION_NAME = "misc";

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

    public RobotFileBom parse() throws ExceptionInInitializerError, ParseIniException, RobotFileBomException {
        RobotFileBom robotFileBom = new RobotFileBom();
        robotFileBom.setLeftMotorPort(charToPort(getLeftMotorPort()));
        robotFileBom.setIsLeftMotorInverted(getIsLeftMotorInverted());
        robotFileBom.setRightMotorPort(charToPort(getRightMotorPort()));
        robotFileBom.setIsRightMotorInverted(getIsRightMotorInverted());
        robotFileBom.setRfidSensorPort(charToPort(getRfidSensorPort()));
        robotFileBom.setUsSensorPort(charToPort(getUsSensorPort()));
        robotFileBom.setProxyPushPort(getProxyPushPort());
        robotFileBom.setProxyPullPort(getProxyPullPort());
        robotFileBom.setProxyIp(getProxyIp());
        robotFileBom.setSensorMessageDelayMs(getSensorMessageDelay());
        robotFileBom.setVolume(this.getVolume());

        if (!robotFileBom.isModelValid()) {
            throw new RobotFileBomException(robotFileBom);
        }
        return robotFileBom;
    }

    private int getVolume() {
        return iniFile.get(MISC_SECTION_NAME, VOLUME_OPTION_NAME, int.class);
    }

    private int getSensorMessageDelay() {
        return iniFile.get(PROXY_SECTION_NAME, SENSOR_DELAY_OPTION_NAME, int.class);
    }

    private String getProxyIp() {
        return iniFile.get(PROXY_SECTION_NAME, IP_OPTION_NAME, String.class);
    }

    private int getProxyPushPort() {
        return iniFile.get(PROXY_SECTION_NAME, PUSH_PORT_OPTION_NAME, int.class);
    }

    private int getProxyPullPort() {
        return iniFile.get(PROXY_SECTION_NAME, PULL_PORT_OPTION_NAME, int.class);
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
