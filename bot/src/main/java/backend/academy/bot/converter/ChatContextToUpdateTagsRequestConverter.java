package backend.academy.bot.converter;

import static backend.academy.bot.fsm.Constants.TAGS;

import backend.academy.bot.domain.ChatContext;
import backend.academy.dto.dto.UpdateTagsRequest;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChatContextToUpdateTagsRequestConverter implements Converter<ChatContext, UpdateTagsRequest> {
    @Override
    public UpdateTagsRequest convert(@NotNull ChatContext context) {
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) context.attributes().getOrDefault(TAGS, Collections.emptyList());
        return new UpdateTagsRequest(tags);
    }
}
