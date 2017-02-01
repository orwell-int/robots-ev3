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
    private static final long READ_VALUE_INTERVAL_MS = 1;
    private ColourMap colourMap = new ColourMap();
    private static final float sigmaFactor = 5f;
    private static final int WINDOW_SIZE = 10;
    private SlidingWindow slidingWindow = new SlidingWindow(WINDOW_SIZE);

    public ColourSensor(Port port) {
        initColorSensor(port);
        sensorMeasure = new SensorMeasure<>();
        colourMap.addColour(
                EnumColours.RED,
                new ColourMatcher(
                        new RgbColour(0.151151f, 0.01643667f, 0.00948284f),
                        new RgbColourSigma(0.05888082f, 0.011222564f, 9.3079184E-3f),
                        sigmaFactor));
        colourMap.addColour(
                EnumColours.GREEN,
                new ColourMatcher(
                        new RgbColour(0.062760934f, 0.12154779f, 0.020936513f),
                        new RgbColourSigma(0.010610968f, 0.014321462f, 6.953949E-3f),
                        sigmaFactor));
        colourMap.addColour(
                EnumColours.BLUE,
                new ColourMatcher(
                        new RgbColour(0.033704247f, 0.0603916f, 0.05437072f),
                        new RgbColourSigma(8.384969E-3f, 8.9144957E-3f, 7.594952E-3f),
                        sigmaFactor));
        logback.debug("ColourSensor init done");
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
        slidingWindow.addColour(colourMap.getColour(rgbColour));
        EnumColours colour = slidingWindow.getMainColour();
        sensorMeasure.set(colour.ordinal());
        logback.debug("ReadValue of colourSensor = " + colour);
    }

    @Override
    public long getReadValueInterval() {
        return READ_VALUE_INTERVAL_MS;
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
