package backend.academy.scrapper.service;

import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.model.Chat;
import backend.academy.scrapper.repository.IChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final IChatRepository chatRepository;

    @Transactional
    public void registerChat(long chatId) {
        if (chatRepository.isRegistered(chatId)) {
            throw new ChatAlreadyRegisteredException("Чат " + chatId + " уже зарегистрирован.");
        }
        chatRepository.add(new Chat(chatId));
    }

    @Transactional
    public void removeChat(long chatId) {
        if (!chatRepository.isRegistered(chatId)) {
            throw new ChatNotFoundException("Чат с " + chatId + " не существует.");
        }
        chatRepository.remove(chatId);
    }
}
