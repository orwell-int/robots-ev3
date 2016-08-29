package orwell.tank.hardware;

import lejos.hardware.port.I2CPort;
import lejos.hardware.sensor.RFIDSensor;
import lejos.mf.common.UnitMessageType;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Michaël Ludmann on 6/10/15.
 */
public class RfidFlagSensor extends RFIDSensor implements ISensor {
    private final static Logger logback = LoggerFactory.getLogger(RFIDSensor.class);

    private static final int READ_RATE_MS = 50;
    private final Timer timer;
    private final ArrayList<ISensorListener> sensorListenerList;
    private volatile long rfidValue;

    /**
     * Create a class to provide access to the device. Perform device
     * initialization.
     *
     * @param port The sensor port to use for this device.
     */
    public RfidFlagSensor(I2CPort port) {
        super(port);
        sensorListenerList = new ArrayList<>();
        // Schedule a sensor reading task at a fixed period
        timer = new Timer(READ_RATE_MS, new SensorReadService());
    }

    @Override
    public void addSensorListener(ISensorListener sensorListener) {
        sensorListenerList.add(sensorListener);
    }

    @Override
    public void startListen() {
        timer.start();
    }

    @Override
    public void stopListen() {
        timer.stop();
    }

    private void setRfidValue(long rfidValue) {
        if (this.rfidValue == rfidValue)
            return;
        this.rfidValue = rfidValue;
        for (ISensorListener listener : sensorListenerList) {
            listener.receivedNewValue(UnitMessageType.Rfid, Long.toString(rfidValue));
        }
    }

    private class SensorReadService implements TimerListener {
        @Override
        public void timedOut() {
            setRfidValue(readTransponderAsLong(true));
        }
    }
}
