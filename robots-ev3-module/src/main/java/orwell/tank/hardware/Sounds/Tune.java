package orwell.tank.hardware.Sounds;

import lejos.hardware.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static lejos.hardware.Sounds.PIANO;

public class Tune implements IPlayable {
    private static final Logger logback = LoggerFactory.getLogger(Tune.class);

    private final ArrayList<IPlayable> tune = new ArrayList<>();

    public static Tune GetVictoryTune(int[] instrument) {
        Tune victoryTune = new Tune();
        victoryTune.tune.add(new Tone(400, 200, 80, instrument));
        victoryTune.tune.add(new Silence(270));
        victoryTune.tune.add(new Tone(400, 100, 80, instrument));
        victoryTune.tune.add(new Silence(90));
        victoryTune.tune.add(new Tone(400, 160, 80, instrument));
        victoryTune.tune.add(new Silence(80));
        victoryTune.tune.add(new Tone(582, 970, 80, instrument));

        return victoryTune;
    }

    public static Tune GetDefeatTune(int[] instrument) {
        Tune victoryTune = new Tune();
        victoryTune.tune.add(new Tone(590, 810, 80, instrument));
        victoryTune.tune.add(new Tone(400, 760, 80, instrument));
        victoryTune.tune.add(new Tone(294, 1250, 80, instrument));

        return victoryTune;
    }

    public static Tune GetDrawTune(int[] instrument) {
        Tune victoryTune = new Tune();
        victoryTune.tune.add(new Tone(400, 570, 80, instrument));
        victoryTune.tune.add(new Tone(785, 460, 80, instrument));
        victoryTune.tune.add(new Tone(400, 420, 80, instrument));

        return victoryTune;
    }

    public static Tune GetLightFireTune() {
        Tune lightFireTune = new Tune();
        lightFireTune.tune.add(new Tone(350, 150, 80));

        return lightFireTune;
    }

    public static Tune GetHeavyFireTune() {
        Tune heavyFireTune = new Tune();
        heavyFireTune.tune.add(new Tone(86, 300, 80));

        return heavyFireTune;
    }

    @Override
    public void play() {
        final int oldVolume = Sound.getVolume();
        new TuneThread(tune).run();
        Sound.setVolume(oldVolume);
    }

    public static void main(String[] args) throws InterruptedException {
        GetVictoryTune(PIANO).play();
        Thread.sleep(300);
        GetDefeatTune(PIANO).play();
        Thread.sleep(300);
        GetDrawTune(PIANO).play();
        Thread.sleep(300);
        GetLightFireTune().play();
        Thread.sleep(300);
        GetHeavyFireTune().play();
    }

    private class TuneThread implements Runnable {
        private static final int THREAD_SLEEP_BETWEEN_TONES_MS = 1;
        private final ArrayList<IPlayable> tune;

        private TuneThread(ArrayList<IPlayable> tune) {
            this.tune = tune;
        }

        @Override
        public void run() {
            for (IPlayable playable : tune) {
                playable.play();
                try {
                    Thread.sleep(THREAD_SLEEP_BETWEEN_TONES_MS);
                } catch (InterruptedException e) {
                    logback.error("Tune thread interrupted", e);
                }
            }
        }
    }
}
