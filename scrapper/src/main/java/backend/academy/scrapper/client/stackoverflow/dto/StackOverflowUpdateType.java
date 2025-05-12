package backend.academy.scrapper.client.stackoverflow.dto;

import lombok.Getter;

@Getter
public enum StackOverflowUpdateType {
    COMMENT("Комментарий к вопросу."),
    ANSWER("Ответ к вопросу.");

    private final String description;

    StackOverflowUpdateType(String description) {
        this.description = description;
    }
}
