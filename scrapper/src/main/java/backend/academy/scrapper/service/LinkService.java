package backend.academy.scrapper.service;

import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.LinkResponse;
import backend.academy.dto.dto.ListLinksResponse;
import backend.academy.dto.dto.RemoveLinkRequest;
import backend.academy.scrapper.domain.ILinkRepository;
import backend.academy.scrapper.domain.dto.Link;
import backend.academy.scrapper.exception.AlreadyTrackedUrlException;
import backend.academy.scrapper.exception.UrlNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final ILinkRepository linkRepository;
    private long nextId = 1L;

    public ListLinksResponse getLinks(Long chatId) {
        List<Link> listOfLinks = linkRepository.getLinksForChatId(chatId);
        return new ListLinksResponse(
                listOfLinks.stream().map(Link::toLinkResponse).toList(), listOfLinks.size());
    }

    public LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest) {
        if (linkRepository.isTracking(chatId, addLinkRequest.url())) {
            throw new AlreadyTrackedUrlException("Ссылка " + addLinkRequest.url() + " уже отслеживается!");
        }
        Link link = new Link(
                nextId++, addLinkRequest.url(), addLinkRequest.tags(), addLinkRequest.filters(), Instant.now());
        linkRepository.add(chatId, link);
        return link.toLinkResponse();
    }

    public LinkResponse removeLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        Optional<Link> link = linkRepository.remove(chatId, removeLinkRequest.url());
        return link.map(Link::toLinkResponse).orElseThrow(() -> new UrlNotFoundException("Ссылка не найдена"));
    }
}
