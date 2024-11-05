var usedPairs = [];
function getRandomPair(pairs) {
    if (pairs.length === 0) {
        return null;
    }
    pairs = pairs.filter(pair = !usedPairs.includes(pair));
    var randomIndex = Math.floor(Math.random() * pairs.length);
    var pair = pairs[randomIndex];
    usedPairs.push(pair);
    return pair;
}