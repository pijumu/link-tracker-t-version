package backend.academy.bot.converter;

import backend.academy.bot.model.ChatSession;
import backend.academy.dto.dto.RemoveLinkRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionToRemoveLinkRequestConverter implements Converter<ChatSession, RemoveLinkRequest> {
    @Override
    public RemoveLinkRequest convert(@NotNull ChatSession chatSession) {
        String url = chatSession.attributes().get("url");
        return new RemoveLinkRequest(url);
    }
}
