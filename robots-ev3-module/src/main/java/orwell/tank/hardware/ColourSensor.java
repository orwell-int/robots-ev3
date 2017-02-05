package orwell.tank.hardware;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.mf.common.UnitMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.config.RobotColourConfigFileBom;
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

    public ColourSensor(Port port, RobotColourConfigFileBom colourConfig) {
        initColorSensor(port);
        sensorMeasure = new SensorMeasure<>();

        for (EnumColours colour : EnumColours.values()) {
            if (colour == EnumColours.NONE) {
                continue;
            }
            colourMap.addColour(colour,
                    new ColourMatcher(colourConfig.getRgbColour(colour),
                            colourConfig.getRgbColourSigma(colour),
                            colourConfig.getSigmaFactor()));
        }

//        colourMap.addColour(
//                EnumColours.RED,
//                new ColourMatcher(
//                        new RgbColour(0.13748199f, 0.015051797f, 0.008865912f),
//                        new RgbColourSigma(0.0049859635f, 8.808833E-4f, 6.3952274E-4f),
//                        sigmaFactor));
//        colourMap.addColour(
//                EnumColours.BLUE,
//                new ColourMatcher(
//                        new RgbColour(0.032393806f, 0.05794528f, 0.051745355f),
//                        new RgbColourSigma(0.0016909537f, 0.0028935347f, 0.0022330638f),
//                        sigmaFactor));
//        colourMap.addColour(
//                EnumColours.GREEN,
//                new ColourMatcher(
//                        new RgbColour(0.059485763f, 0.11591416f, 0.020168781f),
//                        new RgbColourSigma(0.0038263516f, 0.007473127f, 0.0012494328f),
//                        sigmaFactor));
//        colourMap.addColour(
//                EnumColours.YELLOW,
//                new ColourMatcher(
//                        new RgbColour(0.21071628f, 0.109347545f, 0.017938823f),
//                        new RgbColourSigma(0.011403219f, 0.0059716245f, 0.0010305807f),
//                        sigmaFactor));
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
