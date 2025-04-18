package backend.academy.scrapper.client;

import java.time.Instant;

public interface Notifier {
    Instant getFormattedTime();

    String getMessage();
}
