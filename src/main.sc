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
        
    state: Game
        q: $regex</game>
        script:
            var geographyData = $csv.read("geography-ru.csv");
            var randomIndex = Math.floor(Math.random() * geographyData.length);
            var question = "Какой город является столицей " + geographyData[randomIndex]["Государство"] + "?";
            var correctAnswer = geographyData[randomIndex]["Столица"];
        a: {{question}} ({{correctAnswer}}).

    state: PlayerAnswer
        q: $Word
        a: {{$parseTree._Word.word}}

    state: CheckAnswer
        script:
            var playerAnswer = $parseTree._Word.word;
            var isCorrect = playerAnswer.toLowerCase() === correctAnswer.toLowerCase();
            if (isCorrect) {
                $jsapi.say("Правильно! Вы угадали столицу.");
            } else {
                $jsapi.say("Ошибка! Правильный ответ: " + correctAnswer + ". Попробуем еще раз.");
            }

    state: ContinueGame
        q: $regex</continue>
        script:
           var geographyData = $csv.read("geography-ru.csv");
           var randomIndex = Math.floor(Math.random() * geographyData.length);
           var question = "Какой город является столицей " + geographyData[randomIndex]["Государство"] + "?";
           var correctAnswer = geographyData[randomIndex]["Столица"];
        go!: /Game

    state: EndGame
        q: $regex</end>
        script:
             $jsapi.say("Спасибо за игру! Вы угадали X столиц из Y.");
             $session = {};
             $client = {};
        go!: /Start

    state: CityPattern
        q: * $Capital *
        a: Столица: {{$parseTree._Capital.name}}
        
    state: Text
        q: $Word
        a: Слово из справочника: {{$parseTree._Word.word}}

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /Start

