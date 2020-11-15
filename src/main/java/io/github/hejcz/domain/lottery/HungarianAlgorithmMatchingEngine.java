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
import java.util.stream.Stream;

@Component
class HungarianAlgorithmMatchingEngine implements MatchingEngine {

    private static final int IMPOSSIBLE = Integer.MAX_VALUE;
    private static final int ITERATIONS = 500;

    @Override
    public AnnualMatches match(Group group, MatchesHistory matchesHistory, Collection<ForbiddenMatch> forbiddenMatches) {
        AnnualMatches best = null;
        for (int i = 0; i < ITERATIONS; i++) {
            OrderedUsers orderedUsers = orderUsers(group.toSet());
            AnnualMatches current = performAssignment(matchesHistory, orderedUsers, forbiddenMatches);
            if (best == null) {
                best = current;
            } else {
                long bestScore = score(best);
                if (bestScore == 0) {
                    return best;
                }
                if (bestScore < score(current)) {
                    best = current;
                }
            }
        }
        return best;
    }

    private long score(AnnualMatches best) {
        Collection<Match> matches = best.getMatches();
        return -matches.stream()
            .filter(it -> matches.contains(new Match(it.recipient(), it.giver())))
            .count();
    }

    /**
     * Każdemu użytkownikowi przypisuje kolejną liczbę naturalną.
     * Algorytm węgierski jest deterministyczny. Żeby wprowadzić element losowości można
     * losować liczbę porządkową użytkownika. Jeżeli użytkownicy A i B nie wylosowali
     * użytkowników C i D ani razu, a wszyscy pozostali użytkownicy wylosowali ich co najmniej raz
     * to w zależności od liczby porządkowej A i B silnik wyznaczy kombinacje
     * A -> C i B -> D lub A -> D i B -> C. Wynika to z zasad działania algorytmu.
     */
    private OrderedUsers orderUsers(Set<User> users) {
        int nextOrdinal = 0;
        List<User> shuffledUsers = new ArrayList<>(users);
        Collections.shuffle(shuffledUsers);
        Map<Integer, User> ordinalToUser = new HashMap<>();
        Map<Integer, OrderedUser> idToOrderedUser = new HashMap<>();
        for (User user : shuffledUsers) {
            int ordinal = nextOrdinal++;
            ordinalToUser.put(ordinal, user);
            idToOrderedUser.put(user.getId(), new OrderedUser(user, ordinal));
        }
        return new OrderedUsers(ordinalToUser, idToOrderedUser);
    }

    private AnnualMatches performAssignment(MatchesHistory matchesHistory,
                                            OrderedUsers orderedUsers,
                                            Collection<ForbiddenMatch> forbiddenMatches) {
        double[][] costMatrix = createCostMatrix(matchesHistory, orderedUsers, forbiddenMatches);
        int[] algorithmResult = new HungarianAlgorithm(costMatrix).execute();
        return convertToAnnualMatches(algorithmResult, orderedUsers);
    }

    /**
     * @return macierz kosztów będąca podawana na wejście algorytmu węgierskiego.
     * Kosztem jest liczba dopasowań w historii takich że i-ty user kupował prezent j-temu.
     */
    private double[][] createCostMatrix(MatchesHistory matchesHistory,
                                        OrderedUsers users,
                                        Collection<ForbiddenMatch> forbiddenMatches) {
        Map<Integer, OrderedUser> userToOrderedUser = users.getUserToOrderedUser();
        double[][] matrix = new double[userToOrderedUser.size()][userToOrderedUser.size()];

        for (AnnualMatches annualMatches : matchesHistory.getAnnualMatches()) {
            for (Match match : annualMatches.getMatches()) {
                OrderedUser giver = userToOrderedUser.get(match.giver().getId());
                OrderedUser recipient = userToOrderedUser.get(match.recipient().getId());
                if (giver != null && recipient != null) {
                    ++matrix[giver.ordinal()][recipient.ordinal()];
                }
            }
        }

        // nie można kupować prezentu samemu sobie
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][i] = IMPOSSIBLE;
        }

        forbiddenMatches.forEach(forbiddenMatch -> {
            Integer first = userToOrderedUser.get(forbiddenMatch.firstUserId).ordinal();
            Integer second = userToOrderedUser.get(forbiddenMatch.secondUserId).ordinal();
            matrix[first][second] = IMPOSSIBLE;
            matrix[second][first] = IMPOSSIBLE;
        });

        return matrix;
    }

    /**
     * Przekształca wynik algorytmu węgierskiego w pary kupujący - obdarowywany.
     *
     * @param hungarianAlgorithmResult jednowymiarowa tablica gdzie wartość j pod tym i-tym indeksem
     *                                 oznacza że i-ty użytkownik kupuje j-temu użytkownikowi prezent.
     * @param orderedUsers             uporządkowana kolekcja użytkowników.
     */
    private AnnualMatches convertToAnnualMatches(int[] hungarianAlgorithmResult,
                                                 OrderedUsers orderedUsers) {
        Map<Integer, User> ordinalToUser = orderedUsers.getOrdinalToUser();
        return new AnnualMatches(
            Stream.iterate(0, i -> ++i)
                .limit(hungarianAlgorithmResult.length)
                .map(i -> new Match(ordinalToUser.get(i), ordinalToUser.get(hungarianAlgorithmResult[i])))
                .collect(Collectors.toList())
        );
    }

}
