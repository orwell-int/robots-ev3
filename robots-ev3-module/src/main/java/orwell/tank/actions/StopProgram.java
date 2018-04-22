package orwell.tank.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;

import java.util.List;

public class StopProgram implements IInputAction {
    private static final Logger logback = LoggerFactory.getLogger(NotHandled.class);

    public StopProgram(List<String> payloadBody) {
        logback.debug("StopProgram: " + payloadBody);
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        remoteRobot.stopRobotAndDisconnect();
    }
}
