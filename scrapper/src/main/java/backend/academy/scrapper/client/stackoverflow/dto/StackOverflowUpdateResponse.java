package backend.academy.scrapper.client.stackoverflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StackOverflowUpdateResponse(List<Update> items) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Update(
            Owner owner,
            @JsonProperty("creation_date") @JsonDeserialize(using = UnixTimestampDeserializer.class)
                    Instant creationDate,
            String body) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Owner(@JsonProperty("display_name") String name) {}
    }
}
