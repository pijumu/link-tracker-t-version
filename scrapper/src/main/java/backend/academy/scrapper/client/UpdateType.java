package backend.academy.scrapper.client;

import lombok.Getter;

@Getter
public enum UpdateType {
    GITHUB_ISSUE("Issue:"),
    GITHUB_PULL_REQUEST("Pull request:"),
    STACKOVERFLOW_COMMENT("Комментарий к вопросу:"),
    STACKOVERFLOW_ANSWER("Ответ:");

    private final String description;

    UpdateType(String description) {
        this.description = description;
    }
}
