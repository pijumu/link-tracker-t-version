package backend.academy.scrapper.client.util;

import java.time.Instant;

public record UpdateDto(String name, Instant lastTimeUpdated) {}
