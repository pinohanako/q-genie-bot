var usedPairs = [];
function getRandomPair(pairs) {
    if (pairs.length === 0) {
        return null;
    }
    for(var randomIndex = 0; randomIndex !in usedPairs; i += 1) {
        randomIndex = Math.floor(Math.random() * pairs.length);
    }
    var pair = pairs[randomIndex];
    usedPairs.push(pair);
    return pair;
}