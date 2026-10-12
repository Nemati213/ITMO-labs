"use strict";

const CANVAS_SIZE = 400;
const CENTER_X = CANVAS_SIZE / 2;
const CENTER_Y = CANVAS_SIZE / 2;
const R_SCALE_PIXELS = 140;
const AXES_PADDING = 10;
const AXES_COLOR = "#ffffff";
const AXES_OUTLINE_COLOR = "#000000";
const AREA_COLOR = "#f2d0a6";
const VALID_X_VALUES = new Set([-5, -4, -3, -2, -1, 0, 1, 2, 3]);
const VALID_R_VALUES = new Set([1, 2, 3, 4, 5]);

const canvas = document.getElementById("graph");
const ctx = canvas.getContext("2d");
const areaImage = new Image();
const form = document.getElementById("pointForm");
const validationMessage = document.getElementById("validation-message");

let selectedR = null;
let selectedX = null;
let selectedY = null;

init();

function init() {
    const value = document.getElementById("r-coordinate").value;
    selectedR = isRInRange(value) ? value : null;
    areaImage.addEventListener("load", drawGraph);
    areaImage.src = canvas.dataset.areaImage;
    drawGraph();
    initEventListeners();
}

function initEventListeners() {
    form.addEventListener("submit", handleFormSubmit);
    canvas.addEventListener("click", handleCanvasClick);
    document.getElementById("r-coordinate").addEventListener("change", () => {
        const value = document.getElementById("r-coordinate").value;
        selectedR = isRInRange(value) ? value : null;
        drawGraph();
    });
}

function handleFormSubmit(event) {
    event.preventDefault();

    if (!validateR()) return;
    if (!validateX()) return;
    if (!validateY()) return;

    processForm();
}

function handleCanvasClick(event) {
    if (!validateR()) return;

    const bounds = canvas.getBoundingClientRect();
    const canvasX = (event.clientX - bounds.left) * canvas.width / bounds.width;
    const canvasY = (event.clientY - bounds.top) * canvas.height / bounds.height;
    const x = (canvasX - CENTER_X) * Number(selectedR) / R_SCALE_PIXELS;
    const y = (CENTER_Y - canvasY) * Number(selectedR) / R_SCALE_PIXELS;
    const parameters = new URLSearchParams({
        x: String(x),
        y: String(y),
        r: selectedR,
        source: "canvas",
        timeZone: Intl.DateTimeFormat().resolvedOptions().timeZone || "UTC"
    });

    window.location.assign(`${form.action}?${parameters}`);
}

function processForm() {
    clearError();
    document.getElementById("y-coordinate").value = selectedY;
    document.getElementById("time-zone").value =
        Intl.DateTimeFormat().resolvedOptions().timeZone || "UTC";
    form.submit();
}

function validateR() {
    const rInput = document.getElementById("r-coordinate");
    const rRaw = rInput.value.trim().replace(',', '.');

    if (rRaw === "") {
        showError("Пожалуйста, выберите R");
        return false;
    }

    if (!checkString(rRaw) || !isRInRange(rRaw)) {
        showError("R должен быть одним из значений: 1, 2, 3, 4, 5");
        return false;
    }

    selectedR = rRaw;
    drawGraph();
    clearError();
    return true;
}

function validateX() {
    const checked = document.querySelectorAll('input[name="x"]:checked');

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
        showError("Y должен быть числом в диапазоне [-3; 5]");
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
    const number = Number(value.replace(",", "."));
    return Number.isFinite(number) && number >= -3 && number <= 5;
}

function isRInRange(value) {
    return VALID_R_VALUES.has(Number(value));
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
    drawPoints();
}

function drawPoints() {
    if (!selectedR) return;

    const radius = Number(selectedR);
    const rows = document.querySelectorAll("#resultTable tbody tr[data-x]");

    ctx.save();
    for (const row of rows) {
        const pointX = Number(row.dataset.x);
        const pointY = Number(row.dataset.y);
        const x = CENTER_X + pointX * R_SCALE_PIXELS / radius;
        const y = CENTER_Y - pointY * R_SCALE_PIXELS / radius;

        ctx.fillStyle = isPointInsideArea(pointX, pointY, radius) ? "#97ff00" : "#ff3b19";
        ctx.beginPath();
        ctx.arc(x, y, 5, 0, Math.PI * 2);
        ctx.fill();
        ctx.strokeStyle = "black";
        ctx.lineWidth = 4;
        ctx.stroke();
        ctx.strokeStyle = "white";
        ctx.lineWidth = 2;
        ctx.stroke();
    }
    ctx.restore();
}

function isPointInsideArea(x, y, r) {
    const insideTriangle = x <= 0 && x >= -r && y >= 0 && y <= x + r;
    const insideRectangle = x >= 0 && x <= r && y >= 0 && y <= r / 2;
    const insideQuarterCircle = x >= 0 && y <= 0 && x * x + y * y <= r * r;

    return insideTriangle || insideRectangle || insideQuarterCircle;
}

function drawShapes() {
    ctx.strokeStyle = AXES_COLOR;
    ctx.strokeRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

    ctx.save();
    ctx.beginPath();
    ctx.rect(CENTER_X, CENTER_Y - R_SCALE_PIXELS / 2,
        R_SCALE_PIXELS, R_SCALE_PIXELS / 2);
    ctx.moveTo(CENTER_X, CENTER_Y);
    ctx.arc(CENTER_X, CENTER_Y, R_SCALE_PIXELS, 0, Math.PI / 2, false);
    ctx.closePath();

    ctx.moveTo(CENTER_X, CENTER_Y);
    ctx.lineTo(CENTER_X - R_SCALE_PIXELS, CENTER_Y);
    ctx.lineTo(CENTER_X, CENTER_Y - R_SCALE_PIXELS);
    ctx.closePath();
    ctx.clip();

    if (areaImage.complete && areaImage.naturalWidth > 0) {
        const size = Math.min(areaImage.naturalWidth, areaImage.naturalHeight);
        const sourceX = (areaImage.naturalWidth - size) / 2;
        const sourceY = (areaImage.naturalHeight - size) / 2;
        ctx.drawImage(areaImage, sourceX, sourceY, size, size,
            CENTER_X - R_SCALE_PIXELS, CENTER_Y - R_SCALE_PIXELS,
            R_SCALE_PIXELS * 2, R_SCALE_PIXELS * 2);
    } else {
        ctx.fillStyle = AREA_COLOR;
        ctx.fillRect(CENTER_X - R_SCALE_PIXELS, CENTER_Y - R_SCALE_PIXELS,
            R_SCALE_PIXELS * 2, R_SCALE_PIXELS * 2);
    }

    ctx.restore();
}

function drawAxes() {
    ctx.strokeStyle = AXES_OUTLINE_COLOR;
    ctx.lineWidth = 4;
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
    ctx.strokeStyle = AXES_COLOR;
    ctx.lineWidth = 1.5;
    ctx.stroke();
}

function drawLabels() {
    const TICK_SIZE = 4;

    ctx.fillStyle = AXES_COLOR;
    ctx.font = 'bold 14px Arial';

    const labels = [
        { offset: -R_SCALE_PIXELS, text: selectedR ? `-${selectedR}` : '-R' },
        { offset: -R_SCALE_PIXELS / 2, text: selectedR ? `-${selectedR / 2}` : '-R/2' },
        { offset: R_SCALE_PIXELS / 2, text: selectedR ? `${selectedR / 2}` : 'R/2' },
        { offset: R_SCALE_PIXELS, text: selectedR ? `${selectedR}` : 'R' },
    ];

    ctx.strokeStyle = AXES_OUTLINE_COLOR;
    ctx.lineWidth = 4;
    ctx.beginPath();

    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';

    labels.forEach(item => {
        const x = CENTER_X + item.offset;
        ctx.moveTo(x, CENTER_Y - TICK_SIZE);
        ctx.lineTo(x, CENTER_Y + TICK_SIZE);
        drawLabel(item.text, x, CENTER_Y + TICK_SIZE + 4);
    });

    ctx.textAlign = 'right';
    ctx.textBaseline = 'middle';

    labels.forEach(item => {
        const y = CENTER_Y - item.offset;
        ctx.moveTo(CENTER_X - TICK_SIZE, y);
        ctx.lineTo(CENTER_X + TICK_SIZE, y);
        drawLabel(item.text, CENTER_X - TICK_SIZE - 4, y);
    });

    ctx.stroke();
    ctx.strokeStyle = AXES_COLOR;
    ctx.lineWidth = 1.5;
    ctx.stroke();
    ctx.textAlign = 'left';
    ctx.textBaseline = 'middle';
    drawLabel("X", CANVAS_SIZE - AXES_PADDING - 6, CENTER_Y - 12);
    drawLabel("Y", CENTER_X + 10, AXES_PADDING);
}

function drawLabel(text, x, y) {
    ctx.save();
    ctx.strokeStyle = AXES_OUTLINE_COLOR;
    ctx.lineWidth = 3;
    ctx.lineJoin = "round";
    ctx.strokeText(text, x, y);
    ctx.fillStyle = AXES_COLOR;
    ctx.fillText(text, x, y);
    ctx.restore();
}
