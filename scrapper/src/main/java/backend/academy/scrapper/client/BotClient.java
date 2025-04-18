package backend.academy.scrapper.client;

import backend.academy.dto.dto.LinkUpdate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface BotClient {
    @PostExchange(value = "/updates", accept = "application/json")
    void sendUpdate(@RequestBody LinkUpdate linkUpdate);
}
