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
        intent!: /ask_capital
        script:
            var data = $csv.read("geography-ru.csv");
            var randomIndex = Math.floor(Math.random() * data.length);
            var country = data[randomIndex][0];
            var capital = data[randomIndex][1];
            $reactions.answer("Какой город является столицей " + country + "?");
            $reactions.answer("(" + capital + ")");
            $reactions.wait();

    state: CheckAnswer
        intent!: /check_answer
        script:
            var userAnswer = $caila.inflect($parseTree._answer, ["nomn"]);
            if (userAnswer.toLowerCase() === capital.toLowerCase()) {
                $reactions.answer("Правильно! " + capital + " является столицей " + country + ".");
                $reactions.wait();
            } else {
                $reactions.answer("Ошибка! Правильный ответ: " + capital + ".");
                $reactions.wait();
            }

    state: EndGame
        intent!: /end_game
        script:
        var correctAnswers = $memory.get("correctAnswers") || 0;
        $reactions.answer("Игра завершена! Ты правильно назвал " + correctAnswers + " столиц.");

    state: CityPattern
        q: * $Capital *
        a: Столица: {{$parseTree._Capital.name}}
        
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

