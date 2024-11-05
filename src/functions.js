var usedPairs = [];
function getRandomPair(pairs) {
    if (pairs.length === 0) {
        return null;
    }
    var filteredPairs = pairs.filter(pair => !usedPairs.includes(pair));
    if (filteredPairs.length === 0) {
        return null;
    }
    var randomIndex = Math.floor(Math.random() * filteredPairs.length);
    var pair = filteredPairs[randomIndex];
    usedPairs.push(pair);
    return pair;
}