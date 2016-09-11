package orwell.tank.config;

import lejos.hardware.port.Port;

/**
 * Created by Michaël Ludmann on 07/09/16.
 */
public class RobotFileBom {

    private Port leftMortPort = null;
    private Port rightMotorPort = null;
    private boolean isLeftMotorInverted = false;
    private boolean isRightMotorInverted = false;
    private Port rfidSensorPort = null;
    private Port usSensorPort = null;
    private int proxyPushPort = -1;
    private int proxyPullPort = -1;
    private String proxyIp = null;
    private int sensorMessageDelayMs = -1;
    private int volume = -1;

    public Port getLeftMotorPort() {
        return leftMortPort;
    }

    public void setLeftMortPort(Port leftMortPort) {
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

    public int getProxyPushPort() {
        return proxyPushPort;
    }

    public void setProxyPushPort(int proxyPushPort) {
        this.proxyPushPort = proxyPushPort;
    }

    public int getProxyPullPort() {
        return proxyPullPort;
    }

    public void setProxyPullPort(int proxyPullPort) {
        this.proxyPullPort = proxyPullPort;
    }

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public int getSensorMessageDelayMs() {
        return sensorMessageDelayMs;
    }

    public void setSensorMessageDelayMs(int sensorMessageDelayMs) {
        this.sensorMessageDelayMs = sensorMessageDelayMs;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isModelComplete() {
        return leftMortPort != null &&
                rightMotorPort != null &&
                rfidSensorPort != null &&
                usSensorPort != null &&
                proxyPushPort != -1 &&
                proxyPullPort != -1 &&
                proxyIp != null &&
                volume != -1 &&
                sensorMessageDelayMs != -1 &&
                leftMortPort != rightMotorPort &&
                rfidSensorPort != usSensorPort;
    }
}
