package io.github.hejcz.domain.lottery;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
class Match {
    User giver;
    User recipient;
}
