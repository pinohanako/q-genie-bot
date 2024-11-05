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
        a: Давай поиграем. Я буду называть страну, а ты угадываешь столицу.
        go!: /Do you want to start?
        
    state: Do you want to start?
        a: Напиши, хочешь начать?

        state: Yes
            q: * [думаю] (да|хочу|*можете|*можешь|надеюсь|хотелось бы|давай|начнем) *
            script:
                 var $originalPairs = $Pairs;
                 var randomPair = getRandomPair($Pairs);
                 $session.correctAnswers = 0;
                 $session.count = 0;

                 var state = randomPair['value']['name'];
                 var capital = randomPair['value']['capital'];

                 $session.capital = capital
                 $session.state = state
                 $reactions.answer("Отлично! Какая столица государства " + state + "? (Правильный ответ: " + capital + ")");
       
            state: CountryMatch
                q: * $Country *
                script:
                    if ($session.state == $parseTree._Country.name) {
                        $session.count++;
                        $reactions.answer("Это государство, а я спрашивал столицу!");
            
            state: CheckCapital
                q: * $Capital *
                if: $session.count % 5 === 0;
                    go!: /Do you want to start?/Yes/CheckCapital/GetGPTResponse
                else:
                script:
                    if ($session.capital === $parseTree._Capital.name) {
                        $session.correctAnswers++;
                        $session.count++;
                        var newRandomPair = getRandomPair($Pairs);
                        var newState = newRandomPair['value']['name'];
                        var newCapital = newRandomPair['value']['capital'];
                        $session.capital = newCapital
                        $reactions.answer("Продолжим! Какая столица государства " + newState + "? (Правильный ответ: " + newCapital + ")");
                    } else {
                        $session.count++;
                        $reactions.answer("Неверный ответ! Попробуй еще раз");
                    }

                state: GetGPTResponse
                    script:
                        var initialCapital = $parseTree._Capital.name
                        var userMessage = "Скажи какой-то интересный короткий факт о столице " + initialCapital
                        var assistantResponse = $gpt.createChatCompletion([{ "role": "user", "content": userMessage }]);
                        var response = assistantResponse.choices[0].message.content;
                        $reactions.answer(response);
                        
                        if ($session.capital === $parseTree._Capital.name) {
                            $session.correctAnswers++;
                            $session.count++;
                            var newRandomPair = getRandomPair($Pairs);
                            var newState = newRandomPair['value']['name'];
                            var newCapital = newRandomPair['value']['capital'];
                            $session.capital = newCapital
                            $reactions.answer("Продолжим! Какая столица государства " + newState + "? (Правильный ответ: " + newCapital + ")");
                        } else {
                            $session.count++;
                            $reactions.answer("Неверный ответ! Попробуй еще раз");
                        }

        state: No
            q: * [уже] (не надо|не хочу|не нужно|нет|не нач) [спасибо] *
            a: Хорошо. Буду рад поиграть в следующий раз!

    state: StartAgain
        q: * (еще раз|заново|повтор|старт|начн* заново|снова|нач* игру) *
        script:
            var randomPair = getRandomPair($Pairs);
            var state = randomPair['value']['name'];
            var capital = randomPair['value']['capital'];
            $session.count = 0;
            $session.correctAnswers = 0;

            $session.capital = capital
            $reactions.answer("А мы вошли во вкус! Какая столица государства " + state + "? (Правильный ответ: " + capital + ")");
            
    state: CapitalPattern
        q: * $Capital *
        if: $session.count % 5 === 0;
           go!: /CapitalPattern/GetGPTResponse
        else:
        script:
            if ($session.capital === $parseTree._Capital.name) {
                $session.correctAnswers++;
                $session.count++;
                
                var newRandomPair = getRandomPair($Pairs);
                var newState = newRandomPair['value']['name'];
                var newCapital = newRandomPair['value']['capital'];
                $session.capital = newCapital
                $session.state = newState
                $reactions.answer("Продолжим! Какая столица государства " + newState + "? (Правильный ответ: " + newCapital + ")");
            } else {
                $reactions.answer("Неа! Попробуй еще раз");
                $session.count++;
            }
            
        state: GetGPTResponse 
            script:
                var initialCapital = $parseTree._Capital.name
                var userMessage = "Скажи какой-то интересный короткий факт о столице " + initialCapital
                var assistantResponse = $gpt.createChatCompletion([{ "role": "user", "content": userMessage }]);
                var response = assistantResponse.choices[0].message.content;
                $reactions.answer(response);
                if ($session.capital === $parseTree._Capital.name) {
                    $session.correctAnswers++;
                    $session.count++;
                
                    var newRandomPair = getRandomPair($Pairs);
                    var newState = newRandomPair['value']['name'];
                    var newCapital = newRandomPair['value']['capital'];
                    $session.capital = newCapital
                    $reactions.answer("Продолжим! Какая столица государства " + newState + "? (Правильный ответ: " + newCapital + ")");
                } else {
                    $session.count++;
                    $reactions.answer("Неа! Попробуй еще раз");
                }

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
        a: Я не понял. Вы сказали: {{$request.query}}
             
    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /Start

