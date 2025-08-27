package backend.academy.scrapper.repository.jpa.impl;

import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jpa")
@Repository
public interface ChatJpaRepository extends JpaRepository<ChatEntity, Long> {}
