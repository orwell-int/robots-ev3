package orwell.tank;

import lejos.hardware.DeviceException;
import lejos.hardware.Sound;
import lejos.hardware.port.Port;
import lejos.internal.ev3.EV3LED;
import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;
import lejos.mf.common.exception.UnitMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQException;
import orwell.tank.actions.IInputAction;
import orwell.tank.actions.StopTank;
import orwell.tank.communication.RobotMessageBroker;
import orwell.tank.communication.UdpProxyFinder;
import orwell.tank.communication.UdpProxyFinderFactory;
import orwell.tank.config.RobotColourConfigFileBom;
import orwell.tank.config.RobotFileBom;
import orwell.tank.exception.FileBomException;
import orwell.tank.exception.ParseIniException;
import orwell.tank.hardware.*;
import orwell.tank.messaging.EnumConnectionState;
import orwell.tank.messaging.UnitMessageDecoderFactory;
import utils.Cli;
import utils.IniFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RemoteRobot extends Thread {
    private final static Logger logback = LoggerFactory.getLogger(RemoteRobot.class);
    private static final long THREAD_SLEEP_BETWEEN_MSG_MS = 3;
    private final RobotFileBom robotConfig;
    private RobotMessageBroker robotMessageBroker;
    private ArrayList<ThreadedSensor> threadedSensorList = new ArrayList<>();
    private ArrayList<UnitMessage> sensorsMessages = new ArrayList<>();
    private EnumConnectionState connectionState = EnumConnectionState.NOT_CONNECTED;
    private EV3LED led;
    private Tracks tracks;
    private boolean ready = false;
    private long lastSensorMessageTime = System.currentTimeMillis();
    private RobotColourConfigFileBom colourConfig;
    private SimpleKeyListener simpleKeyListener = new SimpleKeyListener();

    public RemoteRobot(RobotFileBom robotConfig, RobotColourConfigFileBom colourConfig) {
        this.robotConfig = robotConfig;
        this.colourConfig = colourConfig;
        initHardware();
    }

    public static void main(String[] args) throws IOException {
        final IniFiles iniFiles = new Cli(args).parse();
        if (iniFiles == null || iniFiles.isPartiallyEmpty()) {
            logback.warn("Command Line Interface did not manage to extract a ini file orwell.tank.config. Exiting now.");
            System.exit(0);
        }
        try {
            final RobotFileBom robotBom = iniFiles.robotIniFile.parse();
            final RobotColourConfigFileBom colourConfigFileBom = iniFiles.colourConfigIniFile.parse();
            final RemoteRobot remoteRobot = new RemoteRobot(robotBom, colourConfigFileBom);
            if (remoteRobot.isReady()) {
                remoteRobot.start();
            }
        } catch (ParseIniException e) {
            logback.error("Failed to parse the ini file. Exiting now");
        } catch (FileBomException e) {
            logback.error(e.getMessage());
        }
    }

    private void initHardware() {
        led = new EV3LED();
        Sound.setVolume(robotConfig.getGlobalVolume());
        try {
            initTracks(robotConfig.getLeftMotorPort(), robotConfig.isLeftMotorInverted(),
                    robotConfig.getRightMotorPort(), robotConfig.isRightMotorInverted());
            initColor(robotConfig.getColorSensorPort());
            initUs(robotConfig.getUsSensorPort());
            initBattery();

            ready = true;
            Sound.twoBeeps();
        } catch (DeviceException e) {
            logback.error(e.getMessage());
            dispose();
        }
    }

    private void initBattery() {
        ThreadedSensor<String> batteryInfoThreaded = new ThreadedSensor<>(new BatteryInfo());
        threadedSensorList.add(batteryInfoThreaded);
        batteryInfoThreaded.start();
        logback.info("Battery info init Ok");
    }

    private void initColor(Port colorSensorPort) {
        if (colorSensorPort == null) {
            logback.info("No Color Sensor configured");
            return;
        }
        ThreadedSensor<Integer> colorThreadedSensor = new ThreadedSensor<>(
                new ColourSensor(colorSensorPort, colourConfig));
        threadedSensorList.add(colorThreadedSensor);
        colorThreadedSensor.start();
        logback.info("Color init Ok");
    }

    private void initTracks(Port leftMotor, boolean isLeftMotorInverted,
                            Port rightMotor, boolean isRightMotorInverted) {
        logback.debug("Init tracks: [" + leftMotor.getName() + "] inverted: " + isLeftMotorInverted +
                " [" + rightMotor.getName() + "] inverted: " + isRightMotorInverted);
        tracks = new Tracks(leftMotor, isLeftMotorInverted,
                rightMotor, isRightMotorInverted);
        logback.info("Tracks init Ok");
    }

    private void initUs(Port usPort) {
        if (usPort == null) {
            logback.info("No US Sensor configured");
            return;
        }
        ThreadedSensor<Integer> usThreadedSensor = new ThreadedSensor<>(new UsRadarSensor(usPort));
        threadedSensorList.add(usThreadedSensor);
        usThreadedSensor.start();
        logback.info("US init Ok");
    }

    public void run() {
        logback.info("Start running RemoteRobot");
        try {
            while (!simpleKeyListener.wasKeyPressed()) {
                createRobotMessageBrokerFromUdpBroadcast();
                try {
                    connect();
                    startReceivingMessagesLoop();
                } catch (ZMQException e) {
                    logback.warn("ZMQ error (clear connection): " + e);
                    robotMessageBroker = null;
                }
            }
        } catch (Exception e) {
            logback.error(e.getMessage());
        }
        dispose();
        Thread.yield();
    }

    public void createRobotMessageBrokerFromUdpBroadcast() {
        if (null == robotMessageBroker) {
            UdpProxyFinder udpProxyFinder = UdpProxyFinderFactory.fromParameters(
                    robotConfig.getBroadcastPort(),
                    robotConfig.getBroadcastTimeout(),
                    simpleKeyListener);
            udpProxyFinder.broadcastAndGetServerAddress();
            robotMessageBroker = new RobotMessageBroker(
                    udpProxyFinder.getPushAddress(), udpProxyFinder.getPullAddress());
        }
    }

    public void connect() {
        robotMessageBroker.connect();
    }

    private void startReceivingMessagesLoop() {
        try {
            establishFirstConnection();
            while (isRobotListeningAndConnected()) {
                listenForNewMessage();
                sendMessageOnSensorUpdate();
                sleepBetweenMessages();
            }
        } catch (Exception e) {
            logback.error("Exception during RemoteRobot run: " + e.getStackTrace());
        }
    }

    private void establishFirstConnection() {
        while (isRobotListeningAndNotConnected()) {
            listenForNewMessage();
            sleepBetweenMessages();
        }
    }

    private void sleepBetweenMessages() {
        try {
            sleep(THREAD_SLEEP_BETWEEN_MSG_MS);
        } catch (InterruptedException e) {
            logback.error(e.getMessage());
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
        return lastSensorMessageTime + robotConfig.getSensorMessageDelayMs() <= System.currentTimeMillis();
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

    public void sendConnectionAckMessage() {
        robotMessageBroker.sendMessage(new UnitMessage(UnitMessageType.Connection, "connected"));
    }

    public void sendConnectionCloseMessage() {
        robotMessageBroker.sendMessage(new UnitMessage(UnitMessageType.Connection, "close"));
    }

    private boolean isRobotListeningAndNotConnected() {
        return !simpleKeyListener.wasKeyPressed() && !isConnected();
    }

    private boolean isRobotListeningAndConnected() {
        return !simpleKeyListener.wasKeyPressed() && isConnected();
    }

    private boolean isConnected() {
        return getConnectionState() == EnumConnectionState.CONNECTED;
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
        Sound.playSample(new File(robotConfig.getSoundDrawFilepath()), robotConfig.getEndGameVolume());

        stopRobotAndDisconnect();
        closeHardware();
        ready = false;
    }

    public void stopRobotAndDisconnect() {
        stopTank();
        disconnect();
        logback.info("Robot is stopped and " + getConnectionState());
        led.setPattern(EV3LED.COLOR_NONE);

    }

    private void stopTank() {
        StopTank stopTank = new StopTank();
        stopTank.performAction(this);
    }

    public EnumConnectionState disconnect() {
        if (EnumConnectionState.CONNECTED == getConnectionState()) {
            setConnectionState(EnumConnectionState.NOT_CONNECTED);
            robotMessageBroker.disconnect();
        }
        return getConnectionState();
    }

    public EnumConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(EnumConnectionState connectionState) {
        this.connectionState = connectionState;
        logback.info("Tank changed its connection status to " + connectionState);
        if (connectionState == EnumConnectionState.NOT_CONNECTED) {
            sendConnectionCloseMessage();
            Sound.beepSequence();
            led.setPattern(EV3LED.COLOR_RED, EV3LED.PATTERN_BLINK);
        }
        if (connectionState == EnumConnectionState.CONNECTED) {
            Sound.beepSequenceUp();
            led.setPattern(EV3LED.COLOR_GREEN, EV3LED.PATTERN_HEARTBEAT);
        }
    }

    private void closeHardware() {
        if (tracks != null)
            tracks.close();
        for (ThreadedSensor sensor : threadedSensorList) {
            sensor.stop();
            sensor.close();
        }
    }

    public Tracks getTracks() {
        return tracks;
    }

    public boolean isReady() {
        return ready;
    }

    public void handleVictory() {
        stopTank();
        logback.info("I WON! \\o/");
        Sound.playSample(new File(robotConfig.getSoundVictoryFilepath()), robotConfig.getEndGameVolume());
    }

    public void handleDefeat() {
        stopTank();
        logback.info("I LOST... :(");
        Sound.playSample(new File(robotConfig.getSoundDefeatFilepath()), robotConfig.getEndGameVolume());
    }

    public void handleDraw() {
        stopTank();
        logback.info("Nobody won this time :|");
        Sound.playSample(new File(robotConfig.getSoundDrawFilepath()), robotConfig.getEndGameVolume());
    }

    public void handleWait() {
        stopTank();
        logback.info("Waiting for the game to start.");
    }
}