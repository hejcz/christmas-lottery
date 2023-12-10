package io.github.hejcz.domain.lottery;

import java.util.Map;

record MatchesHistory(Map<Match, Long> annualMatches) {
}
