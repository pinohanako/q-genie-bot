var usedPairs = [];
function getRandomPair(pairs) {
    if (pairs.length === 0) {
        return null;
    }
    for(var randomIndex = 0; !usedPairs.includes(pairs[randomIndex]); i += 1) {
       var randomIndex = Math.floor(Math.random() * pairs.length)};
    // var randomIndex = Math.floor(Math.random() * pairs.length);
    var pair = pairs[randomIndex];
    usedPairs.push(pair);
    return pair;
}