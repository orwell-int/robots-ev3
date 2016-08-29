package orwell.tank.hardware;

import lejos.mf.common.UnitMessage;
import lejos.mf.common.UnitMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.communication.RobotMessageBroker;

import java.util.ArrayList;

/**
 * Created by Michaël Ludmann on 29/08/16.
 */
public class SensorsListener implements ISensorListener {
    private final static Logger logback = LoggerFactory.getLogger(SensorsListener.class);

    private final RobotMessageBroker messageBroker;
    private ArrayList<ISensor> sensors = new ArrayList<>();

    public SensorsListener(RobotMessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    public void startListenToSensors() {
        for (ISensor sensor : sensors) {
            if (sensor == null)
                continue;
            sensor.addSensorListener(this);
            sensor.startListen();
        }
    }

    public void stopListenToSensors() {
        for (ISensor sensor : sensors) {
            sensor.stopListen();
        }
    }

    @Override
    public void receivedNewValue(UnitMessageType messageType, String newValue) {
        logback.debug("Received new value : " + messageType + " " + newValue);
        messageBroker.sendMessage(new UnitMessage(messageType, newValue));
    }

    @Override
    public void add(ArrayList<ISensor> sensors) {
        this.sensors.addAll(sensors);
    }
}
