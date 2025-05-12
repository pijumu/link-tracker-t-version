package backend.academy.scrapper.repository.jpa.adapter;

import backend.academy.scrapper.domain.LinkRepository;
import backend.academy.scrapper.domain.dto.LinkAddDto;
import backend.academy.scrapper.domain.dto.LinkDto;
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import backend.academy.scrapper.repository.jpa.entity.UrlEntity;
import backend.academy.scrapper.repository.jpa.entity.views.LinkView;
import backend.academy.scrapper.repository.jpa.impl.LinkJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jpa")
@Repository
@RequiredArgsConstructor
public class LinkJpaAdapterRepository implements LinkRepository {
    private final LinkJpaRepository linkJpaRepository;

    @Override
    public Long save(LinkAddDto link, Long chatId, Long urlId) {
        LinkEntity linkEntity = new LinkEntity();
        ChatEntity chatEntity = new ChatEntity();
        UrlEntity urlEntity = new UrlEntity();

        chatEntity.id(chatId);
        urlEntity.id(urlId);

        linkEntity.chatEntity(chatEntity);
        linkEntity.urlEntity(urlEntity);
        linkEntity.filters(link.filters());
        linkEntity.tags(link.tags());

        linkEntity = linkJpaRepository.save(linkEntity);
        return linkEntity.id();
    }

    @Override
    public LinkDto findByChatIdAndUrl(Long chatId, String url) {
        LinkView link = linkJpaRepository.findByChatEntityIdAndUrlEntityUrl(chatId, url);
        return new LinkDto(link.getId(), link.getUrlEntity().getUrl(), link.getFilters(), link.getTags());
    }

    @Override
    public void deleteById(Long linkId) {
        linkJpaRepository.deleteById(linkId);
    }

    @Override
    public List<LinkDto> findAllByChatId(Long chatId) {
        return linkJpaRepository.findAllByChatEntityId(chatId).stream()
                .map(this::toLinkDto)
                .toList();
    }

    @Override
    public List<LinkDto> findAllByChatIdWithTags(Long chatId, List<String> tags) {
        return linkJpaRepository.findAllByChatEntityId(chatId).stream()
                .map(this::toLinkDto)
                .filter(linkDto -> linkDto.tags().containsAll(tags))
                .toList();
    }

    @Override
    public Optional<LinkDto> updateTagsByLinkId(Long linkId, List<String> tags) {
        return linkJpaRepository.findById(linkId).map(linkEntity -> {
            linkEntity.tags(tags);
            return new LinkDto(linkEntity.id(), linkEntity.urlEntity().url(), linkEntity.filters(), linkEntity.tags());
        });
    }

    private LinkDto toLinkDto(LinkView link) {
        return new LinkDto(link.getId(), link.getUrlEntity().getUrl(), link.getFilters(), link.getTags());
    }
}
