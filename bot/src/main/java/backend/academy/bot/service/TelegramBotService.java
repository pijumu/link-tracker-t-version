package backend.academy.bot.service;

import backend.academy.bot.command.CommandMetadata;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class TelegramBotService {
    private final TelegramBot bot;
    private final HandlerService handlerService;
    private final ApplicationContext applicationContext;

    @Autowired
    public TelegramBotService(ApplicationContext applicationContext, TelegramBot bot, HandlerService handlerService) {
        this.bot = bot;
        this.handlerService = handlerService;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void initCommands() {
        SetMyCommands setMyCommands =
                new SetMyCommands(applicationContext.getBeansWithAnnotation(CommandMetadata.class).values().stream()
                        .map(bean -> {
                            CommandMetadata annotation = bean.getClass().getAnnotation(CommandMetadata.class);
                            return new BotCommand(annotation.name(), annotation.description());
                        })
                        .toArray(BotCommand[]::new));
        bot.execute(setMyCommands);
    }

    @PostConstruct
    public void setUpdateListener() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null) {
                    handlerService.handle(update);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
