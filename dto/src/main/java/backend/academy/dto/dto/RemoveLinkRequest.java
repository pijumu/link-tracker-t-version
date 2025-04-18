package backend.academy.dto.dto;

import backend.academy.dto.validator.ValidUrl;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RemoveLinkRequest(@JsonProperty("url") @ValidUrl String url) {}
