package io.github.hejcz.domain.lottery;

import lombok.Value;

import java.util.Collection;

@Value
public class WishList {
    boolean isLocked;
    Collection<DtoWishRecipient> wishes;
}
