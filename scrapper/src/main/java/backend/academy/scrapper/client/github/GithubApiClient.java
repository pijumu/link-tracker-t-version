package backend.academy.scrapper.client.github;

import backend.academy.dto.validator.UrlType;
import backend.academy.scrapper.client.ExternalClient;
import backend.academy.scrapper.client.Notifier;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GithubApiClient implements ExternalClient {
    private final GithubClient githubClient;

    @Override
    public Notifier fetchData(Map<String, String> params) {
        return githubClient.getInfo(params.get("owner"), params.get("repo"));
    }

    @Override
    public UrlType getUrlType() {
        return UrlType.GITHUB;
    }
}
