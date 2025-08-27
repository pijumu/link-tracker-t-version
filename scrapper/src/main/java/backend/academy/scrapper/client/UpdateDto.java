package backend.academy.scrapper.client;

import java.time.Instant;
import org.apache.commons.lang3.tuple.Pair;

public record UpdateDto(
        Long urlId, String url, String topic, String user, Instant createdAt, Pair<UpdateType, String> preview) {}
