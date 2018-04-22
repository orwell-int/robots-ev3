package orwell.tank.actions;

import lejos.mf.common.constants.GameStateStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;

import java.util.List;

public class GameState implements IInputAction {
    private static final Logger logback = LoggerFactory.getLogger(GameState.class);
    private String payload;

    public GameState(List<String> payloadBody) {
        if (payloadBody == null || payloadBody.size() == 0) {
            return;
        }
        payload = payloadBody.get(0);
        logback.debug("GameState: " + payloadBody);
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        if (GameStateStrings.Victory.equalsIgnoreCase(payload)) {
            remoteRobot.handleVictory();
        }
        else if (GameStateStrings.Defeat.equalsIgnoreCase(payload)) {
            remoteRobot.handleDefeat();
        }
        else if (GameStateStrings.Draw.equalsIgnoreCase(payload)) {
            remoteRobot.handleDraw();
        }
        else if (GameStateStrings.Wait.equalsIgnoreCase(payload)) {
            remoteRobot.handleWait();
        }
    }
}
