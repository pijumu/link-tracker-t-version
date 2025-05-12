package backend.academy.scrapper.repository.jpa.impl;

import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import backend.academy.scrapper.repository.jpa.entity.views.LinkView;
import backend.academy.scrapper.repository.jpa.entity.views.UrlIdChatIdView;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jpa")
@Repository
public interface LinkJpaRepository extends JpaRepository<LinkEntity, Long> {
    LinkView findByChatEntityIdAndUrlEntityUrl(Long chatEntityId, String urlEntityUrl);

    void deleteById(@NotNull Long linkId);

    List<LinkView> findAllByChatEntityId(Long chatEntityId);

    // Все ещё опираемся на свойство BigSerial
    @Query(
            """
    SELECT l.urlEntity.id AS urlId, l.chatEntity.id AS chatId
    FROM LinkEntity l
    WHERE :start <= l.urlEntity.id AND l.urlEntity.id <= :end
    """)
    List<UrlIdChatIdView> findByUrlIdRange(Long start, Long end);
}
