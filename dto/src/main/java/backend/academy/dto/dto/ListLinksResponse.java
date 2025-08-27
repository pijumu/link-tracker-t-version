package backend.academy.dto.dto;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, Integer size) {}
