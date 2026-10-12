<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,models.ResultRecord" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Лабораторная работа №3</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style/index.css?v=background-media">
    <script src="<%= request.getContextPath() %>/js/script.js?v=bright-points" defer></script>
    <script src="<%= request.getContextPath() %>/js/widgets.js?v=1" defer></script>
    <script src="<%= request.getContextPath() %>/js/chaos.js?v=1" defer></script>
</head>
<body>
<div class="background-characters" aria-hidden="true">
    <img class="character character-one" src="<%= request.getContextPath() %>/images/character-1-cutout.png" alt="" draggable="false">
    <img class="character character-two" src="<%= request.getContextPath() %>/images/character-2-cutout.png" alt="" draggable="false">
    <img class="character character-three" src="<%= request.getContextPath() %>/images/character-3-cutout.png" alt="" draggable="false">
</div>
<main class="page-content">
<div class="chaos-photos" aria-hidden="true">
    <% for (int photo = 1; photo <= 6; photo++) { %>
    <img class="chaos-photo photo-<%= photo %>" src="<%= request.getContextPath() %>/images/chaos-<%= photo %>.png" alt="" draggable="false">
    <% } %>
    <img class="background-meme" src="<%= request.getContextPath() %>/images/homomorphism-cat.jpg" alt="" draggable="false">
    <% for (int clip = 1; clip <= 7; clip++) { %>
    <video class="background-clip clip-<%= clip %>" autoplay muted loop playsinline preload="metadata" tabindex="-1">
        <source src="<%= request.getContextPath() %>/videos/background-clip-<%= clip %>.mp4" type="video/mp4">
    </video>
    <% } %>
</div>

<div class="top-row">
<header>
    <h1>Лабораторная работа №3</h1>
    <p>Mirzozhanov Nematullo, P3230, вариант: 34313</p>
</header>
<aside class="widget weather-widget">

    <p id="weather-temperature">— °C</p>
    <p id="weather-description" aria-live="polite">Загружаем погоду…</p>
    <p id="weather-wind"></p>
    <div class="weather-footer"><a href="https://open-meteo.com/" target="_blank" rel="noopener noreferrer">Open-Meteo</a><button id="weather-refresh" type="button">Обновить</button></div>
</aside>
</div>

<% if (request.getAttribute("errorMsg") != null) { %>
<p role="alert"><%= request.getAttribute("errorMsg") %></p>
<% } %>



<div class="workbench">
<section class="graph-section">

<canvas id="graph" width="400" height="400"
        data-area-image="<%= request.getContextPath() %>/images/graph-background.png">
    Ваш браузер не поддерживает Canvas.
</canvas>
<aside class="widget clicker-widget">
    <div><p>Нажал — уже молодец.</p><p>Кликов: <output id="clicker-score">0</output></p></div>
    <button id="clicker-button" type="button" aria-label="Кликнуть по фотографии"><img src="<%= request.getContextPath() %>/images/character-1-cutout.png" alt="" draggable="false"></button>
</aside>
</section>

<section class="form-section">

<form id="pointForm" action="<%= request.getContextPath() %>/controller" method="get" novalidate>
    <fieldset id="x-group">
        <div class="field-title">Координата X</div>
        <% for (int x = -5; x <= 3; x++) { %>
        <label>
            <input type="radio" name="x" value="<%= x %>">
            <%= x %>
        </label>
        <% } %>
    </fieldset>

    <fieldset id="y-group">
        <div class="field-title">Координата Y</div>
        <label for="y-coordinate">Число от -3 до 5 включительно:</label>
        <input type="text" name="y" id="y-coordinate" autocomplete="off">
    </fieldset>

    <fieldset id="r-group">
        <div class="field-title">Радиус R</div>
        <label for="r-coordinate">Выберите радиус:</label>
        <select name="r" id="r-coordinate">
            <option value="">Выберите R</option>
            <% for (int r = 1; r <= 5; r++) { %>
            <option value="<%= r %>" <%= String.valueOf(r).equals(request.getParameter("selectedR")) ? "selected" : "" %>><%= r %></option>
            <% } %>
        </select>
    </fieldset>

    <input type="hidden" name="source" value="form">
    <input type="hidden" name="timeZone" id="time-zone">
    <button type="submit">Проверить точку</button>
    <p id="validation-message" role="alert"></p>
</form>
<aside class="widget pokemon-widget">

    <div class="pokemon-content"><img id="pokemon-image" alt="" hidden><div><p id="pokemon-name" aria-live="polite">Ловим покемона…</p><p id="pokemon-info"></p></div></div>
    <button id="pokemon-next" type="button">Другой покемон</button>
    <a class="widget-credit" href="https://pokeapi.co/" target="_blank" rel="noopener noreferrer">PokéAPI</a>
</aside>
</section>
</div>
<div class="arcade-row">
<aside class="widget blackjack-widget">

    <p>Ближе к 21, но без перебора. Туз — 1 или 11.</p>
    <p>Дилер: <span id="dealer-score">?</span></p><div id="dealer-cards" class="playing-cards"></div>
    <p>Ты: <span id="player-score">0</span></p><div id="player-cards" class="playing-cards"></div>
    <p id="blackjack-status" aria-live="polite"></p>
    <div class="game-buttons"><button id="card-hit" type="button">Ещё карту</button><button id="card-stand" type="button">Хватит</button><button id="card-new" type="button">Новая</button></div>
</aside>
<aside class="widget quiz-widget">

    <p>Верных: <output id="quiz-score">0</output> / <output id="quiz-total">0</output></p>
    <p id="quiz-question"></p><div id="quiz-answers"></div>
    <p id="quiz-status" aria-live="polite"></p><button id="quiz-next" type="button">Другой вопрос</button>
</aside>
</div>
<div class="bottom-row">
<section class="history-section">

<div class="history-scroll">
<table id="resultTable">
    <thead>
    <tr>
        <th>X</th>
        <th>Y</th>
        <th>R</th>
        <th>Результат</th>
        <th>Время</th>
        <th>Время выполнения, мс</th>
    </tr>
    </thead>
    <tbody>
    <%
        Object historyObject = application.getAttribute("historyList");
        if (historyObject instanceof List<?>) {
            for (Object item : (List<?>) historyObject) {
                if (!(item instanceof ResultRecord)) continue;
                ResultRecord record = (ResultRecord) item;
    %>
    <tr data-x="<%= record.getX() %>" data-y="<%= record.getY() %>"
        data-r="<%= record.getR() %>" data-hit="<%= record.getIsHit() %>">
        <td><%= record.getX() %></td>
        <td><%= record.getY() %></td>
        <td><%= record.getR() %></td>
        <td><%= record.getIsHit() ? "Попадание" : "Промах" %></td>
        <td><%= record.getCurrentTime() %></td>
        <td><%= record.getExecutionTime() %></td>
    </tr>
    <%
            }
        }
    %>
    </tbody>
</table>
</div>
</section>
<aside class="widget snake-widget">

    <p>Счёт: <output id="snake-score">0</output> · Рекорд: <output id="snake-best">0</output></p>
    <canvas id="snake-game" width="240" height="240" tabindex="0" aria-label="Поле змейки. Управление стрелками после запуска игры."></canvas>
    <p id="snake-status" aria-live="polite">На минутку отвлечься. Управление стрелками.</p>
    <button id="snake-start" type="button">Играть</button>
    <div class="snake-controls" aria-label="Управление змейкой">
        <button type="button" data-direction="up" aria-label="Вверх">↑</button>
        <button type="button" data-direction="left" aria-label="Влево">←</button>
        <button type="button" data-direction="down" aria-label="Вниз">↓</button>
        <button type="button" data-direction="right" aria-label="Вправо">→</button>
    </div>
</aside>
</div>
</main>
</body>
</html>
