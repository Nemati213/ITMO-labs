# Лабораторная работа №2 по веб-программированию

Веб-приложение проверяет попадание точки в область. Статические файлы раздаёт Apache httpd, а AJAX-запросы перенаправляются Java FastCGI-серверу. Сервер валидирует параметры и возвращает JSON с результатом, текущим временем и временем выполнения. История запросов хранится в `localStorage` браузера.

Автор: Mirzozhanov Nematullo, группа P3230, вариант 23456.

## Параметры варианта

- `X` — одно из значений `{-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2}`;
- `Y` — число от `-3` до `3` включительно;
- `R` — число от `2` до `5` включительно;
- HTTP-метод — `GET`.

## Структура проекта

```text
laba 2/
├── httpd-conf-template.conf  # шаблон конфигурации Apache
├── task/                     # условие и схема варианта
├── README.md
└── laba2/
    ├── src/main/java/        # Java FastCGI-сервер
    ├── libs/fastcgi-lib.jar  # библиотека FastCGI
    ├── web/                  # HTML, JavaScript, изображения и звуки
    ├── build.gradle
    ├── gradlew
    └── gradlew.bat
```

Персональный `httpd.conf`, логи, PID-файлы, IDE-файлы и результаты сборки в репозиторий не добавляются.

## Сборка JAR

Требуется JDK 17 или новее.

Windows PowerShell:

```powershell
cd "laba 2\laba2"
.\gradlew.bat clean jar
```

Linux/macOS:

```bash
cd "laba 2/laba2"
./gradlew clean jar
```

Готовый fat JAR со всеми необходимыми классами будет создан здесь:

```text
laba 2/laba2/build/libs/lab2.jar
```

## Развёртывание на Helios

В примерах используются:

- учётная запись: `s502589`;
- HTTP-порт Apache: `50258`;
- внутренний порт FastCGI: `50259`.

Если эти порты заняты или преподаватель выдал другие, нужно использовать другую пару во всех командах и в конфигурации.

### 1. Подготовить каталоги

Подключиться к Helios:

```bash
ssh -p 2222 s502589@helios.cs.ifmo.ru
```

Создать каталоги:

```bash
mkdir -p ~/httpd-root/htdocs
mkdir -p ~/httpd-root/fcgi-bin
mkdir -p ~/httpd-root/mutex-dir
```

Проверить, свободны ли выбранные порты:

```bash
sockstat -l | grep -E '50258|50259'
```

Если команда ничего не вывела, в этот момент порты свободны.

### 2. Загрузить файлы

После сборки нужно получить на сервере такую структуру:

```text
/home/studs/s502589/httpd-root/
├── httpd-conf-template.conf
├── mutex-dir/
├── htdocs/
│   ├── index.html
│   ├── script.js
│   ├── images/
│   └── sounds/
└── fcgi-bin/
    └── lab2.jar
```

Пример загрузки из PowerShell, запущенного в корне репозитория:

```powershell
scp -P 2222 -r ".\laba 2\laba2\web\." s502589@helios.cs.ifmo.ru:/home/studs/s502589/httpd-root/htdocs/
scp -P 2222 ".\laba 2\laba2\build\libs\lab2.jar" s502589@helios.cs.ifmo.ru:/home/studs/s502589/httpd-root/fcgi-bin/
scp -P 2222 ".\laba 2\httpd-conf-template.conf" s502589@helios.cs.ifmo.ru:/home/studs/s502589/httpd-root/
```

### 3. Создать персональный `httpd.conf`

На Helios скопировать шаблон:

```bash
cp ~/httpd-root/httpd-conf-template.conf ~/httpd-root/httpd.conf
```

Открыть полученный файл любым текстовым редактором и выполнить замены:

| В шаблоне | Для этого проекта |
|---|---|
| `sXXXXXX` | `s502589` |
| `Listen 24000` | `Listen 50258` |
| `ServerName helios.cs.ifmo.ru:24000` | `ServerName helios.cs.ifmo.ru:50258` |
| `DocumentRoot "/var/www"` | `DocumentRoot "/home/studs/s502589/httpd-root/htdocs"` |
| `<Directory "/var/www">` | `<Directory "/home/studs/s502589/httpd-root/htdocs">` |
| `hello-world.jar` | `lab2.jar` |
| `-host localhost:24001` | `-host localhost:50259` |

После замены важные строки должны выглядеть так:

```apache
Listen 50258
ServerName helios.cs.ifmo.ru:50258

DocumentRoot "/home/studs/s502589/httpd-root/htdocs"
<Directory "/home/studs/s502589/httpd-root/htdocs">
    Options FollowSymLinks
    AllowOverride None
    Require all granted
</Directory>

FastCgiExternalServer "/home/studs/s502589/httpd-root/fcgi-bin/lab2.jar" -host localhost:50259 -nph
Alias /fcgi-bin/ "/home/studs/s502589/httpd-root/fcgi-bin/"
```

Все пути к `mutex-dir`, `error.log`, `access.log` и `httpd.pid` также должны содержать `/home/studs/s502589/httpd-root/`.

Выдать права на чтение файлов:

```bash
chmod 755 ~/httpd-root ~/httpd-root/htdocs ~/httpd-root/fcgi-bin ~/httpd-root/mutex-dir
chmod -R a+rX ~/httpd-root/htdocs ~/httpd-root/fcgi-bin
```

### 4. Запустить Java FastCGI

Проверить версию Java:

```bash
java -version
```

Запустить сервер в фоне:

```bash
cd ~/httpd-root
nohup java -DFCGI_PORT=50259 -jar fcgi-bin/lab2.jar > java.log 2>&1 &
echo $! > java.pid
```

Проверить процесс и порт:

```bash
cat ~/httpd-root/java.pid
sockstat -l | grep 50259
tail -n 30 ~/httpd-root/java.log
```

### 5. Запустить Apache

Сначала проверить конфигурацию:

```bash
/usr/local/sbin/httpd -t -f ~/httpd-root/httpd.conf
```

Ожидаемый результат: `Syntax OK`.

Запустить Apache:

```bash
/usr/local/sbin/httpd -f ~/httpd-root/httpd.conf -k start
```

Проверить HTTP-порт:

```bash
sockstat -l | grep 50258
```

### 6. Проверить приложение

Проверка статической страницы с самого Helios:

```bash
curl -I http://localhost:50258/
```

Проверка FastCGI-запроса:

```bash
curl "http://localhost:50258/fcgi-bin/lab2.jar?x=0&y=0&r=3"
```

В браузере приложение открывается по адресу:

```text
http://helios.cs.ifmo.ru:50258/
```

Если прямой доступ к порту недоступен, на локальном компьютере можно открыть SSH-туннель:

```bash
ssh -p 2222 -L 8080:localhost:50258 s502589@helios.cs.ifmo.ru
```

Пока это соединение открыто, сайт будет доступен по адресу `http://localhost:8080/`.

## Остановка и повторный запуск

Остановить Apache:

```bash
/usr/local/sbin/httpd -f ~/httpd-root/httpd.conf -k stop
```

Остановить Java:

```bash
kill "$(cat ~/httpd-root/java.pid)"
```

После изменения только HTML, JavaScript, изображений или звуков достаточно повторно загрузить файлы в `htdocs`. После замены JAR нужно перезапустить Java. После изменения `httpd.conf` нужно перезапустить Apache:

```bash
/usr/local/sbin/httpd -f ~/httpd-root/httpd.conf -k restart
```

## Диагностика

Логи Apache:

```bash
tail -n 50 ~/httpd-root/error.log
```

Логи Java:

```bash
tail -n 50 ~/httpd-root/java.log
```

Проверка запущенных процессов:

```bash
ps aux | grep -E '[h]ttpd|[l]ab2.jar'
```

Если статика открывается, а запросы возвращают `503`, чаще всего Java не запущена либо порт в `-DFCGI_PORT` не совпадает с портом в `FastCgiExternalServer`.

## Материалы задания

- [`task/УСЛОВИЕ.md`](task/УСЛОВИЕ.md) — полное условие;
- [`task/variant-area.png`](task/variant-area.png) — исходная схема области.
