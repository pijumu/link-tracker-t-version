package backend.academy.scrapper.service.data;

import backend.academy.scrapper.domain.ChatRepository;
import backend.academy.scrapper.domain.dto.ChatDto;
import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.exception.RepositoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    @Transactional
    public void registerChat(Long chatId) {
        try {
            chatRepository.save(new ChatDto(chatId));
        } catch (DataIntegrityViolationException e) {
            throw new ChatAlreadyRegisteredException("Чат уже зарегистрирован: " + chatId.toString());
        } catch (DataAccessException e) {
            throw new RepositoryException("Не удалось зарегистрировать чат.");
        }
    }

    @Transactional
    public void removeChat(Long chatId) {
        try {
            chatRepository.deleteById(chatId);
        } catch (EmptyResultDataAccessException e) {
            throw new ChatNotFoundException("Чат не найден: " + chatId);
        } catch (DataAccessException e) {
            throw new RepositoryException("Не удалось удалить чат.");
        }
    }

    @Transactional(readOnly = true)
    public boolean isRegisteredChat(Long chatId) {
        try {
            return chatRepository.existsById(chatId);
        } catch (DataAccessException e) {
            throw new RepositoryException("Не удалось проверить наличие чата.");
        }
    }
}
