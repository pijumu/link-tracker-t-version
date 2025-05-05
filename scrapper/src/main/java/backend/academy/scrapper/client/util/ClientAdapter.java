package backend.academy.scrapper.client.util;

import backend.academy.dto.validator.UrlType;
import java.util.Map;

public interface ClientAdapter {
    UpdateDto getUpdate(Map<String, String> params);

    UrlType getUrlType();
}
