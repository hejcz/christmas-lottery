package io.github.hejcz.domain.lottery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Value
class Group {

    @Getter(AccessLevel.NONE)
    Set<User> users;

    Set<User> toSet() {
        return new HashSet<>(users);
    }

    boolean hasMultipleMembers() {
        return users.size() > 1;
    }

    Set<Integer> membersIds() {
        return users.stream().map(User::getId).collect(Collectors.toSet());
    }

}
