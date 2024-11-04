require: slotfilling/slotFilling.sc
    module = sys.zb-common
  
require: text/text.sc
    module = sys.zb-common

require: common.js
    module = sys.zb-common
    
# Для игры Назови столицу    
require: where/where.sc
    module = sys.zb-common

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
    state: Start
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

    state: AskCapital
        q: * $Capital *
        script:
            var pairs = $csv.read("geography-ru.csv");
            // Счетчик угаданных пар
            var correctAnswers = 0;
            // Массив использованных пар
            var usedPairs = [];

            // Игровой цикл
            while (usedPairs.length < pairs.length) {
                // Случайным образом выбираем пару, которая еще не использовалась
                var pair;
                do {
                    pair = pairs[Math.floor(Math.random() * pairs.length)];
                } while (usedPairs.includes(pair));

                // Задаем вопрос игроку
                var guess = prompt("Какая столица у государства " + pair[0] + "?");

                // Проверяем, содержит ли ответ только одну столицу
                if (guess.split(" ").length === 1) {
                    // Сравниваем ответ игрока с фактической столицей
                    if (pair[1] === guess) {
                        // Если ответ правильный, увеличиваем счетчик угаданных пар
                        correctAnswers++;
                    } else {
                        // Если ответ неверный, выводим сообщение об ошибке
                        alert("Неверный ответ! Попробуйте еще раз.");
                    }
                } else {
                    // Если ответ содержит несколько столиц, выводим сообщение об ошибке
                    alert("Эй, придется определиться! Перебором дело не пойдет");
                }

                // Добавляем использованную пару в массив usedPairs
                usedPairs.push(pair);
            }

            // Отображаем количество угаданных столиц и поздравляем игрока
            alert("Вы угадали все столицы! Поздравляем!");
        
    state: Text
        q: $Word
        a: Слово из справочника: {{$parseTree._Word.word}}

    state: NoMatch
        event!: noMatch
        a: Я предназначен только для игр! Не хотелось бы отходить от темы

    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /Start

