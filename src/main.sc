require: slotfilling/slotFilling.sc
    module = sys.zb-common
  
require: text/text.sc
    module = sys.zb-common

require: common.js
    module = sys.zb-common
    
# Для игры Назови столицу
require: where/where.sc
    module = sys.zb-common

require: geography-ru.csv
    name = Pairs
    var = $Pairs
    strict = true

# Для игры Виселица
require: hangmanGameData.csv
    name = HangmanGameData
    var = $HangmanGameData

patterns:
    $Word = $entity<HangmanGameData> || converter = function ($parseTree) {
        var id = $parseTree.HangmanGameData[0].value;
        return $HangmanGameData[id].value;
        };

theme: /
    state: Sstart
        q!: $regex</start>
        script:
            $jsapi.startSession();
        a: Привет! Давай поиграем. Я буду называть страну, а ты угадываешь столицу. Я готов начать
        
    state: Hello
        intent!: /привет
        a: Привет-привет, уже виделись :)
        
    state: Buy
        intent!: /пока
        a: Пока-пока

theme: /Game
    state: StartGame
        intent!: /начатьИгру
        script:
            // var Pairs = $parseTree.Pairs
            // Массив использованных пар 
            var usedPairs = [];
            var keys = Object.keys($Pairs); 
            var randomIndex = keys[Math.floor(Math.random() * keys.length)];
            var randomValue = keys[randomIndex][1];
            // var randomValue = $Pairs[randomKey]; 
            var state = randomValue;
            var capital = keys[randomIndex][0];

            usedPairs.push(state, capital);
            $reactions.answer("Какая столица у государства " + state + "? (Правильный ответ: " + capital + ")");

         // для проверки ответа игрока
    state: CheckCapital
        q: * $Capital *
        script:
            // Проверяем, содержит ли ответ только одну столицу
            if ($parseTree._Capital.name.split(" ").length === 1) {
                // Сравниваем ответ игрока с фактической столицей
                if (pair[1] === $parseTree._Capital.name) {
                    // Если ответ правильный, увеличиваем счетчик угаданных пар
                    correctAnswers++;
                    // Обновляем массив использованных пар
                    usedPairs.push(pair);
                } else {
                    // Если ответ неверный, выводим сообщение об ошибке
                    $reactions.answer("Неверный ответ! Попробуйте еще раз.");
                }
            } else {
                // Если ответ содержит несколько столиц, выводим сообщение об ошибке
                $reactions.answer("Эй, придется определиться! Перебором дело не пойдет");
            }
            // Случайным образом выбираем новую пару, если еще не угаданы все пары
            if (usedPairs.length < pairs.length) {
                var newPair = pairs[Math.floor(Math.random() * pairs.length)];
                while (usedPairs.includes(newPair)) {
                    newPair = pairs[Math.floor(Math.random() * pairs.length)];
                }
                pair = newPair;
                usedPairs.push(pair);
            }
            // Бот задает новый вопрос
            $reactions.answer("Какая столица у государства " + pair[0] + "?");

    state: EndGame
        intent!: /end_game
        script:
             var correctAnswers = $memory.get("correctAnswers") || 0;
             $reactions.answer("Игра завершена! Ты правильно назвал " + correctAnswers + " столиц.")

    state: NoMatch
        event!: noMatch
        a: Я предназначен только для игр! Не хотелось бы отходить от темы

    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /Start

