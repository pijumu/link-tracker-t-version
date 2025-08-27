package backend.academy.scrapper.client.github.util;

import backend.academy.scrapper.client.github.dto.GithubUpdateResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface GithubClient {
    @GetExchange("/repos/{owner}/{repo}/issues?per_page=5&direction=asc")
    List<GithubUpdateResponse> getLastUpdate(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @RequestParam("since") Instant since);
}
