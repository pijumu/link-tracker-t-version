package backend.academy.bot.exception;

public class ChatNotRegisteredException extends RuntimeException {
    public ChatNotRegisteredException(String message) {
        super(message);
    }
}
