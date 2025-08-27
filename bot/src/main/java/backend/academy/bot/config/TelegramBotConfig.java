package backend.academy.bot.config;

import backend.academy.bot.config.properties.BotProperties;
import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(BotProperties.class)
@RequiredArgsConstructor
public class TelegramBotConfig {
    private final BotProperties botProperties;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botProperties.telegramToken());
    }
}
