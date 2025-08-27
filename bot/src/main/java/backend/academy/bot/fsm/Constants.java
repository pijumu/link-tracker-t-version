package backend.academy.bot.fsm;

public class Constants {
    public static final String UNKNOWN_COMMAND = "Неизвестная команда. Используйте /help команду.";
    public static final String NO_FOLLOWING_LINKS_MESSAGE = "Вы не отслеживаете ссылок.";
    public static final String NO_FOLLOWING_LINKS_MESSAGE_WITH_TAGS = "Вы не отслеживаете ссылок с тегами:%n%s";
    public static final String ALREADY_REGISTERED = "Вы уже зарегистрированы.";
    public static final String REGISTERED = "Вы зарегистрированы.";
    public static final String UNAVAILABLE_SERVICE = "Сервис недоступен.";
    public static final String SUCCESSFULLY_ADDED = "Ссылка успешна добавлена.";
    public static final String SUCCESSFULLY_REMOVED = "Ссылка успешна удалена.";
    public static final String SUCCESSFULLY_TAGS_UPDATED = "Теги успешно обновлены.";
    public static final String ERROR_MESSAGE = "Что-то пошло не так.";

    public static final String ENTER_URL = "Введите ссылку.";
    public static final String ENTER_FILTERS =
            "Введите фильтры через пробел. Фильтров должно быть не больше 3. Используйте /skip, если фильтры не нужны.";
    public static final String ENTER_TAGS =
            "Введите теги через пробел. Тегов должно быть не больше 3. Используйте /skip, если теги не нужны.";

    public static final String NO_URL = "Вы не отслеживаете ссылку %s!";
    public static final String SKIP = "/skip";
    public static final String CANCELED = "Переход в меню команд.";

    public static final String RETRY_INPUT = "Ошибка! %s Введите ещё раз!";

    public static final String COMMAND = "command";
    public static final String URL = "url";
    public static final String FILTERS = "filters";
    public static final String TAGS = "tags";
    public static final String LINK_ID = "linkId";

    private Constants() {}

    public static String repeatInput(String message) {
        return RETRY_INPUT.formatted(message);
    }

    public static String repeatUrl(String message) {
        return RETRY_INPUT.formatted(NO_URL.formatted(message));
    }
}
