var usedPairs = [];
function getRandomPair(pairs) {
    if (pairs.length === 0) {
        return null;
    }

    var randomIndex = Math.floor(Math.random() * pairs.length);
    var pair = pairs[randomIndex];

    // Сохраняем использованные пары
    usedPairs.push(pair);

    return pair;
}
