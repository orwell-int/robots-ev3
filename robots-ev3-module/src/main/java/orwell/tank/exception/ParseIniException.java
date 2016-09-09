package orwell.tank.exception;

/**
 * Created by Michaël Ludmann on 07/09/16.
 */
public class ParseIniException extends Exception {

    public ParseIniException(String incorrectIniValue) {
        super(incorrectIniValue);
    }
}
