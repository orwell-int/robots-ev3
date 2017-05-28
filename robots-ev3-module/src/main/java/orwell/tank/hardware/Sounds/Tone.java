package orwell.tank.hardware.Sounds;

import static lejos.hardware.Sound.playNote;
import static lejos.hardware.Sound.setVolume;
import static lejos.hardware.Sounds.PIANO;

public class Tone implements IPlayable {
    private final int[] instrument;
    private final int frequency;
    private final int durationMs;
    private final int volume;

    public Tone(int frequency, int durationMs, int volume) {
        this(frequency, durationMs, volume, PIANO);
    }

    public Tone(int frequency, int durationMs, int volume, int[] instrument) {
        this.frequency = frequency;
        this.durationMs = durationMs;
        this.instrument = instrument;
        this.volume = volume;
    }

    @Override
    public void play() {
        setVolume(volume);
        playNote(instrument, frequency, durationMs);
    }
}
