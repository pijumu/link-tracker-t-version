package backend.academy.scrapper.client.stackoverflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StackOverflowTitleResponse(List<Title> items) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Title(String title) {}
}
