package backend.academy.scrapper.domain.dto;

import java.util.List;

public record LinkAddDto(String url, List<String> filters, List<String> tags) {}
