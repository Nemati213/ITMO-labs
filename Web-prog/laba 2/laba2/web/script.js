"use strict";

const CANVAS_SIZE = 400;
const CENTER_X = CANVAS_SIZE / 2;
const CENTER_Y = CANVAS_SIZE / 2;
const R_SCALE_PIXELS = 140;
const AXES_PADDING = 10;
const AXES_COLOR = "#000000";
const AREA_COLOR = "#3c9cff";
const SERVER_URL = "/fcgi-bin/lab2.jar";
const REQUEST_METHOD = "GET";
const ACCEPT_HEADER = "Accept";
const RESPONSE_CONTENT_TYPE = "application/json";
const X_PARAMETER = "x";
const Y_PARAMETER = "y";
const R_PARAMETER = "r";
const HISTORY_STORAGE_KEY = "history";
const DRONE_IMAGE_URL = "images/drone.png";
const WARHEAD_IMAGE_URL = "images/warhead.png";
const KING_IMAGE_URL = "images/king.png";
const ALARM_SOUND_URL = "sounds/alarm.mp3";
const EXPLOSION_SOUND_URL = "sounds/explosion.mp3";
const KING_LAUGH_SOUND_URL = "sounds/king-laugh.mp3";
const ERROR_EFFECT_DURATION_MS = 3000;
const WARHEAD_FALL_DURATION_MS = 1250;
const EXPLOSION_DURATION_MS = 950;
const DRONE_SPEED_PIXELS_PER_SECOND = 170;
const DRONE_SUBMIT_COOLDOWN_MS = 700;

const VALID_X_VALUES = new Set([-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2]);

const canvas = document.getElementById("graph");
const ctx = canvas.getContext("2d");
const form = document.getElementById("pointForm");
const resultTableBody = document.getElementById("result-body");
const validationMessage = document.getElementById("validation-message");
const submitButton = document.getElementById("submit-button");
const drone = document.createElement("img");
const alarmSound = new Audio(ALARM_SOUND_URL);
const explosionSound = new Audio(EXPLOSION_SOUND_URL);
const kingLaughSound = new Audio(KING_LAUGH_SOUND_URL);

let selectedR = null;
let selectedX = null;
let selectedY = null;
let requestInProgress = false;
let alarmTimeout = null;
let droneX = window.innerWidth / 2;
let droneY = window.innerHeight / 2;
let droneDirectionX = 1;
let droneDirectionY = 0;
let droneLastFrameTime = performance.now();
let droneSubmitBlockedUntil = 0;

init();

function init() {
    drawGraph();
    initEventListeners();
    initDrone();
    loadHistory();
}

function initEventListeners() {
    form.addEventListener("submit", handleFormSubmit);
    document.addEventListener("keydown", handleDroneKeydown);
    document.addEventListener("pointerdown", handleBackgroundPointerDown);
    window.addEventListener("resize", keepDroneInsideViewport);
}

function initDrone() {
    drone.src = DRONE_IMAGE_URL;
    drone.alt = "Дрон";
    drone.className = "background-drone";
    drone.tabIndex = -1;
    document.body.append(drone);
    updateDronePosition();
    requestAnimationFrame(moveDrone);
}

function handleDroneKeydown(event) {
    if (isFormElement(document.activeElement)) return;

    const directions = {
        ArrowUp: [0, -1],
        ArrowDown: [0, 1],
        ArrowLeft: [-1, 0],
        ArrowRight: [1, 0]
    };
    const direction = directions[event.key];

    if (direction === undefined) return;

    event.preventDefault();
    droneDirectionX = direction[0];
    droneDirectionY = direction[1];
}

function handleBackgroundPointerDown(event) {
    if (isFormElement(event.target)) return;
    event.preventDefault();
    drone.focus({preventScroll: true});
}

function isFormElement(element) {
    return element instanceof Element
        && element.closest("input, button, select, textarea, a, label") !== null;
}

function moveDrone(currentTime) {
    const elapsedSeconds = Math.min((currentTime - droneLastFrameTime) / 1000, 0.05);
    droneLastFrameTime = currentTime;

    droneX += droneDirectionX * DRONE_SPEED_PIXELS_PER_SECOND * elapsedSeconds;
    droneY += droneDirectionY * DRONE_SPEED_PIXELS_PER_SECOND * elapsedSeconds;

    teleportDroneAtViewportEdges();
    updateDronePosition();
    checkDroneSubmitCollision(currentTime);
    requestAnimationFrame(moveDrone);
}

function teleportDroneAtViewportEdges() {
    const halfWidth = (drone.offsetWidth || 125) / 2;
    const halfHeight = (drone.offsetHeight || 125) / 2;

    if (droneX < -halfWidth) droneX = window.innerWidth + halfWidth;
    if (droneX > window.innerWidth + halfWidth) droneX = -halfWidth;
    if (droneY < -halfHeight) droneY = window.innerHeight + halfHeight;
    if (droneY > window.innerHeight + halfHeight) droneY = -halfHeight;
}

function updateDronePosition() {
    drone.style.transform = `translate3d(${droneX}px, ${droneY}px, 0) translate(-50%, -50%)`;
}

function checkDroneSubmitCollision(currentTime) {
    if (requestInProgress
        || submitButton.disabled
        || currentTime < droneSubmitBlockedUntil) {
        return;
    }

    const droneBounds = drone.getBoundingClientRect();
    const buttonBounds = submitButton.getBoundingClientRect();
    const intersects = droneBounds.right >= buttonBounds.left
        && droneBounds.left <= buttonBounds.right
        && droneBounds.bottom >= buttonBounds.top
        && droneBounds.top <= buttonBounds.bottom;

    if (!intersects) return;

    droneSubmitBlockedUntil = currentTime + DRONE_SUBMIT_COOLDOWN_MS;
    resetDrone();
    form.requestSubmit();
}

function resetDrone() {
    droneX = window.innerWidth / 2;
    droneY = window.innerHeight / 2;
    droneDirectionX = 1;
    droneDirectionY = 0;
    updateDronePosition();
}

function keepDroneInsideViewport() {
    droneX = Math.min(Math.max(droneX, 0), window.innerWidth);
    droneY = Math.min(Math.max(droneY, 0), window.innerHeight);
    updateDronePosition();
}

function handleFormSubmit(event) {
    event.preventDefault();

    if (requestInProgress) return;

    if (!validateR()) return;
    if (!validateX()) return;
    if (!validateY()) return;

    processForm();
}

async function processForm() {
    requestInProgress = true;
    submitButton.disabled = true;
    clearError();

    const parameters = new URLSearchParams({
        [X_PARAMETER]: selectedX,
        [Y_PARAMETER]: selectedY,
        [R_PARAMETER]: selectedR
    });

    try {
        const response = await fetch(`${SERVER_URL}?${parameters}`, {
            method: REQUEST_METHOD,
            headers: {
                [ACCEPT_HEADER]: RESPONSE_CONTENT_TYPE
            }
        });

        let result;

        try {
            result = await response.json();
        } catch {
            throw new Error("Сервер вернул некорректный ответ");
        }

        if (!response.ok) {
            throw new Error(result.error || `Ошибка сервера: ${response.status}`);
        }

        const attempt = {
            x: result.x,
            y: result.y,
            r: result.r,
            hit: result.hit,
            time: result.currentTime,
            executionTimeNanos: result.executionTimeNanos
        };

        recordAttempt(attempt);

        if (attempt.hit) {
            await showHitAnimation(attempt);
        } else {
            await showMissAnimation();
        }
    } catch (error) {
        console.error(error);
        showError(error instanceof Error
            ? error.message
            : "Не удалось получить ответ сервера");
    } finally {
        requestInProgress = false;
        submitButton.disabled = false;
    }
}

function validateR() {
    const rInput = document.getElementById("r-coordinate");
    const rRaw = rInput.value.trim().replace(',', '.');

    if (rRaw === "") {
        showError("Пожалуйста, введите R");
        return false;
    }

    if (!checkString(rRaw) || !isRInRange(rRaw)) {
        showError("R должен быть числом в диапазоне [2; 5]");
        return false;
    }

    selectedR = rRaw;
    drawGraph();
    clearError();
    return true;
}

function validateX() {
    const checked = document.querySelectorAll('input[name="x-coord"]:checked');

    if (checked.length === 0) {
        showError("Пожалуйста, выберите X");
        return false;
    }

    if (checked.length > 1) {
        showError("Пожалуйста, выберите ровно один X");
        return false;
    }

    const val = parseFloat(checked[0].value);
    if (!VALID_X_VALUES.has(val)) {
        showError("Выбрано недопустимое значение X");
        return false;
    }

    selectedX = val;
    clearError();
    return true;
}

function validateY() {
    const yInput = document.getElementById("y-coordinate");
    const yRaw = yInput.value.trim().replace(',', '.');

    if (yRaw === "") {
        showError("Пожалуйста, введите Y");
        return false;
    }
    if (!checkString(yRaw) || !isYInRange(yRaw)) {
        showError("Y должен быть числом в диапазоне [-3; 3]");
        return false;
    }

    selectedY = yRaw;
    clearError();
    return true;
}

function checkString(value) {
    const text = value.trim().replace(",", ".");
    const unsigned = text.startsWith("-") || text.startsWith("+")
        ? text.slice(1)
        : text;

    const parts = unsigned.split(".");
    if (parts.length > 2) return false;

    const integer = parts[0];
    const fraction = parts[1] ?? "";
    const digits = integer + fraction;

    if (digits.length === 0) return false;

    for (const char of digits) {
        if (char < "0" || char > "9") return false;
    }

    return true;
}

function isYInRange(value) {
    const text = value.replace(",", ".");
    const unsigned = text.startsWith("-") || text.startsWith("+")
        ? text.slice(1)
        : text;
    const [integer = "0", fraction = ""] = unsigned.split(".");
    const whole = Number(integer || "0");

    if (whole < 3) return true;
    if (whole > 3) return false;

    for (const char of fraction) {
        if (char !== "0") return false;
    }

    return true;
}

function isRInRange(value) {
    const text = value.replace(",", ".");
    if (text.startsWith("-")) return false;

    const unsigned = text.startsWith("+") ? text.slice(1) : text;
    const [integer = "0", fraction = ""] = unsigned.split(".");
    const whole = Number(integer || "0");

    if (whole < 2 || whole > 5) return false;
    if (whole < 5) return true;

    for (const char of fraction) {
        if (char !== "0") return false;
    }

    return true;
}

function showError(message) {
    stopAlarm();
    validationMessage.textContent = message;
    document.body.dataset.errorMessage = message;
    document.body.classList.add("alarm-active");
    playSound(alarmSound);
    alarmTimeout = setTimeout(stopAlarm, ERROR_EFFECT_DURATION_MS);
}

function clearError() {
    validationMessage.textContent = "";
    delete document.body.dataset.errorMessage;
    stopAlarm();
}

function stopAlarm() {
    clearTimeout(alarmTimeout);
    alarmTimeout = null;
    document.body.classList.remove("alarm-active");
    alarmSound.pause();
    alarmSound.currentTime = 0;
}

function playSound(sound) {
    sound.currentTime = 0;
    sound.play().catch(() => {});
}

async function showHitAnimation(attempt) {
    const target = getGraphPointPosition(attempt);
    const warhead = document.createElement("img");

    warhead.src = WARHEAD_IMAGE_URL;
    warhead.alt = "Падающая боеголовка";
    warhead.className = "falling-warhead";
    warhead.style.left = `${target.x}px`;
    document.body.append(warhead);

    const fall = warhead.animate([
        {
            top: "-190px",
            transform: "translateX(-50%) rotate(-18deg)"
        },
        {
            top: `${target.y - 72}px`,
            transform: "translateX(-50%) rotate(9deg)"
        }
    ], {
        duration: WARHEAD_FALL_DURATION_MS,
        easing: "cubic-bezier(.62,.02,.92,.48)",
        fill: "forwards"
    });

    await fall.finished.catch(() => {});
    warhead.remove();
    await showExplosion(target.x, target.y);
}

function getGraphPointPosition(attempt) {
    const canvasBounds = canvas.getBoundingClientRect();
    const canvasX = CENTER_X + Number(attempt.x) / Number(attempt.r) * R_SCALE_PIXELS;
    const canvasY = CENTER_Y - Number(attempt.y) / Number(attempt.r) * R_SCALE_PIXELS;

    return {
        x: canvasBounds.left + canvasX * canvasBounds.width / CANVAS_SIZE,
        y: canvasBounds.top + canvasY * canvasBounds.height / CANVAS_SIZE
    };
}

function showExplosion(x, y) {
    const explosion = document.createElement("div");

    explosion.className = "mini-explosion";
    explosion.textContent = "💥 БАБАХ 💥";
    explosion.style.left = `${x}px`;
    explosion.style.top = `${y}px`;
    document.body.append(explosion);
    playSound(explosionSound);

    return new Promise(resolve => {
        setTimeout(() => {
            explosion.remove();
            resolve();
        }, EXPLOSION_DURATION_MS);
    });
}

function showMissAnimation() {
    const king = document.createElement("img");

    king.src = KING_IMAGE_URL;
    king.alt = "Смеющийся король";
    king.className = "laughing-king";
    document.body.append(king);
    kingLaughSound.currentTime = 0;

    return new Promise(resolve => {
        let fallbackTimeout = null;

        const finish = () => {
            clearTimeout(fallbackTimeout);
            kingLaughSound.removeEventListener("ended", finish);
            kingLaughSound.pause();
            kingLaughSound.currentTime = 0;
            king.remove();
            resolve();
        };

        kingLaughSound.addEventListener("ended", finish, { once: true });
        fallbackTimeout = setTimeout(finish, 10000);

        kingLaughSound.play().catch(() => {
            clearTimeout(fallbackTimeout);
            fallbackTimeout = setTimeout(finish, 2500);
        });
    });
}

function drawGraph() {
    ctx.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
    drawShapes();
    drawAxes();
    drawLabels();
}

function drawShapes() {
    ctx.strokeStyle = AXES_COLOR;
    ctx.strokeRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
    ctx.fillStyle = AREA_COLOR;
    ctx.fillRect(CENTER_X, CENTER_Y, R_SCALE_PIXELS / 2, R_SCALE_PIXELS);

    ctx.beginPath();
    ctx.moveTo(CENTER_X, CENTER_Y);
    ctx.arc(CENTER_X, CENTER_Y, R_SCALE_PIXELS, Math.PI, Math.PI * 1.5, false);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(CENTER_X, CENTER_Y);
    ctx.lineTo(CENTER_X, CENTER_Y - (R_SCALE_PIXELS / 2));
    ctx.lineTo(CENTER_X + (R_SCALE_PIXELS / 2), CENTER_Y);
    ctx.closePath();
    ctx.fill();
}

function drawAxes() {
    ctx.strokeStyle = AXES_COLOR;
    ctx.lineWidth = 1.5;
    ctx.beginPath();

    ctx.moveTo(CENTER_X, CANVAS_SIZE - AXES_PADDING);
    ctx.lineTo(CENTER_X, AXES_PADDING);
    ctx.lineTo(CENTER_X - 5, AXES_PADDING + 8);
    ctx.moveTo(CENTER_X, AXES_PADDING);
    ctx.lineTo(CENTER_X + 5, AXES_PADDING + 8);

    ctx.moveTo(AXES_PADDING, CENTER_Y);
    ctx.lineTo(CANVAS_SIZE - AXES_PADDING, CENTER_Y);
    const arrowX = CANVAS_SIZE - AXES_PADDING;
    ctx.lineTo(arrowX - 8, CENTER_Y - 5);
    ctx.moveTo(arrowX, CENTER_Y);
    ctx.lineTo(arrowX - 8, CENTER_Y + 5);

    ctx.stroke();
}

function drawLabels() {
    const TICK_SIZE = 4;

    ctx.fillStyle = AXES_COLOR;
    ctx.font = '12px Arial';

    const labels = [
        { offset: -R_SCALE_PIXELS,       text: selectedR ? `-${selectedR}` : '-R' },
        { offset: -R_SCALE_PIXELS / 2,   text: selectedR ? `-${selectedR / 2}` : '-R/2' },
        { offset: R_SCALE_PIXELS / 2,    text: selectedR ? `${selectedR / 2}` : 'R/2' },
        { offset: R_SCALE_PIXELS,        text: selectedR ? `${selectedR}` : 'R' },
    ];

    ctx.strokeStyle = AXES_COLOR;
    ctx.lineWidth = 1.5;
    ctx.beginPath();

    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';

    labels.forEach(item => {
        const x = CENTER_X + item.offset;
        ctx.moveTo(x, CENTER_Y - TICK_SIZE);
        ctx.lineTo(x, CENTER_Y + TICK_SIZE);
        ctx.fillText(item.text, x, CENTER_Y + TICK_SIZE + 4);
    });

    ctx.textAlign = 'right';
    ctx.textBaseline = 'middle';

    labels.forEach(item => {
        const y = CENTER_Y - item.offset;
        ctx.moveTo(CENTER_X - TICK_SIZE, y);
        ctx.lineTo(CENTER_X + TICK_SIZE, y);
        ctx.fillText(item.text, CENTER_X - TICK_SIZE - 4, y);
    });

    ctx.stroke();
    ctx.textAlign = 'left';
    ctx.textBaseline = 'middle';
    ctx.fillText("X", CANVAS_SIZE - AXES_PADDING, CENTER_Y - 12);
    ctx.fillText("Y", CENTER_X + 10, AXES_PADDING);
}

function recordAttempt(attempt) {
    const raw = localStorage.getItem(HISTORY_STORAGE_KEY);
    const history = JSON.parse(raw || "[]");
    history.push(attempt);
    const jsonString = JSON.stringify(history);
    localStorage.setItem(HISTORY_STORAGE_KEY, jsonString);
    addTableRow(attempt);
}

function formatDateTime(timestamp) {
    return new Intl.DateTimeFormat("ru-RU", {
        dateStyle: "short",
        timeStyle: "medium"
    }).format(new Date(timestamp));
}

function addTableRow(attempt) {
    const statusText = attempt.hit ? "Попадание" : "Промах";
    const statusClass = attempt.hit ? "result-hit" : "result-miss";
    const formattedDate = formatDateTime(attempt.time);
    const executionTime = attempt.executionTimeNanos === undefined
        ? "—"
        : `${attempt.executionTimeNanos} нс`;

    const rowHTML = `
        <tr>
            <td>${attempt.x}</td>
            <td>${attempt.y}</td>
            <td>${attempt.r}</td>
            <td class="${statusClass}">${statusText}</td>
            <td>${formattedDate}</td>
            <td>${executionTime}</td>
        </tr>
    `;

    resultTableBody.insertAdjacentHTML("afterbegin", rowHTML);
}


function loadHistory() {
    const raw = localStorage.getItem(HISTORY_STORAGE_KEY);
    const history = JSON.parse(raw || "[]");

    resultTableBody.innerHTML = "";

    history.forEach(attempt => {
        addTableRow(attempt);
    });
}
