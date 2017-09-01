package lesziy.carol.api.lottery;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
class OrderedUser {
    User user;
    Integer ordinal;
}
