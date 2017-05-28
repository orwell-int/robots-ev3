package orwell.tank.hardware;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.mf.common.UnitMessageType;
import lejos.robotics.SampleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsRadarSensor extends EV3UltrasonicSensor implements ISensor<Integer> {
    public static final int METER_TO_CM_FACTOR = 100;
    private static final long READ_VALUE_INTERVAL_MS = 100;
    private SensorMeasure<Integer> sensorMeasure;

    public UsRadarSensor(Port port) {
        super(port);
        sensorMeasure = new SensorMeasure<>();
    }

    @Override
    public Integer get() {
        return sensorMeasure.get();
    }

    @Override
    public void readValue() {
        SampleProvider sampleProvider = getDistanceMode();
        float samples[] = new float[sampleProvider.sampleSize()];
        sampleProvider.fetchSample(samples, 0);
        float value = samples[0];
        int valueInt;
        if (value == Float.MAX_VALUE) {
            valueInt = Integer.MAX_VALUE;
        } else {
            valueInt = Math.round(value * METER_TO_CM_FACTOR);
        }
        sensorMeasure.set(valueInt);
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
