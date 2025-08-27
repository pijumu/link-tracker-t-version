package backend.academy.bot.service.update.digest;

import backend.academy.bot.domain.ChatUpdate;
import backend.academy.bot.service.MessageSenderService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.notifications", value = "mode", havingValue = "digest")
@RequiredArgsConstructor
public class ChatUpdateBatchProcessor {
    private final MessageSenderService sender;

    public void process(List<ChatUpdate> batch) {
        Map<Long, List<ChatUpdate>> byUrlId = batch.stream().collect(Collectors.groupingBy(ChatUpdate::urlId));

        String bigMessage = byUrlId.values().stream()
                .map(updatesForUrl -> {
                    String url = updatesForUrl.getFirst().url();

                    String combinedMsg = updatesForUrl.stream()
                            .map(ChatUpdate::message)
                            .distinct()
                            .collect(Collectors.joining("\n"));

                    return "🔔 Обновление по ссылке:%n%s%n%n%s".formatted(url, combinedMsg);
                })
                .collect(Collectors.joining("\n\n"));

        sender.sendMessage(bigMessage, batch.getFirst().chatId());
    }
}
