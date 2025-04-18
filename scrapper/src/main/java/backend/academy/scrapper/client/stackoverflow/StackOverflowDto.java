package backend.academy.scrapper.client.stackoverflow;

import backend.academy.scrapper.client.Notifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.List;

public record StackOverflowDto(
        @Min(value = 1, message = "Список должен содержать ровно 1 вопрос") List<QuestionDto> items)
        implements Notifier {

    public record QuestionDto(
            @JsonProperty("question_id") Integer questionId,
            String title,
            @JsonProperty("last_activity_date") Long lastActivityDate) {}

    @Override
    public Instant getFormattedTime() {
        return Instant.ofEpochSecond(this.items().getFirst().lastActivityDate());
    }

    @Override
    public String getMessage() {
        return "Произошло обновление";
    }
}
