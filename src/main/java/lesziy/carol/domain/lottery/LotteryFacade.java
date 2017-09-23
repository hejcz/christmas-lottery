package lesziy.carol.domain.lottery;

import java.util.Collection;
import java.util.Optional;

public interface LotteryFacade {

    void performLottery();

    boolean annualLotteryNotPerformedYet();

    Optional<DtoWishGiver> actualRecipientWishes(Integer giverId);

    Collection<DtoWishRecipient> wishesOf(Integer recipientId);

    void updateWishes(Integer recipientId, Collection<DtoWishRecipient> wishes);

    void deleteActualLottery();
}
