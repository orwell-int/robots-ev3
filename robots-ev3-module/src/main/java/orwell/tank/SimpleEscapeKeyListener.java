package orwell.tank;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleEscapeKeyListener implements KeyListener {
    private static final Logger logback = LoggerFactory.getLogger(SimpleEscapeKeyListener.class);

    private boolean wasKeyPressed = false;

    public SimpleEscapeKeyListener() {
        Button.ESCAPE.addKeyListener(this);
    }

    public void keyPressed(Key k) {
        logback.debug("Key " + k.getName() + " pressed");
        wasKeyPressed = true;
    }

    public void keyReleased(Key k) {
        logback.debug("Key " + k.getName() + " released");
        wasKeyPressed = true;
    }

    public boolean wasKeyPressed() {
        return wasKeyPressed;
    }

    public void reset() {
        wasKeyPressed = false;
    }
}
