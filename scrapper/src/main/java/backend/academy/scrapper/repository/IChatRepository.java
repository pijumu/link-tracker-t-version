package backend.academy.scrapper.repository;

import backend.academy.scrapper.model.Chat;

public interface IChatRepository {
    void add(Chat chat);

    void remove(Long chatId);

    boolean isRegistered(Long chatId);
}
