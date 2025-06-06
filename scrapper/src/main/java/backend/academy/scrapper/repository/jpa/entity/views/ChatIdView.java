package backend.academy.scrapper.repository.jpa.entity.views;

import java.util.List;

public interface ChatIdView {
    Long getChatId();

    List<String> getFilters();
}
