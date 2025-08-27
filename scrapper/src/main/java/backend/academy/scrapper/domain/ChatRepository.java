package backend.academy.scrapper.domain;

import backend.academy.scrapper.domain.dto.ChatDto;

public interface ChatRepository {
    void save(ChatDto chatDto);

    void deleteById(Long id);

    boolean existsById(Long id);
}
