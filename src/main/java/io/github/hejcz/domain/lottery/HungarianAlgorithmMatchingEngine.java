package io.github.hejcz.domain.lottery;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
class HungarianAlgorithmMatchingEngine implements MatchingEngine {

    // score negates strong components count, so it may not be < than -1
    public static final int MAX_POSSIBLE_SCORE = -1;
    private static final int IMPOSSIBLE_MATCH = Integer.MAX_VALUE;
    private static final int ITERATIONS = 500;

    /**
     * This is the most interesting part. Hungarian algorithm is deterministic, so for the same input
     * it will always return the same result. Some results are better and some are worse i.e.
     * - for some time it was normal that for 6 people results were A -> B, B -> A, C -> D, D -> C etc.
     * Once you knew it was pretty easy to guess who buys you a gift.
     * - for the same group an engine would return the same results in a cycle, so after X years, where
     * X is a (size of the group - 1) all future matches would be repeated.
     * To fix latter I shuffle users, so an input is different and to fix prior I run many iterations
     * (it was easier than improving an algorithm) and get a result without
     */
    @Override
    public AnnualMatches match(Group group, MatchesHistory matchesHistory,
            Collection<ForbiddenMatch> forbiddenMatches) {
        AnnualMatches best = null;
        long bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < ITERATIONS; i++) {
            OrderedUsers orderedUsers = orderUsers(group.toSet());
            AnnualMatches current = runAssignmentAlgorithm(matchesHistory, orderedUsers, forbiddenMatches);
            if (best == null) {
                best = current;
                bestScore = score(best);
            } else {
                long currentScore = score(current);
                if (bestScore < currentScore) {
                    best = current;
                    bestScore = currentScore;
                }
            }
            if (bestScore == MAX_POSSIBLE_SCORE) {
                break;
            }
        }
        return best;
    }

    private long score(AnnualMatches best) {
        return -1 * GraphHelper.countStrongComponents(best.matches());
    }

    private OrderedUsers orderUsers(Set<UserId> users) {
        int nextOrdinal = 0;
        List<UserId> shuffledUsers = new ArrayList<>(users);
        Collections.shuffle(shuffledUsers);
        Map<Integer, UserId> ordinalToUser = new HashMap<>();
        Map<Integer, OrderedUser> idToOrderedUser = new HashMap<>();
        for (UserId user : shuffledUsers) {
            int ordinal = nextOrdinal++;
            ordinalToUser.put(ordinal, user);
            idToOrderedUser.put(user.id(), new OrderedUser(user, ordinal));
        }
        return new OrderedUsers(ordinalToUser, idToOrderedUser);
    }

    private AnnualMatches runAssignmentAlgorithm(MatchesHistory matchesHistory,
            OrderedUsers orderedUsers, Collection<ForbiddenMatch> forbiddenMatches) {
        final double[][] costMatrix = createHungarianAlgorithmMatrix(matchesHistory, orderedUsers, forbiddenMatches);

        final int[] algorithmResult = new HungarianAlgorithm(costMatrix).execute();

        final Map<Integer, UserId> ordinalToUser = orderedUsers.ordinalToUser();
        final List<Match> matches = IntStream.range(0, algorithmResult.length)
                // algorithmResult[i] = j means i-th person buys gift for j-th person
                .mapToObj(i -> new Match(ordinalToUser.get(i), ordinalToUser.get(algorithmResult[i])))
                .collect(Collectors.toList());
        return new AnnualMatches(matches);
    }

    /**
     * matrix[i][j] means an i-th person should buy a gift for j-th person.
     * A value in a matrix represents how bad this match is - a higher value means a worse match.
     * Bad match is a match:
     * - from a forbidden list
     * - match with self
     * - match with person j who was gifted by i many times already
     */
    private double[][] createHungarianAlgorithmMatrix(MatchesHistory matchesHistory,
            OrderedUsers users, Collection<ForbiddenMatch> forbiddenMatches) {
        final Map<Integer, OrderedUser> userToOrderedUser = users.userToOrderedUser();
        final double[][] matrix = new double[userToOrderedUser.size()][userToOrderedUser.size()];

        matchesHistory.matches().forEach(((match, count) -> {
            OrderedUser giver = userToOrderedUser.get(match.giver().id());
            OrderedUser recipient = userToOrderedUser.get(match.recipient().id());
            if (giver != null && recipient != null) {
                matrix[giver.positionInMatrix()][recipient.positionInMatrix()] += count;
            }
        }));

        // prohibit match with self
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][i] = IMPOSSIBLE_MATCH;
        }

        // prohibit forbidden matches
        forbiddenMatches.forEach(forbiddenMatch -> {
            Integer first = userToOrderedUser.get(forbiddenMatch.getFirstUserId()).positionInMatrix();
            Integer second = userToOrderedUser.get(forbiddenMatch.getSecondUserId()).positionInMatrix();
            matrix[first][second] = IMPOSSIBLE_MATCH;
            matrix[second][first] = IMPOSSIBLE_MATCH;
        });

        return matrix;
    }

}
