package lesziy.carol.domain.lottery;

import lesziy.carol.domain.user.DbUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoWishRecipient {
    private Integer id;
    private String text;
    private Integer power;

    DbWish toDb(DbUser recipient) {
        return new DbWish(
            id,
            Timestamp.valueOf(LocalDateTime.now()),
            text,
            power,
            recipient
        );
    }
}
