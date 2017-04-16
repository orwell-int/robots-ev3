package orwell.tank.actions;

import lejos.mf.common.constants.ConnectionStrings;
import orwell.tank.RemoteRobot;
import orwell.tank.messaging.EnumConnectionState;

public class Connection implements IInputAction {
    private final String payload;

    public Connection(String payload) {
        this.payload = payload;
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        if (payload.equalsIgnoreCase(ConnectionStrings.Ping)) {
            remoteRobot.setConnectionState(EnumConnectionState.CONNECTED);
            remoteRobot.sendConnectionAckMessage();
        }
    }
}
