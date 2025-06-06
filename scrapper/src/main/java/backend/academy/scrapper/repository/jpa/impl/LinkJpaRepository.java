package backend.academy.scrapper.repository.jpa.impl;

import backend.academy.scrapper.repository.jpa.entity.LinkEntity;
import backend.academy.scrapper.repository.jpa.entity.views.ChatIdView;
import backend.academy.scrapper.repository.jpa.entity.views.LinkView;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jpa")
@Repository
public interface LinkJpaRepository extends JpaRepository<LinkEntity, Long> {
    LinkView findByChatEntityIdAndUrlEntityUrl(Long chatEntityId, String urlEntityUrl);

    void deleteById(@NotNull Long linkId);

    List<LinkView> findAllByChatEntityId(Long chatEntityId);

    List<ChatIdView> findAllByUrlEntityId(Long urlEntityId);
}
