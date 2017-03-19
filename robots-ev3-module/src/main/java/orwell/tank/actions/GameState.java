package orwell.tank.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;

import java.util.List;

/**
 * Created by Michaël Ludmann on 10/07/16.
 */
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
        if (payload != null && payload.equalsIgnoreCase("vict")) {
            remoteRobot.handleVictory();
        }
        else if (payload != null && payload.equalsIgnoreCase("fail")) {
            remoteRobot.handleDefeat();
        }
        else if (payload != null && payload.equalsIgnoreCase("draw")) {
            remoteRobot.handleDraw();
        }
        else if (payload != null && payload.equalsIgnoreCase("wait")) {
            remoteRobot.handleWait();
        }
    }
}
