package orwell.tank.hardware;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.mf.common.UnitMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 20/11/16.
 */
public class ColorSensor implements ISensor<Integer> {
    private final static Logger logback = LoggerFactory.getLogger(ColorSensor.class);
    private EV3ColorSensor colorSensor;
    private SensorMeasure<Integer> sensorMeasure;
    private static final long READ_VALUE_INTERVAL = 5;

    public ColorSensor(Port port) {
        initColorSensor(port);
        sensorMeasure = new SensorMeasure<>();
    }

    private void initColorSensor(Port port) {
        colorSensor = new EV3ColorSensor(port);
    }

    @Override
    public Integer get() {
        return sensorMeasure.get();
    }

    @Override
    public void readValue() {
        SensorMode sensorMode = colorSensor.getRGBMode();
        float samples[] = new float[sensorMode.sampleSize()];
        sensorMode.fetchSample(samples, 0);
        ColorConverter colorConverter = new ColorConverter(samples);
        sensorMeasure.set(colorConverter.getColor());
    }

    @Override
    public long getReadValueInterval() {
        return READ_VALUE_INTERVAL;
    }

    @Override
    public void close() {
        colorSensor.close();
    }

    @Override
    public UnitMessageType getType() {
        return UnitMessageType.Colour;
    }
}
