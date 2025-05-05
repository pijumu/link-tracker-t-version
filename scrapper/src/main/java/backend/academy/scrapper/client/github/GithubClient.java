package backend.academy.scrapper.client.github;

import backend.academy.scrapper.client.github.util.GithubDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface GithubClient {
    @GetExchange(value = "/repos/{owner}/{repo}", accept = "application/json")
    GithubDto getInfo(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}
