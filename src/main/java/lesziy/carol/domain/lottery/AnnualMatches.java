package lesziy.carol.domain.lottery;

import lombok.Value;

import java.util.Collection;

@Value
class AnnualMatches {
    Collection<Match> matches;
}
