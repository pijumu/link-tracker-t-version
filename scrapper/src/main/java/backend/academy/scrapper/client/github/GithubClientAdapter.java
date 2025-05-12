package backend.academy.scrapper.client.github;

import backend.academy.dto.dto.LinkUpdateDto;
import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.client.github.dto.GithubUpdateResponse;
import backend.academy.scrapper.client.github.util.GithubClient;
import backend.academy.scrapper.client.github.util.GithubUpdateMessageFormatter;
import backend.academy.scrapper.client.util.ClientAdapter;
import backend.academy.scrapper.domain.dto.UpdateInfoDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import backend.academy.scrapper.util.Constants;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GithubClientAdapter implements ClientAdapter {
    private final GithubClient githubClient;
    private final GithubUpdateMessageFormatter formatter;

    @Override
    public UrlType getUrlType() {
        return UrlType.GITHUB;
    }

    @Override
    public Optional<UpdateInfoDto> getUpdate(UrlInfoDto urlInfoDto) {
        List<GithubUpdateResponse> updates = githubClient.getLastUpdate(
                urlInfoDto.meta().get(Constants.OWNER),
                urlInfoDto.meta().get(Constants.REPO),
                urlInfoDto.lastTimeUpdated());
        if (updates.isEmpty()) {
            return Optional.empty();
        }

        List<LinkUpdateDto> result = updates.stream()
                .map(update -> new LinkUpdateDto(urlInfoDto.url(), formatter.formMessage(update), urlInfoDto.chatIds()))
                .toList();
        return Optional.of(new UpdateInfoDto(updates.getLast().creationDate(), result));
    }
}
