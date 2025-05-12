package backend.academy.bot.service;

import backend.academy.bot.fsm.command.util.Command;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TelegramBotService {
    private final TelegramBot bot;
    private final HandlerService handlerService;

    @Autowired
    public TelegramBotService(TelegramBot bot, HandlerService service, List<Command> commands) {
        this.bot = bot;
        this.handlerService = service;
        SetMyCommands setMyCommands = new SetMyCommands(commands.stream()
                .map(command -> {
                    log.debug("Команда {} была установлена.", command.getName());
                    return new BotCommand(command.getName(), command.getDescription());
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
