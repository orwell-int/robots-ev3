package config;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.config.RobotFileBom;
import orwell.tank.config.RobotIniFile;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Michaël Ludmann on 09/09/16.
 */
public class RobotIniFileTest {
    private final static Logger logback = LoggerFactory.getLogger(RobotIniFileTest.class);
    private static final String R2D2_INI_FILENAME = "src/test/resources/tank.TEST.ini";

    @Test
    public void testConstructor() throws IOException {
        RobotIniFile iniFile = new RobotIniFile(R2D2_INI_FILENAME);
        assertNotNull(iniFile);
    }

    @Test
    public void testParse() throws Exception {
        RobotIniFile iniFile = new RobotIniFile(R2D2_INI_FILENAME);
        try {
            RobotFileBom fileBom = iniFile.parse();
            assertEquals(MotorPort.C, fileBom.getLeftMotorPort());
            assertTrue(fileBom.isLeftMotorInverted());
            assertEquals(MotorPort.D, fileBom.getRightMotorPort());
            assertTrue(fileBom.isRightMotorInverted());
            assertEquals(SensorPort.S1, fileBom.getRfidSensorPort());
            assertEquals(SensorPort.S4, fileBom.getUsSensorPort());
            assertEquals(10001, fileBom.getProxyPushPort());
            assertEquals(10000, fileBom.getProxyPullPort());
            assertEquals("192.168.0.16", fileBom.getProxyIp());
            assertEquals(50, fileBom.getSensorMessageDelayMs());
            assertEquals(30, fileBom.getVolume());
        } catch (ExceptionInInitializerError e) {
            logback.warn("Cannot perform the test because no EV3 device is on the local network");
        }
    }
}
