package backend.academy.bot.converter;

import static backend.academy.bot.fsm.Constants.FILTERS;
import static backend.academy.bot.fsm.Constants.TAGS;
import static backend.academy.bot.fsm.Constants.URL;

import backend.academy.bot.domain.ChatContext;
import backend.academy.dto.dto.AddLinkRequest;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChatContextToAddLinkRequestConverter implements Converter<ChatContext, AddLinkRequest> {
    @Override
    public AddLinkRequest convert(@NotNull ChatContext context) {
        String url = (String) context.attributes().get(URL);
        @SuppressWarnings("unchecked")
        List<String> tags = (List<String>) context.attributes().getOrDefault(TAGS, Collections.emptyList());
        @SuppressWarnings("unchecked")
        List<String> filters = (List<String>) context.attributes().getOrDefault(FILTERS, Collections.emptyList());
        return new AddLinkRequest(url, tags, filters);
    }
}
