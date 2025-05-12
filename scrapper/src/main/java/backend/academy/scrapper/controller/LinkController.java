package backend.academy.scrapper.controller;

import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.LinkResponse;
import backend.academy.dto.dto.ListLinksResponse;
import backend.academy.dto.dto.RemoveLinkRequest;
import backend.academy.dto.dto.UpdateTagsRequest;
import backend.academy.scrapper.service.data.LinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/links")
public class LinkController {
    private final LinkService linkService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public ListLinksResponse getLinks(
            @RequestHeader("Tg-Chat-Id") Long chatId,
            @Size(max = 3) @RequestParam(required = false) List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return linkService.getLinks(chatId);
        }
        return linkService.getLinksWithTags(chatId, tags);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public LinkResponse addLink(
            @RequestHeader("Tg-Chat-Id") Long chatId, @Valid @RequestBody AddLinkRequest addLinkRequest) {
        return linkService.addLink(chatId, addLinkRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(produces = "application/json", consumes = "application/json")
    public LinkResponse removeLink(
            @RequestHeader("Tg-Chat-Id") Long chatId, @Valid @RequestBody RemoveLinkRequest removeLinkRequest) {
        return linkService.removeLink(chatId, removeLinkRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{linkId}/tags", produces = "application/json")
    public LinkResponse updateLinkTags(
            @RequestHeader("Tg-Chat-Id") Long chatId,
            @PathVariable Long linkId,
            @Valid @RequestBody UpdateTagsRequest updateTagsRequest) {
        return linkService.updateLinkTags(chatId, linkId, updateTagsRequest);
    }
}
