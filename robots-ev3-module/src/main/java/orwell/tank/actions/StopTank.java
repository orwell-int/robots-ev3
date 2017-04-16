package orwell.tank.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;

import java.util.List;

public class StopTank implements IInputAction {
    private final static Logger logback = LoggerFactory.getLogger(StopTank.class);

    public StopTank(List<String> payloadBody) {
        logback.debug("StopTank: " + payloadBody);
    }

    public StopTank() {
        logback.info("StopTank");
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        Move move = new Move(null);
        move.stop(remoteRobot);
    }
}
