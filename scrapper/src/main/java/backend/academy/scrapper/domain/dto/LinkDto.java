package backend.academy.scrapper.domain.dto;

import java.util.List;

public record LinkDto(Long id, String url, List<String> filters, List<String> tags) {}
