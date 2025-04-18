package backend.academy.scrapper.exception;

public class AlreadyTrackedUrlException extends RuntimeException {
    public AlreadyTrackedUrlException(String message) {
        super(message);
    }
}
