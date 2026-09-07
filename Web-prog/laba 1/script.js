"use strict";

const CANVAS_SIZE = 400;
const CENTER_X = CANVAS_SIZE / 2;
const CENTER_Y = CANVAS_SIZE / 2;
const R_SCALE_PIXELS = 140;
const AXES_PADDING = 10;
const AXES_COLOR = "#000000";
const AREA_COLOR = "#3c9cff";

const VALID_X_VALUES = new Set([-3, -2, -1, 0, 1, 2, 3, 4, 5]);
const VALID_R_VALUES = new Set([1, 2, 3, 4, 5]);

const canvas = document.getElementById("graph");
const ctx = canvas.getContext("2d");
const form = document.getElementById("pointForm");
const resultTableBody = document.getElementById("result-body");
const validationMessage = document.getElementById("validation-message");

let selectedR = null;
let selectedX = null;
let selectedY = null;

init();

function init() {
    drawGraph();
    initEventListeners();
    loadHistory();
}

function initEventListeners() {
    const rButtons = document.querySelectorAll(".r-button");
    rButtons.forEach(button => {
        button.addEventListener("click", () => {
            rButtons.forEach(btn => btn.classList.remove("selected"));
            button.classList.add("selected");
            selectedR = parseFloat(button.dataset.r);
            drawGraph();
        });
    });

    const xCheckboxes = document.querySelectorAll('input[name="x-coord"]');
    xCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", () => {
            if (checkbox.checked) {
                xCheckboxes.forEach(other => {
                    if (other !== checkbox) {
                        other.checked = false;
                    }
                });
            }
        });
    });

    form.addEventListener("submit", handleFormSubmit);
}

function handleFormSubmit(event) {
    event.preventDefault();

    if (!validateR()) return;
    if (!validateX()) return;
    if (!validateY()) return;

    const isHit = checkHit(selectedX, selectedY, selectedR);
    const timeStamp = Date.now();
    const attempt = {
        x: selectedX,
        y: selectedY,
        r: selectedR,
        hit: isHit,
        time: timeStamp
    };
    recordAttempt(attempt);
}



function validateR() {
    if (selectedR === null) {
        showError("Пожалуйста, выберите R");
        return false;
    }

    if (!VALID_R_VALUES.has(selectedR)) {
        showError("Выбрано недопустимое значение R");
        return false;
    }

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

    const yNum = Number(yRaw);
    if (isNaN(yNum)) {
        showError("Y должен быть числом");
        return false;
    }

    if (yNum < -5 || yNum > 3) {
        showError("Y должен быть в диапазоне от -5 до 3");
        return false;
    }

    selectedY = yNum;
    clearError();
    return true;
}

function showError(message) {
    validationMessage.textContent = message;
}

function clearError() {
    validationMessage.textContent = "";
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
    ctx.fillRect(CENTER_X, CENTER_Y, R_SCALE_PIXELS, R_SCALE_PIXELS / 2);

    ctx.beginPath();
    ctx.moveTo(CENTER_X, CENTER_Y);
    ctx.arc(CENTER_X, CENTER_Y, R_SCALE_PIXELS / 2, -Math.PI / 2, 0, false);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(CENTER_X, CENTER_Y);
    ctx.lineTo(CENTER_X - R_SCALE_PIXELS, CENTER_Y);
    ctx.lineTo(CENTER_X, CENTER_Y - (R_SCALE_PIXELS / 2));
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

function checkHit(x, y, r) {
    if (x >= 0 && y >= 0) {
        return (x * x + y * y) <= (r / 2) * (r / 2);
    }

    if (x <= 0 && y >= 0) {
        return y <= (0.5 * x + r / 2);
    }

    if (x >= 0 && y <= 0) {
        return (x <= r) && (y >= -r / 2);
    }
    return false;
}

function recordAttempt(attempt) {
    const raw = localStorage.getItem("history");
    const history = JSON.parse(raw || "[]");
    history.push(attempt);
    const jsonString = JSON.stringify(history);
    localStorage.setItem("history", jsonString);
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

    const rowHTML = `
        <tr>
            <td>${attempt.x}</td>
            <td>${attempt.y}</td>
            <td>${attempt.r}</td>
            <td class="${statusClass}">${statusText}</td>
            <td>${formattedDate}</td>
        </tr>
    `;

    resultTableBody.insertAdjacentHTML("afterbegin", rowHTML);
}


function loadHistory() {
    const raw = localStorage.getItem("history");
    const history = JSON.parse(raw || "[]");

    resultTableBody.innerHTML = "";

    history.forEach(attempt => {
        addTableRow(attempt);
    });
}
