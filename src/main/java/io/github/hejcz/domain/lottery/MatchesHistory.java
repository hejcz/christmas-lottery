package io.github.hejcz.domain.lottery;

import lombok.Value;

import java.util.Collection;

@Value
class MatchesHistory {
    Collection<AnnualMatches> annualMatches;
}
