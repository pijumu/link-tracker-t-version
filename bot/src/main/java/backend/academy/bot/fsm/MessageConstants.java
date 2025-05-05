package backend.academy.bot.fsm;

public class MessageConstants {
    // Статические сообщение
    public static final String UNKNOWN_COMMAND = "Неизвестная команда. Используйте /help команду.";
    public static final String NO_FOLLOWING_LINKS_MESSAGE = "Вы не отслеживаете ссылок.";
    public static final String ALREADY_REGISTERED = "Вы уже зарегистрированы.";
    public static final String REGISTERED = "Вы зарегистрированы.";
    public static final String UNAVAILABLE_SERVICE = "Сервис недоступен.";
    public static final String NOT_VALID_URL =
            "Ссылка не соответствует формату валидной ссылки. Используйте /help команду.";
    public static final String SKIP_MESSAGE = "-";
    public static final String SUCCESSFULLY_ADDED = "Ссылка успешна добавлена.";
    public static final String SUCCESSFULLY_REMOVED = "Ссылка успешна удалена.";
    public static final String ERROR_MESSAGE = "Что-то пошло не так.";

    public static final String ENTER_URL = "Введите ссылку.";
    public static final String ENTER_FILTERS = "Введите фильтры через пробел. Используйте '-', если фильтры не нужны.";
    public static final String ENTER_TAGS = "Введите теги через пробел. Используйте '-', если фильтры не нужны.";

    public static final String URL = "url";
    public static final String FILTERS = "filters";
    public static final String TAGS = "tags";

    private MessageConstants() {}
}
