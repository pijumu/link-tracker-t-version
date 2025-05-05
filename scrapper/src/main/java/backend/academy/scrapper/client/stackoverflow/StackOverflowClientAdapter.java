package backend.academy.scrapper.client.stackoverflow;

import backend.academy.dto.validator.UrlType;
import backend.academy.scrapper.client.util.ClientAdapter;
import backend.academy.scrapper.client.util.UpdateDto;
import backend.academy.scrapper.client.util.UpdateMapper;
import backend.academy.scrapper.util.Constants;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StackOverflowClientAdapter implements ClientAdapter {
    private final StackOverflowClient client;
    private final UpdateMapper updateMapper;

    @Override
    public UpdateDto getUpdate(Map<String, String> params) {
        return updateMapper.map(client.getInfo(params.get(Constants.QUESTION_ID)));
    }

    @Override
    public UrlType getUrlType() {
        return UrlType.STACKOVERFLOW;
    }
}
