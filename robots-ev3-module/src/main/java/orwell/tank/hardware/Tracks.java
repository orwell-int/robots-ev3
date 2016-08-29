package orwell.tank.hardware;

import lejos.hardware.DeviceException;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 28/08/16.
 */
public class Tracks {
    private final static Logger logback = LoggerFactory.getLogger(Tracks.class);
    private static boolean isLeftInverted = false;
    private static boolean isRightInverted = false;
    private EV3LargeRegulatedMotor rightMotor;
    private EV3LargeRegulatedMotor leftMotor;

    public Tracks(Port leftMotorPort, Port rightMotorPort) {
        if (leftMotorPort == rightMotorPort) {
            logback.error("MotorPorts should be different");
        }
        try {
            this.leftMotor = new EV3LargeRegulatedMotor(leftMotorPort);
        } catch (DeviceException e) {

        }
        this.rightMotor = new EV3LargeRegulatedMotor(rightMotorPort);
    }

    public void stop() {
        leftMotor.stop(true);
        rightMotor.stop(true);
    }

    public void setPower(double powerLeft, double powerRight) {
        synchronizeMotors();
        rightMotor.startSynchronization();
        setPowerToMotor(leftMotor, isLeftInverted ? -powerLeft : powerLeft);
        setPowerToMotor(rightMotor, isRightInverted ? -powerRight : powerRight);
        rightMotor.endSynchronization();
    }

    private void setPowerToMotor(EV3LargeRegulatedMotor motor, double power) {
        motor.setSpeed((float) (motor.getMaxSpeed() * power));
        if (0 < power)
            motor.backward();
        else if (0 > power)
            motor.forward();
        else
            motor.flt();
    }

    private void synchronizeMotors() {
        rightMotor.synchronizeWith(new RegulatedMotor[]{leftMotor});
    }

    public void close() {
        leftMotor.close();
        rightMotor.close();
    }
}
