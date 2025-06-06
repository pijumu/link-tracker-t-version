package backend.academy.scrapper.service.checker;

import backend.academy.scrapper.client.UpdateDto;
import backend.academy.scrapper.domain.dto.UpdateWithMessageDto;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MessageFormatterService {
    private static final String MESSAGE = "%s %s%n" + "Пользователь: %s%n" + "Время создания: %s%n" + "Превью: %s";

    public static String formatInstant(Instant instant) {
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(zonedDateTime);
    }

    public UpdateWithMessageDto formUpdateMessageDto(UpdateDto updateDto, List<Long> chatIds) {
        return new UpdateWithMessageDto(
                updateDto.urlId(),
                updateDto.url(),
                updateDto.createdAt(),
                MESSAGE.formatted(
                        updateDto.preview().getLeft().description(),
                        updateDto.topic(),
                        updateDto.user(),
                        formatInstant(updateDto.createdAt()),
                        updateDto.preview().getRight()),
                chatIds);
    }
}
