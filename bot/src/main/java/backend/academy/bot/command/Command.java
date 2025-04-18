package backend.academy.bot.command;

public interface Command {
    void handle(Long chatId, String state, String message);
}
