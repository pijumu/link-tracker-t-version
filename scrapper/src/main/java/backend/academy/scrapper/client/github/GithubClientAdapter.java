package backend.academy.scrapper.client.github;

import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.client.UpdateDto;
import backend.academy.scrapper.client.UpdateType;
import backend.academy.scrapper.client.github.dto.GithubUpdateResponse;
import backend.academy.scrapper.client.github.util.GithubClient;
import backend.academy.scrapper.client.util.ClientAdapter;
import backend.academy.scrapper.client.util.Utility;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import backend.academy.scrapper.util.Constants;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GithubClientAdapter implements ClientAdapter {
    private final GithubClient githubClient;

    @Override
    public UrlType getUrlType() {
        return UrlType.GITHUB;
    }

    @Override
    public List<UpdateDto> getUpdates(UrlInfoDto urlInfoDto) {
        List<GithubUpdateResponse> updates = githubClient.getLastUpdate(
                urlInfoDto.meta().get(Constants.OWNER),
                urlInfoDto.meta().get(Constants.REPO),
                urlInfoDto.lastTimeUpdated());
        if (updates.isEmpty()) {
            return Collections.emptyList();
        }

        return updates.stream()
                .map(update -> new UpdateDto(
                        urlInfoDto.id(),
                        urlInfoDto.url(),
                        update.title(),
                        update.user().name(),
                        update.creationDate(),
                        Pair.of(
                                update.isPullRequest() ? UpdateType.GITHUB_PULL_REQUEST : UpdateType.GITHUB_ISSUE,
                                Utility.first200(update.body()))))
                .toList();
    }
}
