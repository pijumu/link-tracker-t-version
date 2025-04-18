package backend.academy.scrapper.client;

import backend.academy.dto.validator.UrlType;
import java.util.Map;

public interface ExternalClient {
    Notifier fetchData(Map<String, String> params);

    UrlType getUrlType();
}
