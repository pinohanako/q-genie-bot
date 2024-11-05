var usedPairs = [];

function getRandomPair(pairs) {
    if (Array.isArray(pairs) && pairs.length > 0) {
        var filteredPairs = pairs.filter(function(pair) {
            return !usedPairs.includes(pair);
        });

        if (filteredPairs.length > 0) {
            var randomIndex = Math.floor(Math.random() * filteredPairs.length);
            var pair = filteredPairs[randomIndex];
            usedPairs.push(pair);
            return pair;
        } else {
            return null;
        }
    } else {
        return null;
    }
}
