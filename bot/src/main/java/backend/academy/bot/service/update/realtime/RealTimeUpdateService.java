package backend.academy.bot.service.update.realtime;

import backend.academy.bot.service.MessageSenderService;
import backend.academy.bot.service.update.UpdateService;
import backend.academy.dto.dto.LinkUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app.notifications", value = "mode", havingValue = "realtime")
@RequiredArgsConstructor
public class RealTimeUpdateService implements UpdateService {
    private final MessageSenderService sender;

    @Override
    public void handleUpdate(LinkUpdateDto update) {
        update.tgChatIds().forEach(chatId -> sender.sendMessage(update.description(), chatId));
    }
}
