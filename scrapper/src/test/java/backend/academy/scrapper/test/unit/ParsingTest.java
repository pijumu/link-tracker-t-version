package backend.academy.scrapper.test.unit;

import backend.academy.dto.validator.UrlType;
import backend.academy.scrapper.parser.ParsedUrl;
import backend.academy.scrapper.parser.UrlParser;
import backend.academy.scrapper.parser.parsers.GithubUrlParser;
import backend.academy.scrapper.parser.parsers.StackOverflowUrlParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ParsingTest {
    private static UrlParser githubParser;
    private static UrlParser stackOverflowParser;

    @BeforeAll
    static void init() {
        // Arrange
        githubParser = new GithubUrlParser();
        stackOverflowParser = new StackOverflowUrlParser();
    }

    @Test
    @DisplayName("Проверка github парсера. Позитивный сценарий.")
    void test1() {
        // Act
        ParsedUrl parsedUrl = githubParser.parse("https://github.com/pijumu/ASL-Recognition-Model");

        // Assert
        Assertions.assertEquals(parsedUrl.urlType(), UrlType.GITHUB);
        Assertions.assertTrue(parsedUrl.params().containsKey("owner"), "В параметры должен выделяться ключ owner");
        Assertions.assertTrue(parsedUrl.params().containsKey("repo"), "В параметры должен выделяться ключ repo");

        Assertions.assertEquals(parsedUrl.params().get("owner"), "pijumu");
        Assertions.assertEquals(parsedUrl.params().get("repo"), "ASL-Recognition-Model");
    }

    @Test
    @DisplayName("Проверка github парсера. Негативный сценарий.")
    void test2() {
        // Arrange
        String invalidUrl = "https://github.com/piju,/mu/ASL-Recognition-Model";

        // Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> githubParser.parse(invalidUrl));
    }

    @Test
    @DisplayName("Проверка stackoverflow парсера. Позитивный сценарий.")
    void test3() {
        // Act
        ParsedUrl parsedUrl = stackOverflowParser.parse("https://stackoverflow.com/questions/127");

        // Assert
        Assertions.assertEquals(parsedUrl.urlType(), UrlType.STACKOVERFLOW);
        Assertions.assertTrue(
                parsedUrl.params().containsKey("questionId"), "В параметры должен выделяться ключ questionId");
        Assertions.assertEquals(parsedUrl.params().get("questionId"), "127");
    }

    @Test
    @DisplayName("Проверка stackoverflow парсера. Негативный сценарий.")
    void test4() {
        // Arrange
        String invalidUrl = "https://stackoverflow.com/questions/1ub";

        // Act + Assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> stackOverflowParser.parse(invalidUrl));
    }
}
