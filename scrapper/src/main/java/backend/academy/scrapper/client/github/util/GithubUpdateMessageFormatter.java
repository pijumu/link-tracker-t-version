package backend.academy.scrapper.client.github.util;
/*
import backend.academy.scrapper.client.github.dto.GithubUpdateResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class GithubUpdateMessageFormatter {
    private static final String MESSAGE = "Название: %s%nПользователь: %s%nВремя создания: %s%nПревью описания: %s";

    private String formatInstant(Instant instant) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(zonedDateTime);
    }

    public String formMessage(GithubUpdateResponse update) {
        return MESSAGE.formatted(
                update.title(), update.user().name(), formatInstant(update.creationDate()), first200(update.body()));
    }

    private String first200(String text) {
        if (text == null) return "";
        return text.length() <= 200 ? text : text.substring(0, 200);
    }
}
*/
