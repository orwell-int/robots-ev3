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
import orwell.tank.hardware.RfidFlagSensor;
import orwell.tank.hardware.ThreadedSensor;
import orwell.tank.hardware.Tracks;
import orwell.tank.messaging.EnumConnectionState;
import orwell.tank.messaging.UnitMessageDecoderFactory;

import java.io.IOException;
import java.util.ArrayList;

public class RemoteRobot extends Thread {
    private final static Logger logback = LoggerFactory.getLogger(RemoteRobot.class);
    private final static String IP_PROXY = "192.168.0.16";
    private static final int PUSH_PORT = 10001;
    private static final int PULL_PORT = 10000;
    private static final Port LEFT_TRACK_PORT = MotorPort.C;
    private static final Port RIGHT_TRACK_PORT = MotorPort.D;
    private static final Port RFID_PORT = SensorPort.S1;
    private static final Port US_PORT = SensorPort.S4;
    private static final int VOLUME_PERCENT = 10;
    private static final long SENSOR_MESSAGE_DELAY = 50;
    private static NXTUltrasonicSensor usSensor;
    private static RemoteRobot remoteRobot;
    private final RobotMessageBroker robotMessageBroker;
    private ArrayList<ThreadedSensor> threadedSensorList = new ArrayList<>();
    private ArrayList<UnitMessage> sensorsMessages = new ArrayList<>();
    private EnumConnectionState connectionState = EnumConnectionState.NOT_CONNECTED;
    private boolean isListening = false;
    private EV3LED led;
    private Tracks tracks;
    private boolean ready = false;
    private long lastSensorMessageTime = System.currentTimeMillis();

    public RemoteRobot(String serverIpAddress, int pushPort, int pullPort) {
        robotMessageBroker = new RobotMessageBroker(serverIpAddress, pushPort, pullPort);
        initHardware();
        Sound.twoBeeps();
        Button.ESCAPE.addKeyListener(new EscapeListener());
    }

    public static void main(String[] args) throws IOException {
        remoteRobot = new RemoteRobot(IP_PROXY, PUSH_PORT, PULL_PORT);
        if (remoteRobot.isReady()) {
            remoteRobot.start();
        }
    }

    private void initHardware() {
        led = new EV3LED();
        Sound.setVolume(VOLUME_PERCENT);
        try {
            initTracks(LEFT_TRACK_PORT, RIGHT_TRACK_PORT);
            initRfid(RFID_PORT);
            initUs(US_PORT);

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
        ThreadedSensor<Long> rfidThreadedSensor = new ThreadedSensor<>(new RfidFlagSensor(i2cPort));
        threadedSensorList.add(rfidThreadedSensor);
        rfidThreadedSensor.start();
        logback.info("Rfid init Ok");
    }

    private void initUs(Port usPort) {
        usSensor = new NXTUltrasonicSensor(usPort);
        logback.info("US init Ok");
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
                remoteRobot.sendMessageOnSensorUpdate();
                Thread.sleep(10);
            }
            isListening = false;
        } catch (Exception e) {
            logback.error("Exception during RemoteRobot run: " + e.getMessage());
        }
    }

    private void sendMessageOnSensorUpdate() {
        if (!shouldTrySendSensorMessage()) {
            return;
        }
        checkSensorUpdate();
        sendSensorMessage();
    }

    private boolean shouldTrySendSensorMessage() {
        return lastSensorMessageTime + SENSOR_MESSAGE_DELAY <= System.currentTimeMillis();
    }

    private void checkSensorUpdate() {
        for (ThreadedSensor sensor : threadedSensorList) {
            if (sensor.hasUpdate()) {
                sensorsMessages.add(new UnitMessage(sensor.getType(), sensor.get().toString()));
            }
        }
    }

    private void sendSensorMessage() {
        for (UnitMessage message : sensorsMessages) {
            robotMessageBroker.sendMessage(message);
        }
        sensorsMessages.clear();
        lastSensorMessageTime = System.currentTimeMillis();
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

    private void dispose() {
        Sound.buzz();
        stopRobotAndDisconnect();
        closeHardware();
        ready = false;
    }

    public void stopRobotAndDisconnect() {
        stopTank();
        led.setPattern(EV3LED.COLOR_NONE);
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
        for (ThreadedSensor sensor : threadedSensorList) {
            sensor.close();
        }
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