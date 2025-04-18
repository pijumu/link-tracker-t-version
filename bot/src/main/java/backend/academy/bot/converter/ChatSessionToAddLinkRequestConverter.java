package backend.academy.bot.converter;

import backend.academy.bot.model.ChatSession;
import backend.academy.dto.dto.AddLinkRequest;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionToAddLinkRequestConverter implements Converter<ChatSession, AddLinkRequest> {
    @Override
    public AddLinkRequest convert(@NotNull ChatSession chatSession) {
        String tempTags = chatSession.attributes().getOrDefault("tags", "");
        List<String> tags = tempTags.isEmpty() ? List.of() : List.of(tempTags.split(" "));

        String tempFilters = chatSession.attributes().getOrDefault("filters", "");
        List<String> filters = tempFilters.isEmpty() ? List.of() : List.of(tempFilters.split(" "));

        String url = chatSession.attributes().get("url");
        return new AddLinkRequest(url, tags, filters);
    }
}
