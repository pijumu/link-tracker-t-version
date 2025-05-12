package backend.academy.scrapper.client.util;

import backend.academy.dto.validator.util.UrlType;
import backend.academy.scrapper.domain.dto.UpdateInfoDto;
import backend.academy.scrapper.domain.dto.UrlInfoDto;
import java.util.Optional;

public interface ClientAdapter {
    UrlType getUrlType();

    Optional<UpdateInfoDto> getUpdate(UrlInfoDto urlInfoDto);
}
