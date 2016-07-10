package orwell.tank;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.hardware.sensor.RFIDSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.communication.RobotMessageBroker;
import orwell.tank.messaging.EnumConnectionState;

import java.io.IOException;

public class RemoteRobot extends Thread {
    private final static Logger logback = LoggerFactory.getLogger(RemoteRobot.class);

    private static EV3LargeRegulatedMotor C = new EV3LargeRegulatedMotor(MotorPort.C);
    private static EV3LargeRegulatedMotor D = new EV3LargeRegulatedMotor(MotorPort.D);
    private static RFIDSensor rfidSensor = new RFIDSensor(SensorPort.S2);
    private static NXTUltrasonicSensor usSensor = new NXTUltrasonicSensor(SensorPort.S3);
    private static RemoteRobot remoteRobot;
    private final RobotMessageBroker robotMessageBroker;
    private EnumConnectionState connectionState = EnumConnectionState.NOT_CONNECTED;
    private int flag = 1;

    public RemoteRobot(String serverIpAddress, int pushPort, int pullPort) {
        robotMessageBroker = new RobotMessageBroker(serverIpAddress, pushPort, pullPort);
        Sound.setVolume(50);
        Button.ESCAPE.addKeyListener(new EscapeListener());
    }

    public static void main(String[] args) throws IOException {
        remoteRobot = new RemoteRobot("192.168.0.18", 10001, 10000);
        remoteRobot.start();

    }

    public EnumConnectionState connect() {
        robotMessageBroker.connect();
        setConnectionState(EnumConnectionState.CONNECTED);
        Sound.beepSequenceUp();
        return getConnectionState();
    }

    public EnumConnectionState disconnect() {
        if (EnumConnectionState.CONNECTED == getConnectionState()) {
            robotMessageBroker.disconnect();
        }
        setConnectionState(EnumConnectionState.NOT_CONNECTED);
        Sound.beepSequence();
        return getConnectionState();
    }

//    public void robotAction(EnumCommand command) {
//        switch (command) {
//            case LocalClient.BACKWARD:
//                C.backward();
//                D.backward();
//                break;
//            case LocalClient.FORWARD:
//                C.forward();
//                D.forward();
//                break;
//            case LocalClient.RIGHT:
//                C.rotateTo(-10, true);
//                D.rotateTo(10);
//                break;
//            case LocalClient.LEFT:
//                C.rotateTo(10, true);
//                D.rotateTo(-10);
//                break;
//        }
//    }

    public void run() {
        System.out.println("CLIENT CONNECT");
        remoteRobot.connect();
        try {


            while (isRobotRunning()) {
                remoteRobot.waitForNewMessage();
            }


        } catch (Exception e) {
            logback.error("Exception during RemoteRobot run: " + e.getMessage());
        }
        C.stop();
        D.stop();
        remoteRobot.disconnect();
        Thread.yield();
    }

    private void waitForNewMessage() {
        String msg = robotMessageBroker.receiveNewMessage();

        Sound.beep();

        if(flag == 1) {
            C.forward();
            D.backward();
            flag++;
        }
        if(flag == 2){
            C.backward();
            D.forward();
            flag++;
        }
        if(flag == 3){
            C.stop();
            D.stop();
            flag = 1;
        }
    }

    private boolean isRobotRunning() {
        return remoteRobot.getConnectionState() == EnumConnectionState.CONNECTED;
    }

    public EnumConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(EnumConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    private class EscapeListener implements KeyListener {

        public void keyPressed(Key k) {
            remoteRobot.disconnect();
        }

        public void keyReleased(Key k) {
        }
    }
}