package orwell.tank.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;
import orwell.tank.hardware.Sounds.Tune;

import java.util.List;

public class Fire implements IInputAction {
    private static final Logger logback = LoggerFactory.getLogger(Fire.class);

    private boolean leftWeapon;
    private boolean rightWeapon;

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
        if (hasLeftWeaponFired()) {
            Tune.GetLightFireTune().play();
        }
        if (hasRightWeaponFired()) {
            Tune.GetHeavyFireTune().play();
        }
    }
}
