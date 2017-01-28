package orwell.tank.hardware;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.mf.common.UnitMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.hardware.Colours.*;

/**
 * Created by Michaël Ludmann on 20/11/16.
 */
public class ColourSensor implements ISensor<Integer> {
    private final static Logger logback = LoggerFactory.getLogger(ColourSensor.class);
    private EV3ColorSensor colorSensor;
    private SensorMeasure<Integer> sensorMeasure;
    private static final long READ_VALUE_INTERVAL = 1;
    private ColourMap colourMap = new ColourMap();
    private static final float sigmaFactor = 3.5f;
    private static final int WINDOW_SIZE = 10;
    private SlidingWindow slidingWindow = new SlidingWindow(WINDOW_SIZE);

    public ColourSensor(Port port) {
        initColorSensor(port);
        sensorMeasure = new SensorMeasure<>();
        colourMap.addColour(
                EnumColours.RED,
                new ColourMatcher(
                        new RgbColour(0.136712374f, 0.015125331f, 0.008912356f),
                        new RgbColourSigma(0.00696957f, 0.001072834f, 0.000786368f),
                        sigmaFactor));
        colourMap.addColour(
                EnumColours.BLUE,
                new ColourMatcher(
                        new RgbColour(0.032485062f, 0.058520435f, 0.05356543f),
                        new RgbColourSigma(0.001230815f, 0.001799922f, 0.00144575f),
                        sigmaFactor));
        colourMap.addColour(
                EnumColours.GREEN,
                new ColourMatcher(
                        new RgbColour(0.059157122f, 0.11555687f, 0.020583311f),
                        new RgbColourSigma(0.0016912251f, 0.0029267482f, 7.812728E-4f),
                        sigmaFactor));
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
        RgbColour rgbColour = new RgbColour(samples[0], samples[1], samples[2]);
        colourMap.getColour(rgbColour);
//        ColorConverter colorConverter = new ColorConverter(samples);
//        sensorMeasure.set(colorConverter.getColor());
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
