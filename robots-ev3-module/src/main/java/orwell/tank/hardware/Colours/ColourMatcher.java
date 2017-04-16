package orwell.tank.hardware.Colours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public class ColourMatcher {
    private final static Logger logback = LoggerFactory.getLogger(ColourMatcher.class);

    private final RgbColour rgbColourMin;
    private final RgbColour rgbColourMax;

    public ColourMatcher(RgbColour rgbColour, RgbColourSigma rgbColourSigma, float sigmaFactor) {
        RgbColourSigma adjustedRgbColorSigma = rgbColourSigma.mult(sigmaFactor);
        rgbColourMin = rgbColour.sub(adjustedRgbColorSigma);
        rgbColourMax = rgbColour.add(adjustedRgbColorSigma);
        logback.info("RgbColourMin: [" + rgbColourMin + "] RgbColourMax: " + rgbColourMax + "]");
    }

    public boolean doesMatch(RgbColour rgbColour) {
        return rgbColourMin.isLowerThan(rgbColour) && rgbColour.isLowerThan(rgbColourMax);
    }
}
