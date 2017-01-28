package orwell.tank.hardware.Colours;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public class ColourMatcher {
    private final RgbColour rgbColourMin;
    private final RgbColour rgbColourMax;

    public ColourMatcher(RgbColour rgbColour, RgbColourSigma rgbColourSigma, float sigmaFactor) {
        RgbColourSigma adjustedRgbColorSigma = rgbColourSigma.mult(sigmaFactor);
        rgbColourMin = rgbColour.sub(adjustedRgbColorSigma);
        rgbColourMax = rgbColour.add(adjustedRgbColorSigma);
    }

    public boolean doesMatch(RgbColour rgbColour) {
        return rgbColourMin.isLowerThan(rgbColour) && rgbColour.isLowerThan(rgbColourMax);
    }
}
