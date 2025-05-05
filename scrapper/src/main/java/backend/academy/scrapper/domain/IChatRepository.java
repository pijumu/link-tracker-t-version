package backend.academy.scrapper.domain;

import backend.academy.scrapper.domain.dto.Chat;

public interface IChatRepository {
    void add(Chat chat);

    void remove(Long chatId);

    boolean isRegistered(Long chatId);
}
