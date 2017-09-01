package lesziy.carol.api.lottery;

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
}
