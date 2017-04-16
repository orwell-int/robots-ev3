package orwell.tank.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;
import orwell.tank.hardware.Sounds.Tone;

import java.util.ArrayList;
import java.util.List;

import static lejos.hardware.Sound.playTone;

public class Fire implements IInputAction {
    private final static Logger logback = LoggerFactory.getLogger(Fire.class);

    private boolean leftWeapon;
    private boolean rightWeapon;

    private Tone lightFire = new Tone(350, 300);
    private Tone heavyFire = new Tone(150, 600);

    public Fire(List<String> fireInput) {
        logback.debug("Fire: " + fireInput);

        if (2 == fireInput.size()) {
            leftWeapon = Boolean.parseBoolean(fireInput.get(0));
            rightWeapon = Boolean.parseBoolean(fireInput.get(1));
        }
    }

    private boolean hasLeftWeaponFired() {
        return leftWeapon;
    }

    private boolean hasRightWeaponFired() {
        return rightWeapon;
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        List<Tone> fires = new ArrayList<>();

        if (hasLeftWeaponFired())
            fires.add(lightFire);
        if (hasRightWeaponFired()) // bigger weapon
            fires.add(heavyFire);

        new SoundThread(fires).run();
    }

    private class SoundThread implements Runnable {
        private static final int THREAD_SLEEP_BETWEEN_TONES_MS = 5;
        public List<Tone> tones;

        public SoundThread(List<Tone> tones) {
            this.tones = tones;
        }

        @Override
        public void run() {
            for (Tone tone : tones) {
                try {
                    Thread.sleep(THREAD_SLEEP_BETWEEN_TONES_MS);
                } catch (InterruptedException e) {
                    logback.error(e.getStackTrace().toString());
                }
                playTone(tone.frequency, tone.duration);
            }
        }
    }
}
