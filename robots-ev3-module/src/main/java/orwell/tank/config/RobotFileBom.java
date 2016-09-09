package orwell.tank.config;

import lejos.hardware.port.Port;

/**
 * Created by Michaël Ludmann on 07/09/16.
 */
public class RobotFileBom {

    private Port leftMortPort;
    private Port rightMotorPort;
    private boolean isLeftMotorInverted;
    private boolean isRightMotorInverted;
    private Port rfidSensorPort;
    private Port usSensorPort;
    private int proxyPushPort;
    private int proxyPullPort;
    private String proxyIp;
    private int sensorMessageDelay;
    private int volume;

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

    public int getSensorMessageDelay() {
        return sensorMessageDelay;
    }

    public void setSensorMessageDelay(int sensorMessageDelay) {
        this.sensorMessageDelay = sensorMessageDelay;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
