![Build](https://github.com/central-university-dev/backend-academy-2025-spring-template/actions/workflows/build.yaml/badge.svg)

# Link Tracker

<!-- этот файл можно и нужно менять -->

Проект сделан в рамках курса Академия Бэкенда. Написал Сторожев Юрий Игоревич.

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`. Присутствует опциональная зависимость на `Kafka`.

На данный момент проект можно запустить запустив приложения bot и scrapper в IDEA.
Чтобы опробовать бота в telegram создайтe .env в корне проекта, указав:

```cmd
TELEGRAM_TOKEN=your_telegram_token
GITHUB_TOKEN=your_github_token
SO_TOKEN_KEY=your_stackoverflow_token
```

Для дополнительной справки: [HELP.md](./HELP.md)
