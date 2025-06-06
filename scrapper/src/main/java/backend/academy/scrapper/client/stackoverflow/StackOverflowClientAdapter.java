package backend.academy.scrapper.client.stackoverflow;

import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.client.UpdateDto;
import backend.academy.scrapper.client.UpdateType;
import backend.academy.scrapper.client.stackoverflow.dto.StackOverflowTitleResponse;
import backend.academy.scrapper.client.stackoverflow.dto.StackOverflowUpdateResponse;
import backend.academy.scrapper.client.stackoverflow.util.StackOverflowClient;
import backend.academy.scrapper.client.util.ClientAdapter;
import backend.academy.scrapper.client.util.Utility;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import backend.academy.scrapper.util.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StackOverflowClientAdapter implements ClientAdapter {
    private final StackOverflowClient stackOverflowClient;
    // private final StackOverflowUpdateMessageFormatter formatter;

    @Override
    public UrlType getUrlType() {
        return UrlType.STACKOVERFLOW;
    }

    @Override
    public List<UpdateDto> getUpdates(UrlInfoDto urlInfoDto) {

        StackOverflowUpdateResponse updateAnswers = stackOverflowClient.getAnswers(
                urlInfoDto.meta().get(Constants.QUESTION_ID),
                urlInfoDto.lastTimeUpdated().getEpochSecond());

        StackOverflowUpdateResponse updateComments = stackOverflowClient.getComments(
                urlInfoDto.meta().get(Constants.QUESTION_ID),
                urlInfoDto.lastTimeUpdated().getEpochSecond());

        if (isEmpty(updateAnswers) && isEmpty(updateComments)) {
            return Collections.emptyList();
        }

        StackOverflowTitleResponse titleResponse =
                stackOverflowClient.getTitle(urlInfoDto.meta().get(Constants.QUESTION_ID));

        String title = titleResponse.items().getFirst().title();

        List<UpdateDto> updates = new ArrayList<>();

        // Добавление новых ответов
        if (!isEmpty(updateAnswers)) {
            updateAnswers.items().stream()
                    .map(answer -> new UpdateDto(
                            urlInfoDto.id(),
                            urlInfoDto.url(),
                            title,
                            answer.owner().name(),
                            answer.creationDate(),
                            Pair.of(UpdateType.STACKOVERFLOW_ANSWER, Utility.first200(answer.body()))))
                    .forEach(updates::add);
        }

        // Добавление новых комментариев
        if (!isEmpty(updateComments)) {
            updateComments.items().stream()
                    .map(comment -> new UpdateDto(
                            urlInfoDto.id(),
                            urlInfoDto.url(),
                            title,
                            comment.owner().name(),
                            comment.creationDate(),
                            Pair.of(UpdateType.STACKOVERFLOW_COMMENT, Utility.first200(comment.body()))))
                    .forEach(updates::add);
        }
        return updates.stream()
                .sorted(Comparator.comparing(UpdateDto::createdAt))
                .toList();
    }

    public boolean isEmpty(StackOverflowUpdateResponse update) {
        return update == null || update.items() == null || update.items().isEmpty();
    }

    public boolean isEmpty(StackOverflowTitleResponse update) {
        return update == null || update.items() == null || update.items().isEmpty();
    }
}
