require: geography-ru.csv
    name = Pairs
    var = $Pairs
    strict = true

function getRandomPair(pairs) {
    if (pairs.length === 0) {
        return null;
    }

    const randomIndex = Math.floor(Math.random() * pairs.length);
    const pair = pairs[randomIndex];

    // Сохраняем использованные пары
    usedPairs.push(pair);

    return pair;
}
