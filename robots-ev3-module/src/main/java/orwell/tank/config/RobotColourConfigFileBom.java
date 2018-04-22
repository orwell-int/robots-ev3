package orwell.tank.config;

import orwell.tank.hardware.Colours.EnumColours;
import orwell.tank.hardware.Colours.RgbColour;
import orwell.tank.hardware.Colours.RgbColourSigma;

import java.util.HashMap;
import java.util.Map;

public class RobotColourConfigFileBom {
    private final Map<EnumColours, RgbColour> rgbColourMap = new HashMap<>();
    private final Map<EnumColours, RgbColourSigma> rgbColourSigmaMap = new HashMap<>();
    private float sigmaFactor = 1;

    public RgbColour getRgbColour(EnumColours colour) {
        return rgbColourMap.get(colour);
    }

    public void setRgbColour(EnumColours colour, RgbColour rgbColour) {
        rgbColourMap.put(colour, rgbColour);
    }

    public RgbColourSigma getRgbColourSigma(EnumColours colour) {
        return rgbColourSigmaMap.get(colour);
    }

    public void setRgbColourSigma(EnumColours colour, RgbColourSigma rgbColourSigma) {
        rgbColourSigmaMap.put(colour, rgbColourSigma);
    }

    public float getSigmaFactor() {
        return sigmaFactor;
    }

    public void setSigmaFactor(float sigmaFactor) {
        this.sigmaFactor = sigmaFactor;
    }
}
