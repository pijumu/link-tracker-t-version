package backend.academy.scrapper.service.notification;

import backend.academy.dto.dto.LinkUpdateDto;
import java.util.List;

public interface NotificationService {
    void notify(List<LinkUpdateDto> updates);
}
