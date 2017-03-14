package orwell.tank;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;

public class SimpleKeyListener implements KeyListener {
    private boolean wasKeyPressed = false;

    public SimpleKeyListener() {
        Button.ESCAPE.addKeyListener(this);
    }

    public void keyPressed(Key k) {
        wasKeyPressed = true;
    }

    public void keyReleased(Key k) {
        wasKeyPressed = true;
    }

    public boolean wasKeyPressed() {
        return wasKeyPressed;
    }

    public void reset() {
        wasKeyPressed = false;
    }
}
