package backend.academy.bot.service;

import backend.academy.bot.command.Command;
import backend.academy.bot.command.CommandMetadata;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HandlerService {
    private final TelegramBot bot;
    private final ChatSessionService chatSessionService;
    private final ApplicationContext applicationContext;
    private final Map<String, Command> stateToCommand;
    private final Map<String, Command> nameToCommand;

    @Autowired
    public HandlerService(
            TelegramBot bot, ChatSessionService chatSessionService, ApplicationContext applicationContext) {
        this.bot = bot;
        this.chatSessionService = chatSessionService;
        this.applicationContext = applicationContext;
        this.stateToCommand = new HashMap<>();
        this.nameToCommand = new HashMap<>();
    }

    @PostConstruct
    private void initStrategies() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(CommandMetadata.class);
        beans.forEach((unused, bean) -> {
            CommandMetadata annotation = bean.getClass().getAnnotation(CommandMetadata.class);
            Command handler = (Command) bean;
            for (String state : annotation.states()) {
                if (stateToCommand.containsKey(state)) {
                    log.error("Стейт {} уже существует!", state);
                    throw new IllegalStateException(state + " используется несколькими командами.");
                }
                stateToCommand.put(state, handler);
            }
            nameToCommand.put(annotation.name(), handler);
        });
    }

    public void handle(@NotNull Update update) {

        long chatId = update.message().chat().id();
        String message = update.message().text();
        String state = chatSessionService.getState(chatId);

        if (stateToCommand.containsKey(state)) {
            stateToCommand.get(state).handle(chatId, state, message);
            return;
        }
        if (nameToCommand.containsKey(message)) {
            nameToCommand.get(message).handle(chatId, state, message);
            return;
        }

        if (message.startsWith("/")) {
            bot.execute(new SendMessage(chatId, "Неизвестная команда."));
            return;
        }

        nameToCommand.get("/help").handle(chatId, state, message);
    }
}
