package backend.academy.bot.scrapper;

import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.ListLinksResponse;
import backend.academy.dto.dto.RemoveLinkRequest;
import backend.academy.dto.dto.UpdateTagsRequest;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange(value = "/links")
public interface LinkScrapperClient {
    @GetExchange
    ListLinksResponse getLinks(
            @RequestHeader("Tg-Chat-Id") Long chatId, @RequestParam(required = false) List<String> tags);

    @PostExchange
    void addLink(@RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody AddLinkRequest addLinkRequest);

    @DeleteExchange
    void removeLink(@RequestHeader("Tg-Chat-Id") Long chatId, @RequestBody RemoveLinkRequest removeLinkRequest);

    @PutExchange("/{linkId}/tags")
    void updateLinkTags(
            @RequestHeader("Tg-Chat-Id") Long chatId,
            @PathVariable("linkId") Long linkId,
            @RequestBody UpdateTagsRequest updateTagsRequest);
}
