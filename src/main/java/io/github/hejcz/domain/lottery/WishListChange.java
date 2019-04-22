package io.github.hejcz.domain.lottery;

import lombok.Value;

import java.util.List;

@Value
public class WishListChange {

    List<DtoWishRecipient> oldWishes;

    List<DtoWishRecipient> newWishes;

    public boolean wasEmptyBefore() {
        return oldWishes.isEmpty();
    }

}
