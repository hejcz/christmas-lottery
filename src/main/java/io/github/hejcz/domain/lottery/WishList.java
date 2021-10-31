package io.github.hejcz.domain.lottery;

import java.util.Collection;

public record WishList(boolean isLocked, Collection<DtoWishRecipient> wishes) {
}
