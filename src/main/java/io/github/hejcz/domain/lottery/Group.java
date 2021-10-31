package io.github.hejcz.domain.lottery;

import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.stream.Collectors;

record Group(Set<UserId> users) {

    Set<UserId> toSet() {
        return ImmutableSet.copyOf(users);
    }

    boolean hasMultipleMembers() {
        return users.size() > 1;
    }

    Set<Integer> membersIds() {
        return users.stream().map(UserId::id).collect(Collectors.toSet());
    }

}
