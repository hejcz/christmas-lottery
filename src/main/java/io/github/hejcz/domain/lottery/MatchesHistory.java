package io.github.hejcz.domain.lottery;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

record MatchesHistory(Map<Match, Long> matches) {
}
