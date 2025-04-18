package backend.academy.scrapper.client;

import backend.academy.dto.validator.UrlType;
import backend.academy.scrapper.parser.ParsedUrl;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalClientManager {
    private final Map<UrlType, ExternalClient> externalClients;

    @Autowired
    public ExternalClientManager(List<ExternalClient> clients) {
        this.externalClients = clients.stream().collect(Collectors.toMap(ExternalClient::getUrlType, client -> client));
    }

    public Notifier getNotifier(ParsedUrl parsedUrl) {
        return externalClients.get(parsedUrl.urlType()).fetchData(parsedUrl.params());
    }
}
