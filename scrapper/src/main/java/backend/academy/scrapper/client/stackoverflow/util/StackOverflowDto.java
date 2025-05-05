package backend.academy.scrapper.client.stackoverflow.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.List;

public record StackOverflowDto(List<QuestionDto> items) {
    public record QuestionDto(
            String title,
            @JsonProperty("last_activity_date") @JsonDeserialize(using = UnixTimestampDeserializer.class)
                    Instant lastActivityDate) {}
}
