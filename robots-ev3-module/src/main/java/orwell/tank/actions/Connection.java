package orwell.tank.actions;

import orwell.tank.RemoteRobot;
import orwell.tank.messaging.EnumConnectionState;

/**
 * Created by Michaël Ludmann on 10/09/16.
 */
public class Connection implements IInputAction {
    private final String payload;

    public Connection(String payload) {
        this.payload = payload;
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        if (payload.equalsIgnoreCase("ping")) {
            remoteRobot.setConnectionState(EnumConnectionState.CONNECTED);
            remoteRobot.sendConnectionAckMessage();
        }
    }
}
