package orwell.tank.actions;

import lejos.mf.common.constants.GameStateStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;

import java.util.List;

public class GameState implements IInputAction {
    private final static Logger logback = LoggerFactory.getLogger(GameState.class);
    private String payload;

    public GameState(List<String> payloadBody) {
        if (payloadBody == null || payloadBody.size() == 0) {
            return;
        }
        this.payload = payloadBody.get(0);
        logback.debug("GameState: " + payloadBody);
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        if (payload != null && payload.equalsIgnoreCase(GameStateStrings.Victory)) {
            remoteRobot.handleVictory();
        }
        else if (payload != null && payload.equalsIgnoreCase(GameStateStrings.Defeat)) {
            remoteRobot.handleDefeat();
        }
        else if (payload != null && payload.equalsIgnoreCase(GameStateStrings.Draw)) {
            remoteRobot.handleDraw();
        }
        else if (payload != null && payload.equalsIgnoreCase(GameStateStrings.Wait)) {
            remoteRobot.handleWait();
        }
    }
}
