package backend.academy.bot.service.data;

import backend.academy.bot.repository.RedisCacheListCommandRepository;
import backend.academy.bot.scrapper.LinkScrapperClient;
import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.ListLinksResponse;
import backend.academy.dto.dto.RemoveLinkRequest;
import backend.academy.dto.dto.UpdateTagsRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkScrapperService {
    private final LinkScrapperClient linkScrapperClient;
    private final RedisCacheListCommandRepository cacheListCommandRepository;

    // Учитывая логику фильтрации по тегам, этот метод выглядит наполовину абсурдно.
    // Как так-то?
    // Больше смысла было бы, если фильтрация по тегам происходила бы на bot. Но это переписывание всей логики:)
    // C другой стороны мы размазываем логику работы с data между bot и scrapper, поэтому этот подход более менее
    // оправдан
    public ListLinksResponse getLinks(Long chatId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            var result = cacheListCommandRepository.get(chatId);
            if (result != null) {
                return result;
            }
        }
        var result = linkScrapperClient.getLinks(chatId, tags);
        if (tags == null || tags.isEmpty()) {
            cacheListCommandRepository.put(chatId, result);
        }
        return result;
    }

    public void addLink(Long chatId, AddLinkRequest addLinkRequest) {
        linkScrapperClient.addLink(chatId, addLinkRequest);
        cacheListCommandRepository.invalidate(chatId);
    }

    public void removeLink(Long chatId, RemoveLinkRequest removeLinkRequest) {
        linkScrapperClient.removeLink(chatId, removeLinkRequest);
        cacheListCommandRepository.invalidate(chatId);
    }

    public void updateLinkTags(Long chatId, Long linkId, UpdateTagsRequest updateTagsRequest) {
        linkScrapperClient.updateLinkTags(chatId, linkId, updateTagsRequest);
        cacheListCommandRepository.invalidate(chatId);
    }
}
