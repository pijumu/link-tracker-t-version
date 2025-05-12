package backend.academy.scrapper.client.stackoverflow;

import backend.academy.dto.dto.LinkUpdateDto;
import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.client.stackoverflow.dto.StackOverflowTitleResponse;
import backend.academy.scrapper.client.stackoverflow.dto.StackOverflowUpdateResponse;
import backend.academy.scrapper.client.stackoverflow.dto.StackOverflowUpdateType;
import backend.academy.scrapper.client.stackoverflow.util.StackOverflowClient;
import backend.academy.scrapper.client.stackoverflow.util.StackOverflowUpdateMessageFormatter;
import backend.academy.scrapper.client.util.ClientAdapter;
import backend.academy.scrapper.domain.dto.UpdateInfoDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import backend.academy.scrapper.util.Constants;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StackOverflowClientAdapter implements ClientAdapter {
    private final StackOverflowClient stackOverflowClient;
    private final StackOverflowUpdateMessageFormatter formatter;

    @Override
    public UrlType getUrlType() {
        return UrlType.STACKOVERFLOW;
    }

    @Override
    public Optional<UpdateInfoDto> getUpdate(UrlInfoDto urlInfoDto) {

        StackOverflowUpdateResponse updateAnswers = stackOverflowClient.getAnswers(
                urlInfoDto.meta().get(Constants.QUESTION_ID),
                urlInfoDto.lastTimeUpdated().getEpochSecond());

        StackOverflowUpdateResponse updateComments = stackOverflowClient.getComments(
                urlInfoDto.meta().get(Constants.QUESTION_ID),
                urlInfoDto.lastTimeUpdated().getEpochSecond());

        List<LinkUpdateDto> result = new ArrayList<>();

        if (isEmpty(updateAnswers) && isEmpty(updateComments)) {
            return Optional.empty();
        }
        StackOverflowTitleResponse titleResponse =
                stackOverflowClient.getTitle(urlInfoDto.meta().get(Constants.QUESTION_ID));

        if (isEmpty(titleResponse)) {
            return Optional.empty();
        }

        String title = titleResponse.items().getFirst().title();

        Instant lastUpdateTime = null;

        // Добавление новых ответов
        if (!isEmpty(updateAnswers)) {
            updateAnswers.items().stream()
                    .map(answer -> new LinkUpdateDto(
                            urlInfoDto.url(),
                            formatter.formMessage(answer, title, StackOverflowUpdateType.ANSWER),
                            urlInfoDto.chatIds()))
                    .forEach(result::add);
            lastUpdateTime = updateAnswers.items().getLast().creationDate();
        }

        // Добавление новых комментариев
        if (!isEmpty(updateComments)) {
            updateComments.items().stream()
                    .map(answer -> new LinkUpdateDto(
                            urlInfoDto.url(),
                            formatter.formMessage(answer, title, StackOverflowUpdateType.COMMENT),
                            urlInfoDto.chatIds()))
                    .forEach(result::add);
            Instant temp = updateComments.items().getLast().creationDate();
            if (lastUpdateTime == null) {
                lastUpdateTime = temp;
            } else if (lastUpdateTime.isBefore(temp)) {
                lastUpdateTime = temp;
            }
        }

        return Optional.of(new UpdateInfoDto(lastUpdateTime, result));
    }

    public boolean isEmpty(StackOverflowUpdateResponse update) {
        return update == null || update.items() == null || update.items().isEmpty();
    }

    public boolean isEmpty(StackOverflowTitleResponse update) {
        return update == null || update.items() == null || update.items().isEmpty();
    }
}
