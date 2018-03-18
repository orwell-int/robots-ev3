package orwell.tank.hardware;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 28/08/16.
 */
public class Tracks {
    private static final Logger logback = LoggerFactory.getLogger(Tracks.class);
    private boolean isLeftInverted;
    private boolean isRightInverted;
    private final EV3LargeRegulatedMotor rightMotor;
    private final EV3LargeRegulatedMotor leftMotor;

    public Tracks(Port leftMotorPort, boolean isLeftMotorInverted,
                  Port rightMotorPort, boolean isRightMotorInverted) {
        if (leftMotorPort == rightMotorPort) {
            logback.error("MotorPorts should be different");
        }
        leftMotor = new EV3LargeRegulatedMotor(leftMotorPort);
        rightMotor = new EV3LargeRegulatedMotor(rightMotorPort);
        isLeftInverted = isLeftMotorInverted;
        isRightInverted = isRightMotorInverted;
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
        if (power > 0)
            motor.forward();
        else if (power < 0)
            motor.backward();
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
