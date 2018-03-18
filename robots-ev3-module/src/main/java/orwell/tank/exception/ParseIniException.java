package orwell.tank.exception;

public class ParseIniException extends Exception {

    public ParseIniException(String incorrectIniValue) {
        super(incorrectIniValue);
    }
}
