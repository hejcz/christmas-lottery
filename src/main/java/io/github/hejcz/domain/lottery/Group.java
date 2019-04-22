package io.github.hejcz.domain.lottery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

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

}
