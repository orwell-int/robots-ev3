package orwell.tank.hardware.Sounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Silence implements IPlayable {
    private static final Logger logback = LoggerFactory.getLogger(Silence.class);
    private final long durationMs;

    Silence(long durationMs) {
        this.durationMs = durationMs;
    }

    @Override
    public void play() {
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            logback.error("Exception while sleeping " + durationMs + "ms in thread", e);
        }
    }
}
