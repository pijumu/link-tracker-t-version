package backend.academy.scrapper.client.stackoverflow.util;

import backend.academy.scrapper.client.stackoverflow.dto.StackOverflowTitleResponse;
import backend.academy.scrapper.client.stackoverflow.dto.StackOverflowUpdateResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface StackOverflowClient {

    /*
        Если есть вопросы насчёт поля filter. То эти значения позволяют фильтровать поля ответа,
        но эти фильтры захардкожены, и получены при изучении SO API.

        P.S. целый день потратил, чтобы найти оптимальное решение. Стал экспертом SO API)))
        &sort=creation&order=asc
    */
    @GetExchange(
            "questions/{questionId}/comments?site=stackoverflow&sort=creation&order=asc&pagesize=5&filter=!szx-Dsx)YFm7RenuUsIW(gxHfTtAMj8")
    StackOverflowUpdateResponse getComments(
            @PathVariable("questionId") String questionId, @RequestParam("fromdate") Long fromDate);

    @GetExchange(
            "questions/{questionId}/answers?site=stackoverflow&sort=creation&order=asc&pagesize=5&filter=!WWsh2-5LBtfz3iQjzv*iVb*lGN4D)VPVL6K0NHu")
    StackOverflowUpdateResponse getAnswers(
            @PathVariable("questionId") String questionId, @RequestParam("fromdate") Long fromDate);

    @GetExchange("questions/{questionId}?site=stackoverflow&filter=!-tS9ZTnrNw22UJ*9TjRq")
    StackOverflowTitleResponse getTitle(@PathVariable("questionId") String questionId);
}
