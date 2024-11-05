var usedPairs = [];
function getRandomPair(pairs) {
    if (pairs.length === 0) {
        return null;
    }
    var randomIndex = Math.floor(Math.random() * pairs.length);
    var pair = pairs.splice(randomIndex, 1)[0];
    //var pair = pairs[randomIndex];
    usedPairs.push(pair);
    return pair;
}