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

# Для игры Виселица
require: hangmanGameData.csv
    name = HangmanGameData
    var = $HangmanGameData

patterns:
    $Word = $entity<HangmanGameData> || converter = function ($parseTree) {
        var id = $parseTree.HangmanGameData[0].value;
        return $HangmanGameData[id].value;
        };
#    $State = $entity<Pairs> || converter = function ($parseTree) {
#        var capital = $parseTree.Pairs[0].value;
#        for (var i = 0; i < $Pairs.length; i++) {
#            if ($Pairs[i][1] === capital) {
#                return $Pairs[i][0];
#            }
#        }
#        return null;
#    };

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
            // var Pairs = $parseTree.Pairs
            
            // Счетчик угаданных пар и массив использованных пар
            // var correctAnswers = 0;
            var usedPairs = [];
            var pair = $Pairs[Math.floor(Math.random() * $Pairs.length)];
            usedPairs.push(pair);
            $reactions.answer("Какая столица у государства " + pair[1] + "?");
 
    state: EndGame
        intent!: /end_game
        script:
             var correctAnswers = $memory.get("correctAnswers") || 0;
             $reactions.answer("Игра завершена! Ты правильно назвал " + correctAnswers + " столиц.")

    state: NoMatch
        event!: noMatch
        a: Я предназначен только для игр! Не хотелось бы отходить от темы

    state: Text
        q: $Word
        a: Слово из справочника: {{$parseTree._Word.word}}

    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /Start

