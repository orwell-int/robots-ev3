package orwell.tank.exception;

import java.nio.file.FileSystemException;

public class NotFileException extends FileSystemException {

    public NotFileException(String file) {
        super(file);
    }
}
