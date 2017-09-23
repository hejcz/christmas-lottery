package lesziy.carol.domain.lottery;

import lombok.Value;

import java.util.List;

@Value
public class WishesUpdate {
    List<DtoWishRecipient> oldWishes;
    List<DtoWishRecipient> newWishes;
}
