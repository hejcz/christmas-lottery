package lesziy.carol.domain.lottery;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
class Users {
    @Getter(AccessLevel.NONE) Set<User> users;

    Set<User> toSet() {
        return new HashSet<>(users);
    }

    boolean moreThanOne() {
        return users.size() > 1;
    }
}
