package backend.academy.scrapper.client.util;

import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.client.UpdateDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import java.util.List;

public interface ClientAdapter {
    UrlType getUrlType();

    List<UpdateDto> getUpdates(UrlInfoDto urlInfoDto);
}
