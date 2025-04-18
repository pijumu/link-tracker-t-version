package backend.academy.scrapper.client.stackoverflow;

import backend.academy.dto.validator.UrlType;
import backend.academy.scrapper.client.ExternalClient;
import backend.academy.scrapper.client.Notifier;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StackOverflowApiClient implements ExternalClient {
    private final StackOverflowClient soClient;

    @Override
    public Notifier fetchData(Map<String, String> params) {
        return soClient.getInfo(params.get("questionId"));
    }

    @Override
    public UrlType getUrlType() {
        return UrlType.STACKOVERFLOW;
    }
}
