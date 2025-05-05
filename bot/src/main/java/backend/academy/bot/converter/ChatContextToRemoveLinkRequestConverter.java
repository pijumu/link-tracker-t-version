package backend.academy.bot.converter;

import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.dto.dto.RemoveLinkRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChatContextToRemoveLinkRequestConverter implements Converter<ChatContext, RemoveLinkRequest> {
    @Override
    public RemoveLinkRequest convert(@NotNull ChatContext context) {
        String url = context.attributes().get(MessageConstants.URL);
        return new RemoveLinkRequest(url);
    }
}
