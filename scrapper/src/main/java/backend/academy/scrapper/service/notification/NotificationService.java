package backend.academy.scrapper.service.notification;

import backend.academy.dto.dto.LinkUpdateDto;
import backend.academy.scrapper.domain.dto.UpdateWithMessageDto;
import java.util.List;

public interface NotificationService {
    void notify(List<List<UpdateWithMessageDto>> updates);

    static LinkUpdateDto toLinkUpdateDto(UpdateWithMessageDto link) {
        return new LinkUpdateDto(link.urlId(), link.url(), link.description(), link.tgChatIds());
    }
}
