package orwell.tank.hardware.Colours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michaël Ludmann on 28/01/17.
 */
public class SlidingWindow {
    private final static Logger logback = LoggerFactory.getLogger(SlidingWindow.class);

    private final int windowSize;
    private final int minNumberForMatch;
    private final ArrayDeque<EnumColours> slidingWindow;
    private Map<EnumColours, MutableInt> colourCounterMap = new HashMap<>();

    public SlidingWindow(int windowSize, int minNumberForMatch) {
        this.windowSize = windowSize;
        this.minNumberForMatch = minNumberForMatch;
        slidingWindow = new ArrayDeque<>(windowSize);
    }

    public void addColour(EnumColours colour) {
//        logback.debug("Sliding Window add colour: " + colour);
        if(slidingWindow.size() == windowSize) {
            EnumColours colourRemoved = slidingWindow.removeFirst();
            MutableInt count = colourCounterMap.get(colourRemoved);
            count.decrement();
        }
        slidingWindow.addLast(colour);
        MutableInt count = colourCounterMap.get(colour);
        if (count == null) {
            colourCounterMap.put(colour, new MutableInt());
//            logback.debug("Adding colour " + colour + " in sliding window");
        } else {
            count.increment();
        }

    }

    public EnumColours getMainColour() {
        if (slidingWindow.size() < windowSize) {
            return EnumColours.NONE;
        }

        for(Map.Entry<EnumColours, MutableInt> entry : colourCounterMap.entrySet()) {
            if(entry.getValue().get() >= minNumberForMatch) {
                return entry.getKey();
            }
        }

        return EnumColours.NONE;
    }

    class MutableInt {
        int value = 1; // note that we start at 1 since we're counting

        public void increment() {
            ++value;
        }

        public void decrement() {
            --value;
        }

        public int  get(){
            return value;
        }
    }
}
