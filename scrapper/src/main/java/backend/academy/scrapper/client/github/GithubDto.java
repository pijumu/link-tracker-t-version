package backend.academy.scrapper.client.github;

import backend.academy.scrapper.client.Notifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record GithubDto(String name, @JsonProperty("updated_at") String updatedAt) implements Notifier {

    @Override
    public Instant getFormattedTime() {
        return Instant.parse(this.updatedAt());
    }

    @Override
    public String getMessage() {
        return "Произошло обновление";
    }
}
