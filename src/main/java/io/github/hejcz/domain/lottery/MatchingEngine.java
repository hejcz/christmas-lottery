package io.github.hejcz.domain.lottery;

/**
 * Silnik wyznaczający, który użytkownik kupuje któremu prezent w danym roku.
 */
interface MatchingEngine {

    /**
     * Na podstawie historii losowań zwraca pary użytkowników, w których jedna osoba kupuje
     * prezent drugiej (nie sobie nawzajem) zgodnie z założeniami:
     * - każdy użytkownik kupuje komuś prezent
     * - każdy użytkownik dostaje od kogoś prezent
     * - użytkownik nie może kupować prezentu sobie
     * - dąży do tego, żeby każdy użytkownik obdarowywał pozostałych podobną liczbę razy, np.
     * (2 x A -> B, 2 x A -> C) zamiast (3 x A -> B, 1 x A -> C)
     *
     * @param group          - użytkownicy biorący udział w losowaniu.
     * @param matchesHistory - historia wyników losowania.
     * @return wyniki losowania.
     */
    AnnualMatches match(Group group, MatchesHistory matchesHistory);

}
