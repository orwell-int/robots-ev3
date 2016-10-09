package orwell.tank.hardware;

import lejos.hardware.port.I2CException;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.mf.common.UnitMessageType;
import lejos.robotics.SampleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 18/09/16.
 */
public class UsRadarSensor extends NXTUltrasonicSensor implements ISensor<Float> {
    private final static Logger logback = LoggerFactory.getLogger(UsRadarSensor.class);
    private static final long READ_VALUE_INTERVAL_MS = 50;
    private SensorMeasure<Float> sensorMeasure;
    private int i2cErrorCount;

    public UsRadarSensor(Port port) {
        super(port);
        setRetryCount(5);
        i2cErrorCount = 0;
        sensorMeasure = new SensorMeasure<>();
    }

    @Override
    public Float get() {
        return sensorMeasure.get();
    }

    @Override
    public void readValue() {
        SampleProvider sampleProvider = getDistanceMode();
        float samples[] = new float[sampleProvider.sampleSize()];
        try {
            sampleProvider.fetchSample(samples, 0);
            sensorMeasure.set(samples[0]);
        } catch (I2CException e) {
            i2cErrorCount++;
            logback.error("US I2C #" + i2cErrorCount + " read error: " + e.getMessage());
        }
    }

    @Override
    public long getReadValueInterval() {
        return READ_VALUE_INTERVAL_MS;
    }

    @Override
    public UnitMessageType getType() {
        return UnitMessageType.UltraSound;
    }
}
