package backend.academy.scrapper.service.data;

import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.LinkResponse;
import backend.academy.dto.dto.ListLinksResponse;
import backend.academy.dto.dto.RemoveLinkRequest;
import backend.academy.dto.dto.UpdateTagsRequest;
import backend.academy.scrapper.domain.LinkRepository;
import backend.academy.scrapper.domain.UrlRepository;
import backend.academy.scrapper.domain.dto.LinkAddDto;
import backend.academy.scrapper.domain.dto.LinkDto;
import backend.academy.scrapper.domain.dto.ParsedUrlDto;
import backend.academy.scrapper.domain.dto.UrlAddDto;
import backend.academy.scrapper.exception.AlreadyTrackedUrlException;
import backend.academy.scrapper.exception.NoSuchLinkException;
import backend.academy.scrapper.exception.UrlNotFoundException;
import backend.academy.scrapper.service.UrlParserService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final UrlParserService urlParserService;
    private final LinkRepository linkRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public LinkResponse addLink(Long chatId, AddLinkRequest addLinkRequest) {

        Long urlId = urlRepository.findByUrl(addLinkRequest.url()).orElseGet(() -> {
            ParsedUrlDto parsedUrl = urlParserService.parse(addLinkRequest.url());
            return urlRepository.save(
                    new UrlAddDto(addLinkRequest.url(), parsedUrl.urlType(), Instant.now(), parsedUrl.params()));
        });
        try {
            Long linkId = linkRepository.save(
                    new LinkAddDto(addLinkRequest.url(), addLinkRequest.filters(), addLinkRequest.tags()),
                    chatId,
                    urlId);
            return new LinkResponse(linkId, addLinkRequest.url(), addLinkRequest.filters(), addLinkRequest.tags());
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyTrackedUrlException("Ссылка уже отслеживается.");
        }
    }

    @Transactional
    public LinkResponse removeLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        try {
            LinkDto link = linkRepository.findByChatIdAndUrl(chatId, removeLinkRequest.url());
            linkRepository.deleteById(link.id());
            return new LinkResponse(link.id(), link.url(), link.filters(), link.tags());
        } catch (EmptyResultDataAccessException | NoSuchElementException e) {
            throw new UrlNotFoundException("Ссылка не найдена: " + removeLinkRequest.url());
        }
    }

    @Transactional(readOnly = true)
    public ListLinksResponse getLinks(Long chatId) {
        List<LinkResponse> links = linkRepository.findAllByChatId(chatId).stream()
                .map(linkDto -> new LinkResponse(linkDto.id(), linkDto.url(), linkDto.filters(), linkDto.tags()))
                .toList();
        return new ListLinksResponse(links, links.size());
    }

    @Transactional(readOnly = true)
    public ListLinksResponse getLinksWithTags(Long chatId, List<String> tags) {
        List<LinkResponse> links = linkRepository.findAllByChatIdWithTags(chatId, tags).stream()
                .map(linkDto -> new LinkResponse(linkDto.id(), linkDto.url(), linkDto.filters(), linkDto.tags()))
                .toList();
        return new ListLinksResponse(links, links.size());
    }

    @Transactional
    public LinkResponse updateLinkTags(Long chatId, Long linkId, UpdateTagsRequest tags) {
        LinkDto link = linkRepository
                .updateTagsByLinkId(linkId, tags.tags())
                .orElseThrow(() -> new NoSuchLinkException("Не была найдена ссылка с linkId: " + linkId));
        return new LinkResponse(link.id(), link.url(), link.filters(), link.tags());
    }
}
