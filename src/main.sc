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

    state: StartGame
        intent!: /начатьИгру
        script:
            var pairs = loadDataFromCSV("geography-ru.csv");
            // Счетчик угаданных пар и массив использованных пар
            var correctAnswers = 0;
            var usedPairs = [];
            // Случайным образом выбираем пару
            var pair = pairs[Math.floor(Math.random() * pairs.length)];
            // Добавляем случайно отобранную пару в массив usedPairs
            usedPairs.push(pair);
            // Бот задает вопрос
            $reactions.answer("Какая столица у государства " + pair[0] + "?");

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

