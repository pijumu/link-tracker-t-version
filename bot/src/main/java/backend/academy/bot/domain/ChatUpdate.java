package backend.academy.bot.domain;

public record ChatUpdate(Long chatId, Long urlId, String url, String message) {}
