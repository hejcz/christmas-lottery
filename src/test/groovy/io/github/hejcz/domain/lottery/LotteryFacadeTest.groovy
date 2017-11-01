package io.github.hejcz.domain.lottery

import spock.lang.Specification

/**
 * Ze względu na element losowości te testy służą raczej prezentacji warunków
 * silnika losującego, a nie zapobieganiu regresji.
 */
class LotteryFacadeTest extends Specification {
    private matchingEngine = new HungarianAlgorithmMatchingEngine()

    def "User can't gift himself"() {
        def users = usersWithIds([1, 2, 3])
        when:
        def matches = performMatching(users, emptyHistory())
        then:
        matches.every { it.giver() != it.recipient() }
    }

    def "Every user gifts someone"() {
        def users = usersWithIds([7, 8, 5, 6])
        when:
        def matches = performMatching(users, emptyHistory())
        then:
        matches.collect { it.giver() }.sort() == users.sort()
    }

    def "Every user receives gift"() {
        def users = usersWithIds([1, 6, 4, 9, 10])
        when:
        def matches = performMatching(users, emptyHistory())
        then:
        matches.collect { it.recipient() }.sort() == users.sort()
    }

    def "User won't gift same person two years in row if possible"() {
        def users = [8, 9, 1, 5].collect { User.with(it) }.toSet()
        def history = toHistory([[[8, 9], [9, 1], [1, 5], [5, 8]]])
        def historicalMatches = flattenHistory(history)
        when:
        def matches = performMatching(users, history)
        then:
        matches.every {
            !historicalMatches.contains(it)
        }
    }

    def "User will gift same person if other selection is not possible"() {
        def users = [8, 9, 1, 5].collect { User.with(it) }.toSet()
        def history = toHistory([
                [[8, 9], [9, 1], [1, 5], [5, 8]],
                [[8, 1], [9, 8], [5, 9]]
        ])
        when:
        def matches = performMatching(users, history)
        then:
        // wtedy 8 ma po raz pierwszy 5
        matches.contains(new Match(User.with(9), User.with(8))) ||
                // wtedy 9 ma po raz pierwszy 5
                matches.contains(new Match(User.with(8), User.with(9)))
    }

    def "Engine takes into consider multiple years"() {
        def users = [1, 2, 3, 4].collect { User.with(it) }.toSet()
        def history = toHistory([
                [[1, 2], [2, 3], [3, 4], [4, 1]],
                [[2, 4], [4, 2], [1, 3], [3, 1]]
        ])
        when:
        def matches = performMatching(users, history)
        then:
        matches.sort() == [match(1, 4), match(2, 1), match(3, 2), match(4, 3)]
    }

    private Collection<Match> performMatching(Set<User> users, MatchesHistory matchesHistory) {
        matchingEngine.match(new Users(users), matchesHistory).matches
    }

    private static Set<User> usersWithIds(List<Integer> ids) {
        ids.collect { User.with(it) }.toSet()
    }

    MatchesHistory emptyHistory() {
        new MatchesHistory([])
    }

    MatchesHistory toHistory(List<List<List<Integer>>> history) {
        new MatchesHistory(
                history.collect {
                    new AnnualMatches(
                            it.collect {
                                historicalMatch -> match(historicalMatch[0], historicalMatch[1])
                            }
                    )
                }
        )
    }

    private static Match match(Integer id1, Integer id2) {
        new Match(User.with(id1), User.with(id2))
    }

    private static Set flattenHistory(MatchesHistory history) {
        history.annualMatches.collectMany { it.matches }.toSet()
    }
}
