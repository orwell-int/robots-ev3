package orwell.tank.hardware;

import lejos.hardware.port.I2CPort;
import lejos.hardware.sensor.RFIDSensor;
import lejos.mf.common.UnitMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 6/10/15.
 */
public class RfidFlagSensor extends RFIDSensor implements ISensor<Long> {
    private final static Logger logback = LoggerFactory.getLogger(RFIDSensor.class);
    private SensorMeasure<Long> sensorMeasure;

    /**
     * Create a class to provide access to the device. Perform device
     * initialization.
     *
     * @param port The sensor port to use for this device.
     */
    public RfidFlagSensor(I2CPort port) {
        super(port);
        sensorMeasure = new SensorMeasure<>();
    }

    @Override
    public Long get() {
        return sensorMeasure.get();
    }

    @Override
    public void readValue() {
        sensorMeasure.set(readTransponderAsLong(true)); // this also makes the current thread sleep
    }

    @Override
    public long getReadValueInterval() {
        return 0;
    }

    @Override
    public UnitMessageType getType() {
        return UnitMessageType.Rfid;
    }
}
