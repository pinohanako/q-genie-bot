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
    name = pairs
    var = $pairs

# Для игры Виселица
require: hangmanGameData.csv
    name = HangmanGameData
    var = $HangmanGameData

patterns:
    $Word = $entity<HangmanGameData> || converter = function ($parseTree) {
        var id = $parseTree.HangmanGameData[0].value;
        return $HangmanGameData[id].value;
        };
    $State = $entity<pairs> || converter = function ($parseTree) {
        var capital = $parseTree.pairs[0].value;
        for (var i = 0; i < $pairs.length; i++) {
            if ($pairs[i][1] === capital) {
                return $pairs[i][0];
            }
        }
        return null;
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

    state: StartGame
        intent!: /начатьИгру
        script:
            var pairs = $parseTree.pairs[0].value
            // Счетчик угаданных пар и массив использованных пар
            // var correctAnswers = 0;
            var usedPairs = [];
            var pair = pairs[Math.floor(Math.random() * pairs.length)];
            usedPairs.push(pair);
            $reactions.answer("Какая столица у государства " + pair[0] + "?");

    state: Text
        q: $Word
        a: Слово из справочника: {{$parseTree._Word.word}}

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

