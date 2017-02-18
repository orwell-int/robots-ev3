package orwell.tank.config;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Michaël Ludmann on 11/09/16.
 */
public class RobotFileBomTest {
    private final static Logger logback = LoggerFactory.getLogger(RobotFileBomTest.class);

    private static final int VOLUME_TEST = 50;
    private static final boolean IS_LEFT_INVERTED_TEST = false;
    private static final boolean IS_RIGHT_INVERTED_TEST = false;
    private static final int PUSH_PORT_TEST = 1001;
    private static final int PULL_PORT_TEST = 1000;
    private static final String PROXY_IP_TEST = "127.0.0.1";
    private static final int SENSOR_DELAY_MS_TEST = 50;
    private RobotFileBom fileBom;

    @Before
    public void setup() {
        try {
            // the following variables cannot be class fields, since they will throw
            // an exception through BrickFinder class if no EV3 is found locally
            // This is why all those test run into a try/catch, since they need an EV3
            final Port RFID_PORT_TEST = SensorPort.S1;
            final Port US_PORT_TEST = SensorPort.S2;
            final Port LEFT_MOTOR_PORT_TEST = MotorPort.A;
            final Port RIGHT_MOTOR_PORT_TEST = MotorPort.B;

            fileBom = new RobotFileBom();
            fileBom.setLeftMotorPort(LEFT_MOTOR_PORT_TEST);
            fileBom.setIsLeftMotorInverted(IS_LEFT_INVERTED_TEST);
            fileBom.setRightMotorPort(RIGHT_MOTOR_PORT_TEST);
            fileBom.setIsRightMotorInverted(IS_RIGHT_INVERTED_TEST);
            fileBom.setRfidSensorPort(RFID_PORT_TEST);
            fileBom.setUsSensorPort(US_PORT_TEST);
            fileBom.setProxyPushPort(PUSH_PORT_TEST);
            fileBom.setProxyPullPort(PULL_PORT_TEST);
            fileBom.setProxyIp(PROXY_IP_TEST);
            fileBom.setSensorMessageDelayMs(SENSOR_DELAY_MS_TEST);
            fileBom.setGlobalVolume(VOLUME_TEST);
        } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
            logback.warn("Cannot perform the test because no EV3 device is on the local network");
        }
    }

    @Test
    public void testIsModelComplete_true() {
        if (fileBom != null) {
            assertTrue(fileBom.isModelValid());
        } else {
            logback.warn("Cannot perform the test because no EV3 device is on the local network");
        }
    }

    @Test
    public void testIsModelComplete_false_missingMotor() {
        if (fileBom != null) {
            fileBom.setLeftMotorPort(null);
            assertFalse(fileBom.isModelValid());
        } else {
            logback.warn("Cannot perform the test because no EV3 device is on the local network");
        }
    }

    @Test
    public void testIsModelComplete_false_sameMotorPorts() {
        if (fileBom != null) {
            fileBom.setLeftMotorPort(MotorPort.A);
            fileBom.setRightMotorPort(MotorPort.A);
            assertFalse(fileBom.isModelValid());
        } else {
            logback.warn("Cannot perform the test because no EV3 device is on the local network");
        }
    }

    @Test
    public void testIsModelComplete_false_sameSensorPorts() {
        if (fileBom != null) {
            fileBom.setRfidSensorPort(SensorPort.S1);
            fileBom.setUsSensorPort(SensorPort.S1);
            assertFalse(fileBom.isModelValid());
        } else {
            logback.warn("Cannot perform the test because no EV3 device is on the local network");
        }
    }

    @Test
    public void testIsModelComplete_true_nullSensorPorts() {
        if (fileBom != null) {
            fileBom.setRfidSensorPort(null);
            fileBom.setUsSensorPort(null);
            assertTrue(fileBom.isModelValid());
        } else {
            logback.warn("Cannot perform the test because no EV3 device is on the local network");
        }
    }

    @Test
    public void testIsModelComplete_false_samePushPullPorts() {
        if (fileBom != null) {
            fileBom.setProxyPullPort(PUSH_PORT_TEST);
            assertFalse(fileBom.isModelValid());
        } else {
            logback.warn("Cannot perform the test because no EV3 device is on the local network");
        }
    }
}
