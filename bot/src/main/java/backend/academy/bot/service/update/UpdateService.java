package backend.academy.bot.service.update;

import backend.academy.dto.dto.LinkUpdateDto;

public interface UpdateService {
    void handleUpdate(LinkUpdateDto update);
}
