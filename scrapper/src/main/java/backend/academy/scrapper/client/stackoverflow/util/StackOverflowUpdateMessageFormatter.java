package backend.academy.scrapper.client.stackoverflow.util;

import backend.academy.scrapper.client.stackoverflow.dto.StackOverflowUpdateResponse;
import backend.academy.scrapper.client.stackoverflow.dto.StackOverflowUpdateType;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class StackOverflowUpdateMessageFormatter {
    private static final String MESSAGE = "Вопрос: %s%nПользователь: %s%nВремя создания: %s%n%s Превью: %s";

    public static String formatInstant(Instant instant) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(zonedDateTime);
    }

    public String formMessage(StackOverflowUpdateResponse.Update update, String title, StackOverflowUpdateType type) {
        return MESSAGE.formatted(
                title,
                update.owner().name(),
                formatInstant(update.creationDate()),
                type.description(),
                first200(update.body()));
    }

    public static String first200(String text) {
        if (text == null) return "";
        return text.length() <= 200 ? text : text.substring(0, 200);
    }
}
