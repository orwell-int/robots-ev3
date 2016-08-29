package orwell.tank.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;

import java.util.List;

import static lejos.hardware.Sound.playTone;

/**
 * Created by Michaël Ludmann on 10/07/16.
 */
public class Fire implements IInputAction {
    private final static Logger logback = LoggerFactory.getLogger(Fire.class);

    private boolean hasFire = false;
    private boolean leftWeapon;
    private boolean rightWeapon;

    public Fire(List<String> fireInput) {
        logback.debug("Fire: " + fireInput);

        if (2 == fireInput.size()) {
            leftWeapon = Boolean.parseBoolean(fireInput.get(0));
            rightWeapon = Boolean.parseBoolean(fireInput.get(1));
            if (leftWeapon || rightWeapon) {
                hasFire = true;
            }
        }
    }

    public boolean hasLeftWeaponFired() {
        return leftWeapon;
    }

    public boolean hasRightWeaponFired() {
        return rightWeapon;
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        if (hasLeftWeaponFired())
            playTone(350, 300);
        if (hasRightWeaponFired()) // bigger weapon
            playTone(150, 600);
    }
}
