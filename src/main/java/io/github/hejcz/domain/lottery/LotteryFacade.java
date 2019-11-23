package io.github.hejcz.domain.lottery;

import java.util.Collection;
import java.util.Optional;

public interface LotteryFacade {

    void performLottery(Collection<Integer> participatingUsersIds);

    boolean annualLotteryNotPerformedYet();

    Optional<DtoWishGiver> actualRecipientWishes(Integer giverId);

    WishList wishesOf(Integer recipientId);

    void updateWishes(Integer recipientId, Collection<DtoWishRecipient> wishes);

    void deleteActualLottery();

    void lockWishes();

    void unlockWishes();
}
