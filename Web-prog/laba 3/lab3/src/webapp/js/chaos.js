(() => {
    const pokemonButton = document.getElementById('pokemon-next');
    const pokemonName = document.getElementById('pokemon-name');
    const pokemonInfo = document.getElementById('pokemon-info');
    const pokemonImage = document.getElementById('pokemon-image');

    async function loadPokemon() {
        pokemonButton.disabled = true;
        pokemonName.textContent = 'Ловим покемона…';
        const id = 1 + Math.floor(Math.random() * 151);
        const controller = new AbortController();
        const timeout = setTimeout(() => controller.abort(), 8000);
        try {
            let data;
            try { data = JSON.parse(localStorage.getItem(`lab3-pokemon-${id}`)); } catch {}
            if (!data) {
                const response = await fetch(`https://pokeapi.co/api/v2/pokemon/${id}/`, { signal: controller.signal });
                if (!response.ok) throw new Error('Pokemon unavailable');
                const result = await response.json();
                data = { name: result.name, image: result.sprites.front_default, height: result.height, weight: result.weight };
                try { localStorage.setItem(`lab3-pokemon-${id}`, JSON.stringify(data)); } catch {}
            }
            pokemonName.textContent = `#${id} ${data.name}`;
            pokemonInfo.textContent = `${data.height / 10} м · ${data.weight / 10} кг`;
            pokemonImage.src = data.image;
            pokemonImage.alt = data.name;
            pokemonImage.hidden = false;
        } catch {
            pokemonName.textContent = 'Сбежал! Нажми ещё раз.';
        } finally {
            clearTimeout(timeout);
            pokemonButton.disabled = false;
        }
    }
    pokemonButton.addEventListener('click', loadPokemon);
    loadPokemon();

    function shuffle(items) {
        for (let i = items.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [items[i], items[j]] = [items[j], items[i]];
        }
        return items;
    }

    const playerCards = document.getElementById('player-cards');
    const dealerCards = document.getElementById('dealer-cards');
    const cardStatus = document.getElementById('blackjack-status');
    const hit = document.getElementById('card-hit');
    const stand = document.getElementById('card-stand');
    let deck;
    let player;
    let dealer;
    let finished;

    function total(hand) {
        let value = hand.reduce((sum, card) => sum + card.value, 0);
        let aces = hand.filter(card => card.rank === 'A').length;
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }
        return value;
    }

    function renderCards(container, hand, hideSecond) {
        container.replaceChildren();
        hand.forEach((card, index) => {
            const element = document.createElement('span');
            const hidden = hideSecond && index === 1;
            element.className = hidden ? 'playing-card card-back' : `playing-card ${card.red ? 'card-red' : ''}`;
            element.textContent = hidden ? '?' : card.rank + card.suit;
            container.append(element);
        });
    }

    function renderGame() {
        renderCards(playerCards, player, false);
        renderCards(dealerCards, dealer, !finished);
        document.getElementById('player-score').textContent = total(player);
        document.getElementById('dealer-score').textContent = finished ? total(dealer) : '?';
        hit.disabled = finished;
        stand.disabled = finished;
    }

    function endRound() {
        finished = true;
        const mine = total(player);
        const theirs = total(dealer);
        if (mine > 21) cardStatus.textContent = 'Перебор. Дилер забрал раунд.';
        else if (theirs > 21 || mine > theirs) cardStatus.textContent = 'Победа! Красиво зашёл.';
        else if (mine === theirs) cardStatus.textContent = 'Ничья. Бывает.';
        else cardStatus.textContent = 'Дилер выиграл. Ещё одну?';
        renderGame();
    }

    function newRound() {
        deck = [];
        ['♠', '♥', '♦', '♣'].forEach((suit, suitIndex) => {
            ['A', '2', '3', '4', '5', '6', '7', '8', '9', '10', 'J', 'Q', 'K'].forEach((rank, index) => {
                deck.push({ suit, rank, value: index === 0 ? 11 : Math.min(index + 1, 10), red: suitIndex === 1 || suitIndex === 2 });
            });
        });
        shuffle(deck);
        player = [deck.pop(), deck.pop()];
        dealer = [deck.pop(), deck.pop()];
        finished = false;
        cardStatus.textContent = 'Ещё карту или остановишься?';
        if (total(player) === 21 || total(dealer) === 21) endRound();
        else renderGame();
    }

    hit.addEventListener('click', () => {
        if (finished) return;
        player.push(deck.pop());
        if (total(player) > 21) endRound();
        else renderGame();
    });
    stand.addEventListener('click', () => {
        if (finished) return;
        while (total(dealer) < 17) dealer.push(deck.pop());
        endRound();
    });
    document.getElementById('card-new').addEventListener('click', newRound);
    newRound();

    const questions = shuffle([
        ['Сколько бит в одном байте?', ['8', '16', '4', '32']],
        ['Какой HTTP-метод используется в этой лабе?', ['GET', 'POST', 'DELETE', 'PATCH']],
        ['Где хранится общая история проверок?', ['ServletContext', 'В кармане Нагиева', 'Только в браузере', 'В CSS']],
        ['Сколько градусов в прямом угле?', ['90', '180', '45', '360']],
        ['Кто эволюционирует в Райчу?', ['Пикачу', 'Бульбазавр', 'Мяут', 'Сквиртл']],
        ['Как называется столица Австралии?', ['Канберра', 'Сидней', 'Мельбурн', 'Перт']],
        ['Что делает JSP при первом обращении?', ['Компилируется в сервлет', 'Удаляет WAR', 'Запускает змейку', 'Меняет порт']],
        ['Сколько будет 2 в десятой степени?', ['1024', '1000', '512', '2048']],
        ['Какой символ начинает селектор по id в CSS?', ['#', '.', '@', '&']],
        ['В каком квадранте X положительный, а Y отрицательный?', ['IV', 'I', 'II', 'III']],
        ['Сколько карт в колоде без джокеров?', ['52', '54', '36', '48']],
        ['Какой язык исполняет браузер для этих игр?', ['JavaScript', 'Java', 'SQL', 'JSP']]
    ]);
    const answers = document.getElementById('quiz-answers');
    const quizStatus = document.getElementById('quiz-status');
    let questionIndex = 0;
    let correctCount = 0;
    let answeredCount = 0;

    function showQuestion() {
        const question = questions[questionIndex % questions.length];
        document.getElementById('quiz-question').textContent = question[0];
        quizStatus.textContent = '';
        answers.replaceChildren();
        shuffle([...question[1]]).forEach(answer => {
            const button = document.createElement('button');
            button.type = 'button';
            button.textContent = answer;
            button.addEventListener('click', () => {
                answeredCount++;
                const correct = answer === question[1][0];
                if (correct) correctCount++;
                quizStatus.textContent = correct ? 'Да! Мозг работает.' : `Мимо. Ответ: ${question[1][0]}`;
                document.getElementById('quiz-score').textContent = correctCount;
                document.getElementById('quiz-total').textContent = answeredCount;
                answers.querySelectorAll('button').forEach(item => {
                    item.disabled = true;
                    if (item.textContent === question[1][0]) item.classList.add('answer-correct');
                    else if (item === button) item.classList.add('answer-wrong');
                });
            });
            answers.append(button);
        });
    }
    document.getElementById('quiz-next').addEventListener('click', () => {
        questionIndex++;
        if (questionIndex % questions.length === 0) shuffle(questions);
        showQuestion();
    });
    showQuestion();
})();
