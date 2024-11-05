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
    
require: functions.js

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
        a: Привет! Давай поиграем. Я буду называть страну, а ты угадываешь столицу.
        go!: /Do you want to start?
        
    state: Do you want to start?
        a: Начнем?

        state: Yes
            q: * [думаю] (да|*можете|*можешь|надеюсь|хотелось бы|давай|начнем) *
            script:
                 var randomPair = getRandomPair($Pairs);
                 var state = randomPair['value']['name'];
                 var capital = randomPair['value']['capital'];
                 var = capital
                 $reactions.answer("Отлично! Какая столица государства " + state + "? (Правильный ответ: " + capital + ")");
            
            state: CheckCapital
                q: * $Capital *
                script:
                    if (capital === $parseTree._Capital.name) {
                        correctAnswers += 1;
                        var newRandomPair = getRandomPair($Pairs);
                        if (newRandomPair) {
                            var newState = newRandomPair['value']['name'];
                            var newCapital = newRandomPair['value']['capital'];
                            $reactions.answer("Верно! Какая столица государства " + newState + "? (Правильный ответ: " + newCapital + ")");
                        } else {
                            $reactions.answer("Ура! Все столицы угаданы");
                        }
                    } else {
                        $reactions.answer("Неверный ответ! Попробуй еще раз");
                    }
 
        state: No
            q: * [уже] (ничем|не надо|не нужно|нет|не нач) [спасибо] *
            a: Хорошо. Буду рад поиграть в следующий раз!

    state: EndGame
        intent!: /end_game
        script:
             var correctAnswers = $memory.get('correctAsnswers') || 0;
             $reactions.answer("Игра завершена! Ты правильно назвал " + correctAnswers + " столиц.")

    state: Hello
        intent!: /привет
        a: Привет-привет, уже виделись :)
        
    state: Buy
        intent!: /пока
        a: Пока-пока

    state: NoMatch
        event!: noMatch
        a: Я предназначен только для игр! Не хотелось бы отходить от темы

    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /Start

