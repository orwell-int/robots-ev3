package orwell.tank.hardware.Sounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Silence implements IPlayable {
    private final static Logger logback = LoggerFactory.getLogger(Silence.class);
    public long durationMs;

    public Silence(long durationMs) {
        this.durationMs = durationMs;
    }

    @Override
    public void play() {
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            logback.error(e.getStackTrace().toString());
        }
    }
}
