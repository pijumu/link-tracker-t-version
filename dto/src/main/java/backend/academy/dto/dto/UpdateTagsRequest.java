package backend.academy.dto.dto;

import backend.academy.dto.validator.annotation.ValidTags;
import java.util.List;

public record UpdateTagsRequest(@ValidTags List<String> tags) {}
