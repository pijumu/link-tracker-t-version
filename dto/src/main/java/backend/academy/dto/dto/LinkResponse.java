package backend.academy.dto.dto;

import java.util.List;

public record LinkResponse(Long id, String url, List<String> filters, List<String> tags) {}
