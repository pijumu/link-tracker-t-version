package backend.academy.dto.dto;

import backend.academy.dto.validator.annotation.ValidFilters;
import backend.academy.dto.validator.annotation.ValidTags;
import backend.academy.dto.validator.annotation.ValidUrl;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AddLinkRequest(
        @JsonProperty("url") @ValidUrl String url,
        @JsonProperty("tags") @ValidTags List<String> tags,
        @JsonProperty("filters") @ValidFilters List<String> filters) {}
