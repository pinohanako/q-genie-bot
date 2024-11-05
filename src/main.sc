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
                 var $originalPairs = $Pairs;
                 var randomPair = getRandomPair($Pairs);
                 $session.correctAnswers = 0;
                 $session.count = 0;

                 var index = randomPair['id'];
                 var state = randomPair['value']['name'];
                 var capital = randomPair['value']['capital'];

                 $session.capital = capital
                 $reactions.answer("Отлично! Какая столица государства " + state + "? (Правильный ответ: " + capital + ")");

            state: CheckCapital
                q: * $Capital *
                script:
                    $session.count++;
                    if ($session.count % 5 === 0) {
                        go: GetGPTResplonse
                    }
                    if ($session.capital === $parseTree._Capital.name) {
                        $session.correctAnswers++;
                        var newRandomPair = getRandomPair($Pairs);
                        if (newRandomPair) {
                            var newState = newRandomPair['value']['name'];
                            var newCapital = newRandomPair['value']['capital'];
                            $session.capital = newCapital
                            $reactions.answer("Продолжим! Какая столица государства " + newState + "? (Правильный ответ: " + newCapital + ")");
                        } else {
                            $reactions.answer("Ура! Все столицы угаданы");
                        }
                    } else {
                        $reactions.answer("Неверный ответ! Попробуй еще раз");
                    }
                    
                state: GetGPTResponse 
                    q: * $Capital *
                    script:
                        //$session.count--;
                        var initialCapital = $parseTree._Capital.name
                        var userMessage = "Скажи какой-то интересный короткий факт о столице " + initialCapital
                        var assistantResponse = $gpt.createChatCompletion([{ "role": "user", "content": userMessage }]);
                        var response = assistantResponse.choices[0].message.content;
                        $reactions.answer(response);
                    go!: /Do you want to start?/Yes/CheckCapital
 
        state: No
            q: * [уже] (ничем|не надо|не нужно|нет|не нач) [спасибо] *
            a: Хорошо. Буду рад поиграть в следующий раз!

    state: StartAgain
        q: * (еще раз|заново|повтор|старт|еще|продолж*|начн*) *
        script:
            var randomPair = getRandomPair($Pairs);
            var index = randomPair['id'];
            var state = randomPair['value']['name'];
            var capital = randomPair['value']['capital'];
            $session.correctAnswers = 0;

            $session.capital = capital
            $reactions.answer("А мы вошли во вкус! Какая столица государства " + state + "? (Правильный ответ: " + capital + ")");

    state: CapitalPattern
        q: * $Capital *
        script:
            $session.count++;
            if ($session.count % 5 === 0) {
                go: GetGPTResplonse
            }
            if ($session.capital === $parseTree._Capital.name) {
                $session.correctAnswers++;
                if ($session.correctAnswers % 5 === 0) {
                    var initialCapital = $parseTree._Capital.name
                    var userMessage = "Скажи какой-то интересный короткий факт о столице " + initialCapital
                    var assistantResponse = $gpt.createChatCompletion([{ "role": "user", "content": userMessage }]);
                    var response = assistantResponse.choices[0].message.content;
                    $reactions.answer(response);
                }
                
                var newRandomPair = getRandomPair($Pairs);
                if (newRandomPair) {
                    var newState = newRandomPair['value']['name'];
                    var newCapital = newRandomPair['value']['capital'];
                    $session.capital = newCapital
                    $reactions.answer("Продолжим! Какая столица государства " + newState + "? (Правильный ответ: " + newCapital + ")");
                } else {
                    $reactions.answer("Ура! Все столицы угаданы");
                }
            } else {
                $reactions.answer("Неа! Попробуй еще раз");
            }
        state: GetGPTResponse 
            q: * $Capital *
            script:
                //$session.count--;
                var initialCapital = $parseTree._Capital.name
                var userMessage = "Скажи какой-то интересный короткий факт о столице " + initialCapital
                var assistantResponse = $gpt.createChatCompletion([{ "role": "user", "content": userMessage }]);
                var response = assistantResponse.choices[0].message.content;
                $reactions.answer(response);
            go!: /CapitalPattern

    state: EndGame
        intent!: /end_game
        script:
             var correctAnswers = $session.correctAnswers || 0;
             $reactions.answer("Молчу! Количество правильных ответов: " + correctAnswers + ".")

    state: Hello
        intent!: /привет
        a: Привет-привет, уже виделись :)
        
    state: Buy
        intent!: /пока
        a: Пока-пока

    state: NoMatch
        event!: noMatch 
        script:
             var userMessage = $request.query;
             var assistantResponse = $gpt.createChatCompletion([{ "role": "user", "content": userMessage }]);
             var response = assistantResponse.choices[0].message.content;
             $reactions.answer(response);
             
    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /Start

