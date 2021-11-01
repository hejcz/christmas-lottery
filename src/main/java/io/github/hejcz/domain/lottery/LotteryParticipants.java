package io.github.hejcz.domain.lottery;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

record LotteryParticipants(Set<UserId> ids) {

    Set<UserId> toSet() {
        return ImmutableSet.copyOf(ids);
    }

    Set<Integer> toActualIds() {
        return ids.stream()
                .map(UserId::id)
                .collect(Collectors.toSet());
    }

}
