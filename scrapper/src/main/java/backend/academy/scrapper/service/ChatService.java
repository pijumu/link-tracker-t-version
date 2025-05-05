package backend.academy.scrapper.service;

import backend.academy.scrapper.domain.IChatRepository;
import backend.academy.scrapper.domain.dto.Chat;
import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final IChatRepository chatRepository;

    public void registerChat(long chatId) {
        if (chatRepository.isRegistered(chatId)) {
            throw new ChatAlreadyRegisteredException("Чат " + chatId + " уже зарегистрирован.");
        }
        chatRepository.add(new Chat(chatId));
    }

    public void removeChat(long chatId) {
        if (!chatRepository.isRegistered(chatId)) {
            throw new ChatNotFoundException("Чат с " + chatId + " не существует.");
        }
        chatRepository.remove(chatId);
    }

    public boolean isRegisteredChat(long chatId) {
        return chatRepository.isRegistered(chatId);
    }
}
