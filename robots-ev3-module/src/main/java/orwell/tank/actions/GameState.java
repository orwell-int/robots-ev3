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

    public GameState(List<String> payloadBody) {
        logback.debug("GameState: " + payloadBody);
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {

    }
}
