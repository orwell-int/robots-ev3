package config;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import org.junit.Test;
import orwell.tank.config.RobotFileBom;
import orwell.tank.config.RobotIniFile;
import orwell.tank.exception.RobotFileBomException;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Michaël Ludmann on 09/09/16.
 */
public class RobotIniFileTest {
    private static final String R2D2_INI_FILENAME = "src/test/resources/tank.TEST.ini";

    @Test
    public void testConstructor() throws IOException {
        RobotIniFile iniFile = new RobotIniFile(R2D2_INI_FILENAME);
        assertNotNull(iniFile);
    }

    @Test
    public void testParse() throws Exception, RobotFileBomException {
        RobotIniFile iniFile = new RobotIniFile(R2D2_INI_FILENAME);
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
    }
}
