package orwell.tank.exception;

import orwell.tank.config.RobotFileBom;

public class FileBomException extends Exception {
    public FileBomException(RobotFileBom robotFileBom) {
        super("Robot ini file is incomplete");
    }
}
