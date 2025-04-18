package backend.academy.bot.command.commands;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.CommandMetadata;
import backend.academy.bot.service.ChatSessionService;
import backend.academy.dto.validator.SupportedUrl;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@CommandMetadata(name = "/help", description = "список команд")
public class HelpCommand implements Command {
    private String info;
    private final TelegramBot bot;
    private final ChatSessionService chatSessionService;
    private final ApplicationContext applicationContext;

    @Autowired
    public HelpCommand(TelegramBot bot, ChatSessionService chatSessionService, ApplicationContext applicationContext) {
        this.chatSessionService = chatSessionService;
        this.bot = bot;
        this.applicationContext = applicationContext;
    }

    public void initInfoMessage() {
        StringBuilder infoBuilder = new StringBuilder("Поддерживаемые паттерны ссылок:\n");
        applicationContext.getBeansWithAnnotation(SupportedUrl.class).values().stream()
                .map(bean -> bean.getClass().getAnnotation(SupportedUrl.class))
                .forEach(command -> infoBuilder.append(command.helpPattern()).append("\n"));
        infoBuilder.append("\nДоступные команды:\n");
        applicationContext.getBeansWithAnnotation(CommandMetadata.class).values().stream()
                .map(bean -> bean.getClass().getAnnotation(CommandMetadata.class))
                .forEach(command -> infoBuilder
                        .append(command.name())
                        .append(" - ")
                        .append(command.description())
                        .append("\n"));
        this.info = infoBuilder.toString();
    }

    @Override
    public void handle(Long chatId, String state, String message) {
        if (info == null) {
            initInfoMessage();
        }

        if ("NOT_REGISTERED".equals(state)) {
            bot.execute(new SendMessage(chatId, info));
            return;
        }
        chatSessionService.setState(chatId, "IDLE");
        bot.execute(new SendMessage(chatId, info));
    }
}
