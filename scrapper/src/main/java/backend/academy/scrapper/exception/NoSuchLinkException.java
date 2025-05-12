package backend.academy.scrapper.exception;

public class NoSuchLinkException extends RuntimeException {
    public NoSuchLinkException(String message) {
        super(message);
    }
}
