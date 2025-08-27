package backend.academy.dto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RemoveLinkRequest(@JsonProperty("url") String url) {}
