# Лабораторная работа №3 по веб-программированию

Веб-приложение на сервлетах и JSP проверяет попадание точки в область. Контроллер принимает GET-запросы и передаёт их странице с формой либо сервлету проверки. История хранится в контексте приложения и общая для всех пользователей.

Автор: Mirzozhanov Nematullo, группа P3230, вариант 34313.

## Параметры варианта

- `X` — radio, значения `{-5, -4, -3, -2, -1, 0, 1, 2, 3}`;
- `Y` — текстовое поле, число от `-3` до `5` включительно;
- `R` — select, значения `{1, 2, 3, 4, 5}`;
- HTTP-метод — `GET`.

Область состоит из треугольника во II квадранте (`-R ≤ X ≤ 0`, `0 ≤ Y ≤ X + R`), прямоугольника в I квадранте (`0 ≤ X ≤ R`, `0 ≤ Y ≤ R/2`) и четверти круга в IV квадранте (`X ≥ 0`, `Y ≤ 0`, `X² + Y² ≤ R²`). Границы входят в область.

Клик по графику отправляет вычисленные координаты на сервер. Перед кликом нужно выбрать радиус. Для запросов из формы проверяются допустимые значения варианта; для запросов с `source=canvas` сервер проверяет числовой формат и конечность чисел без ограничений диапазона. Источник запроса и часовой пояс также валидируются.

При смене R график и все сохранённые точки перерисовываются в новом масштабе. Цвет точки определяется для текущего R. В таблице остаётся результат исходной проверки.

## Структура проекта

```text
laba 3/
├── README.md
├── .gitignore
├── task/                         # условие и схема варианта
└── lab3/
    ├── src/main/java/
    │   ├── servlets/             # ControllerServlet, AreaCheckerServlet
    │   ├── utils/                # проверка области и валидация
    │   ├── models/               # ResultRecord
    │   ├── storages/             # история в ServletContext
    │   └── exceptions/
    ├── src/webapp/
    │   ├── index.jsp             # форма, график и история
    │   ├── result.jsp            # результат проверки
    │   ├── WEB-INF/web.xml
    │   ├── style/                # CSS отдельно от JSP
    │   ├── js/                   # график, валидация и виджеты
    │   ├── images/
    │   └── videos/
    ├── gradle/wrapper/
    ├── build.gradle
    ├── settings.gradle
    ├── gradlew
    └── gradlew.bat
```

Результаты сборки, WAR, кэш Gradle, настройки IDE, локальные служебные файлы Git и исходные изображения для подготовки вырезок в репозиторий не добавляются. Используемые страницей картинки и видео входят в WAR и хранятся в репозитории.

## Сборка WAR

Для воспроизводимого запуска рекомендуется JDK 21. Проект использует Gradle Wrapper 9.0.0, Jakarta Servlet API 6.0 и JSP API 3.1. Зависимости Servlet/JSP предоставляет WildFly, они не упаковываются в WAR.

Windows PowerShell из папки `Web-prog`:

```powershell
cd "laba 3\lab3"
.\gradlew.bat clean war
```

Linux/macOS:

```bash
cd "laba 3/lab3"
chmod +x gradlew
./gradlew clean war
```

При первой сборке нужен интернет для загрузки Gradle и зависимостей. После загрузки можно использовать `war --offline`.

Готовый архив:

```text
laba 3/lab3/build/libs/lab3-1.0-SNAPSHOT.war
```

## Локальный запуск в WildFly

Проверенная конфигурация: JDK 21 и WildFly 41.0.1.Final в standalone-режиме. WildFly распаковывается из архива; путь к серверу ниже нужно заменить своим.

### 1. Задать пути

В PowerShell, находясь в `laba 3/lab3`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$wildflyPath = "C:\servers\wildfly-41.0.1.Final"
```

### 2. Разместить WAR

```powershell
Copy-Item -LiteralPath ".\build\libs\lab3-1.0-SNAPSHOT.war" -Destination "$wildflyPath\standalone\deployments\lab3-1.0-SNAPSHOT.war" -Force
```

### 3. Запустить сервер

```powershell
& "$wildflyPath\bin\standalone.bat" -b 127.0.0.1 "-Djboss.socket.binding.port-offset=1"
```

Смещение `1` даёт HTTP-порт `8081` вместо стандартного `8080` и порт управления `9991` вместо `9990`. Оно используется для локального запуска, чтобы не занимать уже используемый порт 8080.

### 4. Открыть приложение

```text
http://localhost:8081/lab3-1.0-SNAPSHOT/controller
```

`lab3-1.0-SNAPSHOT` — контекст приложения, полученный из имени WAR, `/controller` — адрес контроллера. Если переименовать архив, контекст тоже изменится. Адрес `http://localhost:8081/controller` без контекста вернёт 404.

Проверка серверного запроса:

```powershell
Invoke-WebRequest "http://localhost:8081/lab3-1.0-SNAPSHOT/controller?x=0&y=0&r=2&source=form&timeZone=Europe%2FMoscow"
```

## Развёртывание на Helios

Нужно загрузить и распаковать WildFly в домашнем каталоге и иметь доступный JDK 21. `JAVA_HOME` должен указывать на установленный на сервере JDK. В примерах используется учётная запись `s502589`; замените её своей при необходимости.

После сборки, из `Web-prog` на локальном компьютере:

```powershell
scp -P 2222 ".\laba 3\lab3\build\libs\lab3-1.0-SNAPSHOT.war" s502589@helios.cs.ifmo.ru:/home/studs/s502589/wildfly-41.0.1.Final/standalone/deployments/
```

Подключиться к серверу:

```bash
ssh -p 2222 s502589@helios.cs.ifmo.ru
java -version
```

Запустить standalone с HTTP listener на всех IPv4-адресах и смещением портов. `50258` ниже — пример HTTP-portbase: замените его значением, выданным преподавателем.

```bash
cd ~/wildfly-41.0.1.Final
portbase=50258
portoffset=$((portbase - 8080))
nohup ./bin/standalone.sh -b 0.0.0.0 -bmanagement 127.0.0.1 \
  -Djboss.socket.binding.port-offset="$portoffset" > wildfly-console.log 2>&1 &
```

При стандартной конфигурации HTTP слушает `portbase`, HTTPS — `portbase + 363`, управление — `portbase + 1910`. Смещение применяется ко всем привязкам портов standalone-конфигурации. Убедитесь, что полученные порты разрешены для вашей учётной записи и свободны; если преподаватель выдал отдельную схему портов, настройте её в `standalone/configuration/standalone.xml`.

Проверить запуск и развёртывание:

```bash
tail -n 50 standalone/log/server.log
ls standalone/deployments/
curl -I "http://localhost:${portbase}/lab3-1.0-SNAPSHOT/controller"
```

В браузере при HTTP-portbase 50258:

```text
http://helios.cs.ifmo.ru:50258/lab3-1.0-SNAPSHOT/controller
```

Если прямой доступ недоступен, открыть туннель на локальном компьютере:

```bash
ssh -p 2222 -L 8081:localhost:50258 s502589@helios.cs.ifmo.ru
```

Тогда сайт доступен по локальному адресу из предыдущего раздела. Для туннеля локальный порт 8081 должен быть свободен.

## Остановка и обновление

Для локального сервера, запущенного в терминале, — `Ctrl+C`. Также можно завершить его через CLI:

```powershell
& "$wildflyPath\bin\jboss-cli.bat" --connect --controller=127.0.0.1:9991 --command=":shutdown"
```

Для примера Helios с HTTP-portbase 50258 порт управления равен 52168:

```bash
~/wildfly-41.0.1.Final/bin/jboss-cli.sh --connect --controller=127.0.0.1:52168 --command=:shutdown
```

После изменения Java, JSP, JS, CSS или медиа нужно пересобрать WAR и заменить его в `standalone/deployments`. WildFly обнаруживает обновление архива автоматически. При повторном развёртывании или перезапуске общая история очищается: она хранится в памяти приложения.

## Виджеты

- Погода в Санкт-Петербурге — [Open-Meteo](https://open-meteo.com/), кэш в браузере на 10 минут.
- Случайный покемон из первых 151 — [PokéAPI](https://pokeapi.co/docs/v2), ответы API кэшируются локально.
- Кликер и змейка — счёт кликов и рекорд змейки сохраняются в `localStorage`.
- Мини-21 — локальная колода из 52 карт, туз считается как 1 или 11, дилер берёт карты до 17.
- Викторина — локальные вопросы на русском.
- Фоновые видео — без звука, с повтором; браузер может приостанавливать их вне видимой части страницы.

Погода и покемон требуют доступа к внешним API. При ошибке загрузки можно повторить запрос кнопкой. Проверка точек и локальные игры работают независимо от этих API.

## Диагностика

- `404` — проверьте имя WAR в URL, `/controller` и статус развёртывания.
- `Address already in use` — выбранный порт занят; остановите конфликтующий процесс либо смените смещение.
- Файл `.failed` рядом с WAR — прочитайте его и `standalone/log/server.log`.
- Файл `.deployed` — архив принят сервером.
- При ошибке числовых параметров приложение возвращает форму с сообщением валидации.

Документация: [WildFly Getting Started](https://www.wildfly.org/get-started/), [WildFly Admin Guide](https://docs.wildfly.org/41/Admin_Guide.html).

## Материалы задания

- [`task/УСЛОВИЕ.md`](task/УСЛОВИЕ.md) — полное условие;
- [`task/variant-area.png`](task/variant-area.png) — исходная схема области.
