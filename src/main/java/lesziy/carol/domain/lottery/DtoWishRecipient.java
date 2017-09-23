package lesziy.carol.domain.lottery;

import lesziy.carol.domain.user.DbUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
