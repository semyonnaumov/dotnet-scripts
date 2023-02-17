# dotnet-scripts

Service for running С# scripts in an isolated environment. Consists of two services:

1. Scheduler - [dotnet-scripts-scheduler](https://github.com/semyonnaumov/dotnet-scripts-scheduler)
2. Worker - [dotnet-scripts-worker](https://github.com/semyonnaumov/dotnet-scripts-worker)

For detailed sub-service description address related sub-service repository.

⚠️ Full description is currently available only in Russian.

## 1. Требования к сервису

Сервис, выполняющий C# скрипты и возвращающий результаты выполнения. Для простоты можно предположить, что это онлайн
интерпретатор, задача которого - выполнить скрипт и дать пользователю возможность обратиться за результатом скрипта.

- Результаты выполнения скрипта - STDOUT + STDERR
- Выполнение скрипта единичное, без поддержания сессии
- Использовать одну ОС и платформу с возможностью расширения в API и архитектуре
- Security часть с аутентификацией и авторизацией опустить
- Скрипт отправляется по сети и не параметризуется
- Выполнение скрипта должно быть изолировано, с ограничением использования ресурсов, он должен запускаться безопасно для
  его окружения
    - Лимиты для скрипта на CPU/RAM/Disk/Network
    - Скрипт должен иметь возможность обращаться к диску без доступа к ФС хоста; перед запуском скрипта диск должен быть
      чистым
    - Скрипт может использовать сеть для загрузки NuGet-пакетов через NuGet Feeds и для совершения произвольных
      запросов: в API должна быть заложена возможность получения конфигов для доступа к NuGet фидам при инициализации
      песочницы
    - Должен быть заложен таймаут на выполнение скрипта
    - Ошибки рантайма скрипта должны правильно обрабатываться
    - Сервис должен быть защищен от инъекций
- Должно поддерживаться параллельное выполнение нескольких скриптов
- Сервис должен иметь возможность горизонтального масштабирования
- Должны присутствовать модульные и интеграционные тесты

## 2. Дизайн

Сервис представляет собой два приложения -
[dotnet-scripts-scheduler](https://github.com/semyonnaumov/dotnet-scripts-scheduler) и
[dotnet-scripts-worker](https://github.com/semyonnaumov/dotnet-scripts-worker), общающиеся между
собой через Kafka. **dotnet-scripts-scheduler** имеет RESTful API для общения с клиентом, **dotnet-scripts-worker**
отвечает непосредственно за запуск задач. Схематично это выглядит так:

![Service schema](dotnet-scripts-schema.png)

Модули сервиса находятся в отдельных репозиториях, детальное описание их работы и запуска можно найти там:

1. Планировщик - [dotnet-scripts-scheduler](https://github.com/semyonnaumov/dotnet-scripts-scheduler)
2. Воркер - [dotnet-scripts-worker](https://github.com/semyonnaumov/dotnet-scripts-worker)

## 3. Совместный запуск модулей

### 3.1 Запуск приложений напрямую

Для запуска необходимо сначала поднять обвязку с кафкой и базой данных при помощи docker compose **планировщика**
[dotnet-scripts-scheduler](https://github.com/semyonnaumov/dotnet-scripts-scheduler):

```bash
cd dotnet-scripts-scheduler
docker compose up -d
```

После этого можно запускать приложения:

```bash
cd dotnet-scripts-scheduler
./gradlew bootRun
```

Запускать **dotnet-scripts-worker** следует, указав в переменной окружения `WORKER_JOB_FILES_HOST_DIR` директорию,
которую приложение будет использовать для создания временных файлов. По умолчанию это `/tmp/scripts`.
После этого можно собрать и запустить приложение:

```bash
export WORKER_JOB_FILES_HOST_DIR=$HOME/tmp
./gradlew bootRun
```

⚠️ Внимание! Для запуска приложения на Windows нужно задать переменную
окружения `WORKER_DOCKER_HOST=tcp://localhost:2376`

⚠️ При таком запуске приложение будет использовать локальный докер для поднятия контейнеров со скриптами.

### 3.2 Запуск всех приложений помощью docker compose

Для запуска всего сервиса нужно использовать `docker-compose.yaml` из текущего репозитория:

```bash
docker compose up -d
```

После этого сервис будет доступен на `localhost:8080` (например, Swagger UI: http://localhost:8080/swagger-ui)

## 4. Взаимодействие с сервисом

Примеры запросов к сервису:

1. Отправка скрипта на выполнение:

    ```bash
    curl --request POST 'localhost:8080/jobs' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "requestId": "request-1",
        "senderId": "sender-1",
        "payload": {
            "script": "Console.WriteLine(\"Hello from from job from sender-1\");",
            "jobConfig": {
                "nugetConfigXml": "<?xml version=\"1.0\" encoding=\"utf-8\"?><configuration><packageSources><add key=\"NuGet official package source\" value=\"https://nuget.org/api/v2/\" /></packageSources><activePackageSource><add key=\"All\" value=\"(Aggregate source)\" /></activePackageSource></configuration>"
            },
            "agentType": "linux-amd64-dotnet-7"
        }
    }'
    ```

   Сервис вернет идентификатор созданной джобы:

    ```json
    {
        "jobId": "c0a86402-8646-18b3-8186-4688df130000"
    }
    ```

   ⚠️ Внимание! Код ответа `200` означает, что отправленная джоба уже была создана ранее, и еще раз запускаться не
   будет, потому что найден имеющийся запуск с таким же `requestId`. Для того, чтобы джоба создалась, нужен
   новый `requestId`. Код ответа `201` свидетельствует о создании новой джобы.


2. Получение полной информации о созданной джобе (нужно указать правильный идентификатор):

    ```bash
    curl --request GET 'localhost:8080/jobs/c0a86402-8646-18b3-8186-4688df130000'
    ```

3. Удаление:

    ```bash
    curl --request DELETE 'localhost:8080/jobs/c0a86402-8646-18b3-8186-4688df130000'
    ```