package backend.academy.scrapper.client.stackoverflow;

import backend.academy.scrapper.client.stackoverflow.util.StackOverflowDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface StackOverflowClient {
    @GetExchange(value = "/questions/{questionId}?key={key}&site={site}", accept = "application/json")
    StackOverflowDto getInfo(@PathVariable("questionId") String questionId);
}
