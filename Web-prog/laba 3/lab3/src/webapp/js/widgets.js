(() => {
    function readNumber(key) {
        try {
            const value = Number(localStorage.getItem(key));
            return Number.isSafeInteger(value) && value >= 0 ? value : 0;
        } catch {
            return 0;
        }
    }

    function saveNumber(key, value) {
        try {
            localStorage.setItem(key, String(value));
        } catch {}
    }

    const clickerScore = document.getElementById('clicker-score');
    let clicks = readNumber('lab3-clicks');
    clickerScore.textContent = clicks;
    document.getElementById('clicker-button').addEventListener('click', () => {
        clicks++;
        clickerScore.textContent = clicks;
        saveNumber('lab3-clicks', clicks);
    });

    const temperature = document.getElementById('weather-temperature');
    const description = document.getElementById('weather-description');
    const wind = document.getElementById('weather-wind');
    const refresh = document.getElementById('weather-refresh');

    function weatherName(code) {
        if (code === 0) return 'Ясно';
        if (code <= 2) return 'Переменная облачность';
        if (code === 3) return 'Пасмурно';
        if (code === 45 || code === 48) return 'Туман';
        if (code >= 51 && code <= 57) return 'Морось';
        if (code >= 61 && code <= 67) return 'Дождь';
        if (code >= 71 && code <= 77) return 'Снег';
        if (code >= 80 && code <= 82) return 'Ливень';
        if (code === 85 || code === 86) return 'Снегопад';
        if (code >= 95) return 'Гроза';
        return 'Погода за окном';
    }

    function showWeather(current) {
        if (!current || !Number.isFinite(current.temperature_2m) ||
            !Number.isFinite(current.wind_speed_10m) || !Number.isFinite(current.weather_code)) {
            throw new Error('Invalid weather');
        }
        temperature.textContent = `${Math.round(current.temperature_2m)} °C`;
        description.textContent = weatherName(current.weather_code);
        wind.textContent = `Ветер: ${current.wind_speed_10m.toFixed(1)} м/с`;
    }

    async function loadWeather(useCache) {
        if (useCache) {
            try {
                const cached = JSON.parse(localStorage.getItem('lab3-weather-petersburg'));
                if (cached && Date.now() - cached.time < 600000) {
                    showWeather(cached.current);
                    return;
                }
            } catch {}
        }
        refresh.disabled = true;
        description.textContent = 'Загружаем погоду…';
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 8000);
        try {
            const response = await fetch('https://api.open-meteo.com/v1/forecast?latitude=59.94&longitude=30.31&current=temperature_2m,weather_code,wind_speed_10m&wind_speed_unit=ms&timezone=Europe%2FMoscow', { signal: controller.signal });
            if (!response.ok) throw new Error('Weather unavailable');
            const data = await response.json();
            showWeather(data.current);
            try {
                localStorage.setItem('lab3-weather-petersburg', JSON.stringify({ time: Date.now(), current: data.current }));
            } catch {}
        } catch {
            description.textContent = 'Погода не загрузилась. Попробуй ещё раз.';
        } finally {
            clearTimeout(timeout);
            refresh.disabled = false;
        }
    }

    refresh.addEventListener('click', () => loadWeather(false));
    loadWeather(true);

    const canvas = document.getElementById('snake-game');
    const context = canvas.getContext('2d');
    const scoreOutput = document.getElementById('snake-score');
    const bestOutput = document.getElementById('snake-best');
    const status = document.getElementById('snake-status');
    const start = document.getElementById('snake-start');
    const directions = {
        up: { x: 0, y: -1 }, down: { x: 0, y: 1 },
        left: { x: -1, y: 0 }, right: { x: 1, y: 0 }
    };
    let best = readNumber('lab3-snake-best');
    let snake;
    let food;
    let direction;
    let nextDirection;
    let score;
    let timer = null;
    bestOutput.textContent = best;

    function placeFood() {
        const free = [];
        for (let y = 0; y < 20; y++) {
            for (let x = 0; x < 20; x++) {
                if (!snake.some(part => part.x === x && part.y === y)) free.push({ x, y });
            }
        }
        return free[Math.floor(Math.random() * free.length)];
    }

    function drawSnake() {
        context.fillStyle = '#311044';
        context.fillRect(0, 0, 240, 240);
        context.strokeStyle = '#492050';
        context.lineWidth = 1;
        for (let i = 12; i < 240; i += 12) {
            context.beginPath();
            context.moveTo(i, 0);
            context.lineTo(i, 240);
            context.moveTo(0, i);
            context.lineTo(240, i);
            context.stroke();
        }
        if (food) {
            context.fillStyle = '#ff4fbd';
            context.fillRect(food.x * 12 + 1, food.y * 12 + 1, 10, 10);
        }
        snake.forEach((part, index) => {
            context.fillStyle = index === 0 ? '#ffffff' : '#cbff37';
            context.fillRect(part.x * 12 + 1, part.y * 12 + 1, 10, 10);
        });
    }

    function resetSnake() {
        snake = [{ x: 8, y: 10 }, { x: 7, y: 10 }, { x: 6, y: 10 }];
        direction = directions.right;
        nextDirection = direction;
        score = 0;
        scoreOutput.textContent = score;
        food = placeFood();
        drawSnake();
    }

    function stopSnake(message) {
        clearInterval(timer);
        timer = null;
        status.textContent = message;
        start.textContent = 'Ещё раз';
    }

    function tick() {
        direction = nextDirection;
        const head = { x: snake[0].x + direction.x, y: snake[0].y + direction.y };
        const grows = head.x === food.x && head.y === food.y;
        const body = grows ? snake : snake.slice(0, -1);
        if (head.x < 0 || head.x >= 20 || head.y < 0 || head.y >= 20 ||
            body.some(part => part.x === head.x && part.y === head.y)) {
            stopSnake(`Столкновение! Счёт: ${score}.`);
            return;
        }
        snake.unshift(head);
        if (grows) {
            score++;
            scoreOutput.textContent = score;
            if (score > best) {
                best = score;
                bestOutput.textContent = best;
                saveNumber('lab3-snake-best', best);
            }
            food = placeFood();
            if (!food) stopSnake('Всё поле твоё!');
        } else {
            snake.pop();
        }
        drawSnake();
    }

    function turn(name) {
        const chosen = directions[name];
        if (timer !== null && chosen && !(chosen.x === -direction.x && chosen.y === -direction.y)) {
            nextDirection = chosen;
        }
    }

    start.addEventListener('click', () => {
        clearInterval(timer);
        resetSnake();
        timer = setInterval(tick, 150);
        start.textContent = 'Заново';
        status.textContent = 'Поехали! Стрелки или кнопки снизу.';
        canvas.focus({ preventScroll: true });
    });
    canvas.addEventListener('keydown', event => {
        const keys = { ArrowUp: 'up', ArrowDown: 'down', ArrowLeft: 'left', ArrowRight: 'right' };
        if (keys[event.key]) {
            event.preventDefault();
            turn(keys[event.key]);
        }
    });
    document.querySelectorAll('.snake-controls button').forEach(button => {
        button.addEventListener('click', () => turn(button.dataset.direction));
    });
    document.addEventListener('visibilitychange', () => {
        if (document.hidden && timer !== null) stopSnake('Игра остановлена. Начнём ещё раз?');
    });
    resetSnake();
})();
