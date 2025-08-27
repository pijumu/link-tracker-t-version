package backend.academy.scrapper.domain;

import backend.academy.scrapper.domain.dto.LinkAddDto;
import backend.academy.scrapper.domain.dto.LinkDto;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {
    Long save(LinkAddDto link, Long chatId, Long urlId);

    LinkDto findByChatIdAndUrl(Long chatId, String url);

    void deleteById(Long linkId);

    List<LinkDto> findAllByChatId(Long chatId);

    List<LinkDto> findAllByChatIdWithTags(Long chatId, List<String> tags);

    Optional<LinkDto> updateTagsByLinkId(Long linkId, List<String> tags);

    List<Long> getChatIdsByUrlIdAndFilterLogin(Long urlId, String filterLogin);
}
