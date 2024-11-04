require: geography-ru.csv
    name = Pairs
    var = $Pairs
    strict = true

function getRandomState() {
    var pairs = $Pairs;
    if (pairs.length > 0) {
        var randomIndex = Math.floor(Math.random() * pairs.length);
        return pairs[randomIndex][0];
    } else {
        return null;
    }
}

module.exports = {
    getRandomState: getRandomState
};