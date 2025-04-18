package backend.academy.bot;

import backend.academy.bot.service.ScrapperClient;
import com.pengrad.telegrambot.TelegramBot;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record BotConfig(@NotEmpty String telegramToken, @NotNull String scrapperUrl) {

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(telegramToken);
    }

    @Bean
    public ScrapperClient scraperClient() {
        RestClient restClient = RestClient.builder().baseUrl(scrapperUrl).build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(
                        RestClientAdapter.create(restClient))
                .build();

        return httpServiceProxyFactory.createClient(ScrapperClient.class);
    }
}
