function getRandomPair(pairs) {
    var randomIndex = Math.floor(Math.random() * pairs.length);
    var pair = pairs[randomIndex];
    usedPairs.push(pair);
    return pair;
}