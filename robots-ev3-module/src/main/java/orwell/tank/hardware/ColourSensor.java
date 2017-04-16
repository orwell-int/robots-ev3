package orwell.tank.hardware;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.mf.common.UnitMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.config.RobotColourConfigFileBom;
import orwell.tank.hardware.Colours.*;

public class ColourSensor implements ISensor<Integer> {
    private final static Logger logback = LoggerFactory.getLogger(ColourSensor.class);
    private EV3ColorSensor colorSensor;
    private SensorMeasure<Integer> sensorMeasure;
    private static final long READ_VALUE_INTERVAL_MS = 1;
    private ColourMap colourMap = new ColourMap();
    private static final int WINDOW_SIZE = 1;
    private static final Integer MIN_VALUE_FOR_MATCH = 1;
    private SlidingWindow slidingWindow = new SlidingWindow(WINDOW_SIZE, MIN_VALUE_FOR_MATCH);

    public ColourSensor(Port port, RobotColourConfigFileBom colourConfig) {
        initColorSensor(port);
        sensorMeasure = new SensorMeasure<>();
        float sigmaFactor = colourConfig.getSigmaFactor();

        for (EnumColours colour : EnumColours.values()) {
            if (colour == EnumColours.NONE) {
                continue;
            }

            RgbColour rgbColour = colourConfig.getRgbColour(colour);
            RgbColourSigma rgbColourSigma = colourConfig.getRgbColourSigma(colour);

            colourMap.addColour(colour,
                    new ColourMatcher(
                            rgbColour,
                            rgbColourSigma,
                            sigmaFactor));
        }

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
