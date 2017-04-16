package orwell.tank.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;

import java.util.List;

public class Move implements IInputAction {
    private final static Logger logback = LoggerFactory.getLogger(Move.class);
    private boolean hasMove = false;
    private double leftMove;
    private double rightMove;

    public Move(List<String> moveInput) {
        logback.debug("Move: " + moveInput);

        if (hasTwoInput(moveInput)) {
            parseInput(moveInput);
        }
    }

    private boolean hasTwoInput(List<String> moveInput) {
        return moveInput != null && moveInput.size() == 2;
    }

    private void parseInput(List<String> moveInput) {
        String leftMoveString = moveInput.get(0);
        String rightMoveString = moveInput.get(1);
        try {
            leftMove = Double.parseDouble(leftMoveString);
            rightMove = Double.parseDouble(rightMoveString);
            hasMove = true;
        } catch (NumberFormatException e) {
            logback.warn("Cannot parse Move input into double: " + moveInput);
        }
    }

    public void stop(RemoteRobot remoteRobot) {
        if (remoteRobot.getTracks() != null) {
            remoteRobot.getTracks().stop();
        }
    }

    public boolean hasMove() {
        return hasMove;
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        if (hasMove()) {
            remoteRobot.getTracks().setPower(leftMove, rightMove);
        }
    }
}
