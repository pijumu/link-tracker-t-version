package backend.academy.bot.converter;

import backend.academy.bot.domain.ChatContext;
import backend.academy.bot.fsm.MessageConstants;
import backend.academy.dto.dto.AddLinkRequest;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChatContextToAddLinkRequestConverter implements Converter<ChatContext, AddLinkRequest> {
    @Override
    public AddLinkRequest convert(@NotNull ChatContext context) {
        String tempTags = context.attributes().getOrDefault(MessageConstants.TAGS, "");
        List<String> tags = tempTags.isEmpty() ? List.of() : List.of(tempTags.split(" "));

        String tempFilters = context.attributes().getOrDefault(MessageConstants.FILTERS, "");
        List<String> filters = tempFilters.isEmpty() ? List.of() : List.of(tempFilters.split(" "));

        String url = context.attributes().get(MessageConstants.URL);
        return new AddLinkRequest(url, tags, filters);
    }
}
