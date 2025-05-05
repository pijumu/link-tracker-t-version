package backend.academy.scrapper.client;

import backend.academy.dto.validator.UrlType;
import backend.academy.scrapper.client.util.ClientAdapter;
import backend.academy.scrapper.client.util.UpdateDto;
import backend.academy.scrapper.parser.ParsedUrl;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalClientManager {
    private final ConcurrentMap<UrlType, ClientAdapter> externalClients;

    @Autowired
    public ExternalClientManager(List<ClientAdapter> clients) {
        this.externalClients = new ConcurrentHashMap<>();
        clients.forEach(client -> {
            externalClients.put(client.getUrlType(), client);
        });
    }

    // TODO: some method to get Update but UrlType and params -- DONE damn
    public UpdateDto getUpdate(ParsedUrl parsedUrl) {
        return externalClients.get(parsedUrl.urlType()).getUpdate(parsedUrl.params());
    }
}
