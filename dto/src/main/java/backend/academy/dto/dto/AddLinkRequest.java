package backend.academy.dto.dto;

import backend.academy.dto.validator.ValidUrl;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AddLinkRequest(
        @JsonProperty("url") @ValidUrl String url,
        @JsonProperty("tags") @NotNull List<String> tags,
        @JsonProperty("filters") @NotNull List<String> filters) {}
