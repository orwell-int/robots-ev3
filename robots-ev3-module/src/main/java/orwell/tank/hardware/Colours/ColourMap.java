package orwell.tank.hardware.Colours;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public class ColourMap {
    private final Map<EnumColours, ColourMatcher> colourMap = new HashMap<>();

    public void addColour(EnumColours colour, ColourMatcher matcher) {
        colourMap.put(colour, matcher);
    }

    public EnumColours getColour(RgbColour rgbColour) {
        for (Entry<EnumColours, ColourMatcher> entry : colourMap.entrySet()) {
            if(entry.getValue().doesMatch(rgbColour)) {
                return entry.getKey();
            }
        }
        return EnumColours.NONE;
    }
}
