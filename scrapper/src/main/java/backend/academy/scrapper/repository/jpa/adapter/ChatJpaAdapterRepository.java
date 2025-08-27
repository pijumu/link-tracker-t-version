package backend.academy.scrapper.repository.jpa.adapter;

import backend.academy.scrapper.domain.ChatRepository;
import backend.academy.scrapper.domain.dto.ChatDto;
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.impl.ChatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(prefix = "app", value = "data-access", havingValue = "jpa")
@Repository
@RequiredArgsConstructor
public class ChatJpaAdapterRepository implements ChatRepository {
    private final ChatJpaRepository chatJpaRepository;

    @Override
    public void save(ChatDto chatDto) {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.id(chatDto.id());
        chatJpaRepository.save(chatEntity);
    }

    @Override
    public void deleteById(Long id) {
        chatJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return chatJpaRepository.existsById(id);
    }
}
