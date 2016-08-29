package orwell.tank;

import lejos.hardware.*;
import lejos.hardware.port.I2CPort;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.internal.ev3.EV3LED;
import lejos.mf.common.UnitMessage;
import lejos.mf.common.exception.UnitMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.actions.IInputAction;
import orwell.tank.actions.StopTank;
import orwell.tank.communication.RobotMessageBroker;
import orwell.tank.hardware.ISensor;
import orwell.tank.hardware.RfidFlagSensor;
import orwell.tank.hardware.SensorsListener;
import orwell.tank.hardware.Tracks;
import orwell.tank.messaging.EnumConnectionState;
import orwell.tank.messaging.UnitMessageDecoderFactory;

import java.io.IOException;
import java.util.ArrayList;

public class RemoteRobot extends Thread {
    private final static Logger logback = LoggerFactory.getLogger(RemoteRobot.class);
    private final static String IP_PROXY = "192.168.0.16";
    private static RfidFlagSensor rfidSensor;
    private static NXTUltrasonicSensor usSensor;
    private static RemoteRobot remoteRobot;
    private final RobotMessageBroker robotMessageBroker;
    private SensorsListener sensorsListener;
    private EnumConnectionState connectionState = EnumConnectionState.NOT_CONNECTED;
    private boolean isListening = false;
    private EV3LED led;
    private Tracks tracks;
    private boolean ready = false;

    public RemoteRobot(String serverIpAddress, int pushPort, int pullPort) {
        robotMessageBroker = new RobotMessageBroker(serverIpAddress, pushPort, pullPort);
        initHardware();
        Sound.twoBeeps();
        Button.ESCAPE.addKeyListener(new EscapeListener());
    }

    public static void main(String[] args) throws IOException {
        remoteRobot = new RemoteRobot(IP_PROXY, 10001, 10000);
        if (remoteRobot.isReady()) {
            remoteRobot.start();
        }
    }

    private void initHardware() {
        led = new EV3LED();
        Sound.setVolume(50);
        sensorsListener = new SensorsListener(robotMessageBroker);
        try {
            initTracks(MotorPort.C, MotorPort.D);
            initRfid(SensorPort.S1);
            initUs(SensorPort.S4);

            initSensorListener();
            ready = true;
        } catch (DeviceException e) {
            logback.error(e.getMessage());
            dispose();
        }
    }

    private void initTracks(Port leftMotor, Port rightMotor) {
        tracks = new Tracks(leftMotor, rightMotor);
        logback.info("Tracks init Ok");
    }

    private void initRfid(Port port) {
        I2CPort i2cPort = port.open(I2CPort.class);
        i2cPort.setType(I2CPort.TYPE_LOWSPEED_9V);
        rfidSensor = new RfidFlagSensor(i2cPort);
        logback.info("Rfid init Ok");
    }

    private void initUs(Port usPort) {
        usSensor = new NXTUltrasonicSensor(usPort);
        logback.info("US init Ok");
    }

    private void initSensorListener() {
        ArrayList<ISensor> sensors = new ArrayList<>();
        sensors.add(rfidSensor);
        sensorsListener.add(sensors);
        sensorsListener.startListenToSensors();
    }

    public void run() {
        logback.info("Start running RemoteRobot");
        try {
            connect();
            startReceivingMessagesLoop();
        } catch (Exception e) {
            logback.error(e.getMessage());
        }
        dispose();
        Thread.yield();
    }

    public EnumConnectionState connect() {
        robotMessageBroker.connect();
        setConnectionState(EnumConnectionState.CONNECTED);
        Sound.beepSequenceUp();
        return getConnectionState();
    }

    private void startReceivingMessagesLoop() {
        try {
            isListening = true;
            led.setPattern(EV3LED.COLOR_GREEN, EV3LED.PATTERN_HEARTBEAT);

            while (isRobotListeningAndConnected()) {
                remoteRobot.listenForNewMessage();
                Thread.sleep(1);
            }
            isListening = false;
        } catch (Exception e) {
            logback.error("Exception during RemoteRobot run: " + e.getMessage());
        }
    }

    private boolean isRobotListeningAndConnected() {
        return isListening && isConnected();
    }

    private boolean isConnected() {
        return remoteRobot.getConnectionState() == EnumConnectionState.CONNECTED;
    }

    private void listenForNewMessage() {
        try {
            UnitMessage unitMessage = robotMessageBroker.receivedNewMessage();
            actOnMessage(unitMessage);
        } catch (UnitMessageException e) {
            logback.debug("Unit Message Exception: " + e.getMessage());
        }
    }

    private void actOnMessage(UnitMessage unitMessage) {
        try {
            IInputAction inputAction = UnitMessageDecoderFactory.parseFrom(unitMessage);
            inputAction.performAction(this);
        } catch (Exception e) {
            logback.error(e.getMessage());
        }
    }

    public void dispose() {
        Sound.buzz();
        stopRobotAndDisconnect();
        closeHardware();
        ready = false;
    }

    public void stopRobotAndDisconnect() {
        stopTank();
        led.setPattern(EV3LED.COLOR_NONE);
        sensorsListener.stopListenToSensors();
        disconnect();
        logback.info("Robot is stopped and " + getConnectionState());
    }

    private void stopTank() {
        StopTank stopTank = new StopTank();
        stopTank.performAction(this);
    }

    public EnumConnectionState disconnect() {
        if (EnumConnectionState.CONNECTED == getConnectionState()) {
            robotMessageBroker.disconnect();
        }
        setConnectionState(EnumConnectionState.NOT_CONNECTED);
        Sound.beepSequence();
        return getConnectionState();
    }

    public EnumConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(EnumConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    private void closeHardware() {
        if (tracks != null)
            tracks.close();
        if (rfidSensor != null)
            rfidSensor.close();
        if (usSensor != null)
            usSensor.close();
    }

    public Tracks getTracks() {
        return tracks;
    }

    public boolean isReady() {
        return ready;
    }

    private class EscapeListener implements KeyListener {

        public void keyPressed(Key k) {
            isListening = false;
        }

        public void keyReleased(Key k) {
            isListening = false;
        }
    }
}