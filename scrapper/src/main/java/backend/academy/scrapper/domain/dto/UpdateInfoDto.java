package backend.academy.scrapper.domain.dto;

import backend.academy.dto.dto.LinkUpdateDto;
import java.time.Instant;
import java.util.List;

public record UpdateInfoDto(Instant lastUpdateTime, List<LinkUpdateDto> updates) {}
