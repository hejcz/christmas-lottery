package io.github.hejcz.domain.lottery;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LotteryFacade {

    void performLottery(int groupId, List<Integer> participantsIds);

    boolean isLotteryRunning(int groupId);

    Optional<DtoWishGiver> getMatchWishes(Integer giverId, int groupId);

    WishList wishesOf(Integer recipientId, int groupId);

    void updateWishes(Integer recipientId, int groupId, Set<DtoWishRecipient> wishes);

    void deleteActualLottery(int groupId);

    void lockWishes(Integer giverId, int groupId);

    void unlockWishes(Integer giverId, int groupId);
}
