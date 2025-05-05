package backend.academy.scrapper.test.component;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.dto.dto.AddLinkRequest;
import backend.academy.dto.dto.RemoveLinkRequest;
import backend.academy.scrapper.controller.LinkController;
import backend.academy.scrapper.domain.ILinkRepository;
import backend.academy.scrapper.repository.InMemoryLinkRepository;
import backend.academy.scrapper.service.LinkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@WebMvcTest(LinkController.class)
@EnableWebMvc
@Import({
    LinkService.class,
    InMemoryLinkRepository.class,
})
@ActiveProfiles("api-test")
public class ApiUsageScenarioTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ILinkRepository linkRepository;

    @Test
    @DisplayName("Добавление ссылки для отслеживания. Позитивный сценарий")
    void test1() throws Exception {
        // Arrange
        long chatId = 123L;
        AddLinkRequest addLinkRequest =
                new AddLinkRequest("https://github.com/pjumu/Asl-Recognition-Model", List.of(), List.of("filter1"));
        String json = objectMapper.writeValueAsString(addLinkRequest);

        // Act + Assert
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        Assertions.assertEquals(
                linkRepository.getLinksForChatId(chatId).getFirst().url(),
                "https://github.com/pjumu/Asl-Recognition-Model");
        Assertions.assertEquals(
                linkRepository.getLinksForChatId(chatId).getFirst().tags(), List.of());
        Assertions.assertEquals(
                linkRepository.getLinksForChatId(chatId).getFirst().filters(), List.of("filter1"));
    }

    @Test
    @DisplayName("Добавление дубликата. Негативный сценарий")
    void test2() throws Exception {
        // Arrange
        long chatId = 123L;
        AddLinkRequest addLinkRequest = new AddLinkRequest(
                "https://github.com/pjumu/Asl-Recognition-Model", List.of("tag1"), List.of("filter1, filter2"));
        String json = objectMapper.writeValueAsString(addLinkRequest);

        // Act + Assert
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Удаление ссылки. Позитивный сценарий")
    void test3() throws Exception {
        // Arrange
        long chatId = 123L;
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest("https://github.com/pjumu/Asl-Recognition-Model");
        String json = objectMapper.writeValueAsString(removeLinkRequest);

        // Act + Assert
        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
        Assertions.assertEquals(linkRepository.getLinksForChatId(chatId).size(), 0);
    }

    @Test
    @DisplayName("Добавление дубликата. Негативный сценарий")
    void test4() throws Exception {
        // Arrange
        long chatId = 123L;
        AddLinkRequest addLinkRequest = new AddLinkRequest(
                "https://invalid.com/who/Asl-Recognition-Model", List.of("tag1"), List.of("filter1, filter2"));
        String json = objectMapper.writeValueAsString(addLinkRequest);

        // Act + Assert
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
