package orwell.tank.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;

import java.util.List;

public class NotHandled implements IInputAction {
    private final static Logger logback = LoggerFactory.getLogger(NotHandled.class);
    private final List<String> payloadBody;

    public NotHandled(List<String> payloadBody) {
        this.payloadBody = payloadBody;
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        if (payloadBody != null) {
            logback.warn("NotHandled: " + payloadBody);
        }
    }
}
