package backend.academy.bot.service;

import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.ListLinksResponse;
import backend.academy.dto.dto.RemoveLinkRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface ScrapperClient {
    @PostExchange("/tg-chat/{id}")
    void registerChat(@PathVariable("id") Long chatId);

    @DeleteExchange("/tg-chat/{id}")
    void removeChat(@PathVariable("id") Long chatId);

    @GetExchange("/links")
    ResponseEntity<ListLinksResponse> getLinks(@RequestHeader("Tg-Chat-Id") Long chatId);

    @PostExchange("/links")
    void addLink(@RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody AddLinkRequest addLinkRequest);

    @DeleteExchange("/links")
    void removeLink(@RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody RemoveLinkRequest removeLinkRequest);
}
