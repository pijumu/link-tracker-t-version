package backend.academy.scrapper.repository;

import backend.academy.scrapper.domain.IChatRepository;
import backend.academy.scrapper.domain.dto.Chat;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryChatRepository implements IChatRepository {
    private final Map<Long, Chat> chats = new HashMap<>();

    @Override
    public void add(Chat chat) {
        chats.put(chat.id(), chat);
    }

    @Override
    public void remove(Long chatId) {
        chats.remove(chatId);
    }

    @Override
    public boolean isRegistered(Long chatId) {
        return chats.containsKey(chatId);
    }
}
