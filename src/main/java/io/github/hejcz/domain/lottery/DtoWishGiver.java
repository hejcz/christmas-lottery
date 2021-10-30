package io.github.hejcz.domain.lottery;

import java.util.Collection;

public record DtoWishGiver(String recipientName, String recipientSurname, boolean locked,
                           Collection<DtoWishRecipient> recipientWishes) {
}
