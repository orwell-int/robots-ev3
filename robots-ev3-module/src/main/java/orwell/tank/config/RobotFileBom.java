package orwell.tank.config;

import lejos.hardware.port.Port;

public class RobotFileBom {

    private static final int DEFAULT_BROADCAST_PORT = 9081;
    private static final int DEFAULT_BROADCAST_TIMEOUT = 1000;
    private Port leftMortPort;
    private Port rightMotorPort;
    private boolean isLeftMotorInverted;
    private boolean isRightMotorInverted;
    private Port rfidSensorPort;
    private Port usSensorPort;
    private int sensorMessageDelayMs = -1;
    private int globalVolume = -1;
    private Port colourSensorPort;
    private int endGameVolume = -1;
    private String soundVictoryFilepath;
    private String soundDefeatFilepath;
    private String soundDrawFilepath;
    private int broadcastPort = DEFAULT_BROADCAST_PORT;
    private int broadcastTimeout = DEFAULT_BROADCAST_TIMEOUT;

    public Port getLeftMotorPort() {
        return leftMortPort;
    }

    public void setLeftMotorPort(Port leftMortPort) {
        this.leftMortPort = leftMortPort;
    }

    public Port getRightMotorPort() {
        return rightMotorPort;
    }

    public void setRightMotorPort(Port rightMotorPort) {
        this.rightMotorPort = rightMotorPort;
    }

    public boolean isLeftMotorInverted() {
        return isLeftMotorInverted;
    }

    public void setIsLeftMotorInverted(boolean isLeftMotorInverted) {
        this.isLeftMotorInverted = isLeftMotorInverted;
    }

    public boolean isRightMotorInverted() {
        return isRightMotorInverted;
    }

    public void setIsRightMotorInverted(boolean isRightMotorInverted) {
        this.isRightMotorInverted = isRightMotorInverted;
    }

    public Port getRfidSensorPort() {
        return rfidSensorPort;
    }

    public void setRfidSensorPort(Port rfidSensorPort) {
        this.rfidSensorPort = rfidSensorPort;
    }

    public Port getUsSensorPort() {
        return usSensorPort;
    }

    public void setUsSensorPort(Port usSensorPort) {
        this.usSensorPort = usSensorPort;
    }

    public int getSensorMessageDelayMs() {
        return sensorMessageDelayMs;
    }

    public void setSensorMessageDelayMs(int sensorMessageDelayMs) {
        this.sensorMessageDelayMs = sensorMessageDelayMs;
    }

    public int getGlobalVolume() {
        return globalVolume;
    }

    public void setGlobalVolume(int globalVolume) {
        this.globalVolume = globalVolume;
    }

    public boolean isModelValid() {
        return leftMortPort != null &&
               rightMotorPort != null &&
               globalVolume != -1 &&
               sensorMessageDelayMs != -1 &&
               leftMortPort != rightMotorPort &&
               areSensorsPortsDifferent() &&
               endGameVolume != -1 &&
               soundDefeatFilepath != null &&
               soundDrawFilepath != null &&
               soundVictoryFilepath != null;
    }

    private boolean areSensorsPortsDifferent() {
        return (rfidSensorPort == null && usSensorPort == null) || (rfidSensorPort != usSensorPort);
    }

    public Port getColourSensorPort() {
        return colourSensorPort;
    }

    public void setColourSensorPort(Port colourSensorPort) {
        this.colourSensorPort = colourSensorPort;
    }

    public String getSoundVictoryFilepath() {
        return soundVictoryFilepath;
    }

    public void setSoundVictoryFilepath(String soundVictoryFilepath) {
        this.soundVictoryFilepath = soundVictoryFilepath;
    }

    public String getSoundDefeatFilepath() {
        return soundDefeatFilepath;
    }

    public void setSoundDefeatFilepath(String soundDefeatFilepath) {
        this.soundDefeatFilepath = soundDefeatFilepath;
    }

    public String getSoundDrawFilepath() {
        return soundDrawFilepath;
    }

    public void setSoundDrawFilepath(String soundDrawFilepath) {
        this.soundDrawFilepath = soundDrawFilepath;
    }

    public int getEndGameVolume() {
        return endGameVolume;
    }

    public void setEndGameVolume(int endGameVolume) {
        this.endGameVolume = endGameVolume;
    }

    public int getBroadcastPort() {
        return broadcastPort;
    }

    public void setBroadcastPort(int broadcastPort) {
        if (broadcastPort > 0) {
            this.broadcastPort = broadcastPort;
        }
    }

    public int getBroadcastTimeout() {
        return broadcastTimeout;
    }

    public void setBroadcastTimeout(int broadcastTimeout) {
        if (broadcastTimeout > 0) {
            this.broadcastTimeout = broadcastTimeout;
        }
    }
}