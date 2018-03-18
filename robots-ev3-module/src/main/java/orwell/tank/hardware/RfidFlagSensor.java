package orwell.tank.hardware;

import lejos.hardware.port.I2CPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.RFIDSensor;
import lejos.mf.common.UnitMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

public class RfidFlagSensor implements ISensor<Long> {
    private static final Logger logback = LoggerFactory.getLogger(RFIDSensor.class);
    private static final int VALUE_READ_COUNT_THRESHOLD = 999;
    private static final long SLEEP_BETWEEN_RESTART_MS = 315;
    private static final long READ_VALUE_INTERVAL = 250;
    private final Port port;
    private final SensorMeasure<Long> sensorMeasure;
    private int valueReadCount;
    private RFIDSensor rfidSensor;
    private I2CPort i2cPort;

    /**
     * Create a class to provide access to the device. Perform device
     * initialization.
     *
     * @param port The sensor port to use for this device.
     */
    public RfidFlagSensor(Port port) {
        this.port = port;
        initRfid(port);
        sensorMeasure = new SensorMeasure<>();
    }

    private void initRfid(Port port) {
        i2cPort = port.open(I2CPort.class);
        i2cPort.setType(I2CPort.TYPE_LOWSPEED_9V);
        rfidSensor = new RFIDSensor(i2cPort);
        rfidSensor.getSerialNo(); // might be required to avoid a fatal crash
        logback.info("RFID sensor initialized");
    }

    @Override
    public Long get() {
        return sensorMeasure.get();
    }

    @Override
    public void readValue() {
        if (valueReadCount >= VALUE_READ_COUNT_THRESHOLD) {
            close();
            try {
                sleep(SLEEP_BETWEEN_RESTART_MS);
            } catch (InterruptedException e) {
                logback.error("Exception while sleeping " + SLEEP_BETWEEN_RESTART_MS + "ms in thread", e);
            }
            initRfid(port);
        }
        sensorMeasure.set(rfidSensor.readTransponderAsLong(true)); // this also makes the current thread sleep
        valueReadCount++;
    }

    @Override
    public long getReadValueInterval() {
        return READ_VALUE_INTERVAL;
    }

    @Override
    public UnitMessageType getType() {
        return UnitMessageType.Rfid;
    }

    @Override
    public void close() {
        rfidSensor.stop();
        rfidSensor.close();
        i2cPort.close();
        logback.info("RFID sensor closed");
    }
}
