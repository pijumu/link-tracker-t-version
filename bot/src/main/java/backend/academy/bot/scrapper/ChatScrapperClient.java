package backend.academy.bot.scrapper;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(value = "/tg-chat")
public interface ChatScrapperClient {
    @PostExchange("/{chatId}")
    void registerChat(@PathVariable("chatId") Long chatId);

    // На будущее
    @DeleteExchange("/{chatId}")
    void removeChat(@PathVariable("chatId") Long chatId);

    @GetExchange("/{chatId}")
    boolean isRegisteredChat(@PathVariable("chatId") Long chatId);
}
