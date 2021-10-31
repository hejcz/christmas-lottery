package io.github.hejcz.domain.lottery;

import java.util.List;

public record WishListChange(List<DtoWishRecipient> oldWishes,
                             List<DtoWishRecipient> newWishes) {
    public boolean wasEmptyBefore() {
        return oldWishes.isEmpty();
    }
}
