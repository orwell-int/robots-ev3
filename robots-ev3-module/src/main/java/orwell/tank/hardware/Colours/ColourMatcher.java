package orwell.tank.hardware.Colours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public class ColourMatcher {
    private static final Logger logback = LoggerFactory.getLogger(ColourMatcher.class);

    private final RgbColour rgbColourMin;
    private final RgbColour rgbColourMax;

    public ColourMatcher(RgbColour rgbColour, RgbColourSigma rgbColourSigma, float sigmaFactor) {
        RgbColourSigma adjustedRgbColourSigma = rgbColourSigma.mult(sigmaFactor);
        rgbColourMin = rgbColour.sub(adjustedRgbColourSigma);
        rgbColourMax = rgbColour.add(adjustedRgbColourSigma);
        logback.info("RgbColourMin: [" + rgbColourMin + "] RgbColourMax: " + rgbColourMax + "]");
    }

    public boolean doesMatch(RgbColour rgbColour) {
        return rgbColourMin.isLowerThan(rgbColour) && rgbColour.isLowerThan(rgbColourMax);
    }
}
