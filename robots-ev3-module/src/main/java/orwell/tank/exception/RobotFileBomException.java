package orwell.tank.exception;

import orwell.tank.config.RobotFileBom;

/**
 * Created by Michaël Ludmann on 09/09/16.
 */
public class RobotFileBomException extends Exception {
    public RobotFileBomException(RobotFileBom robotFileBom) {
        super("Model of Robot from ini file is incomplete");
    }
}
