package backend.academy.scrapper.repository.jpa.entity.views;

import java.util.List;

public interface LinkView {
    Long getId();

    UrlOnlyView getUrlEntity();

    List<String> getFilters();

    List<String> getTags();
}
