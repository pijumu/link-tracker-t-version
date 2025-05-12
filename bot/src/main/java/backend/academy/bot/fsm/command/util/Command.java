package backend.academy.bot.fsm.command.util;

import backend.academy.bot.domain.ChatContext;

public interface Command {
    String getName();

    String getDescription();

    String handle(Long chatId, String input, ChatContext context);
}
