function getRandomPair(pairs) {
    var randomIndex = Math.floor(Math.random() * pairs.length);
    var pair = pairs[randomIndex];
    return pair;
}