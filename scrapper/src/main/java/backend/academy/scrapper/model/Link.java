package backend.academy.scrapper.model;

import backend.academy.dto.dto.LinkResponse;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Link {
    private final Long id;
    private final String url;
    private final List<String> tags;
    private final List<String> filters;
    private Instant lastUpdate;

    public boolean needUpdate(Instant lastUpdate) {
        if (this.lastUpdate.isBefore(lastUpdate)) {
            this.lastUpdate = lastUpdate;
            return true;
        }
        return false;
    }

    public LinkResponse toLinkResponse() {
        return new LinkResponse(this.id, this.url, this.tags, this.filters);
    }
}
