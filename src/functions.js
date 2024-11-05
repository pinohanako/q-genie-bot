var usedPairs = [];

function getNextPair(pairs) {
    if (pairs.length === 0) {
        return null;
    } else {
        const lastPair = pairs.pop();
        usedPairs.push(lastPair);
        return lastPair;
    }
}

// var usedPairs = [];
//function getRandomPair(pairs) {
//    if (pairs.length === 0) {
//        return null;
//    }
//    var randomIndex = Math.floor(Math.random() * pairs.length);
//    var pair = pairs[randomIndex];
//    usedPairs.push(pair);
//    return pair;
//}