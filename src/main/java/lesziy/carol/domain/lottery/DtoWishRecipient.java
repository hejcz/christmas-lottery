package lesziy.carol.domain.lottery;

import lesziy.carol.domain.user.DbUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class DtoWishRecipient {
    private Integer id;
    private String text;

    DbWish toDb(DbUser recipient) {
        return new DbWish(
            id,
            text,
            recipient
        );
    }
}
