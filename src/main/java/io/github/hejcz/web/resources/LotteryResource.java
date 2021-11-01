package io.github.hejcz.web.resources;

import io.github.hejcz.domain.lottery.LotteryFacade;
import io.github.hejcz.domain.user.UserFacade;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/lottery")
class LotteryResource {

    private final LotteryFacade lotteryFacade;
    private final UserFacade userFacade;

    public LotteryResource(LotteryFacade lotteryFacade, UserFacade userFacade) {
        this.lotteryFacade = lotteryFacade;
        this.userFacade = userFacade;
    }

    @PutMapping
    @Secured("ADMIN")
    void startLottery(@RequestBody StartLotteryDto startLotteryDto) {
        lotteryFacade.performLottery(startLotteryDto.groupId(), List.copyOf(startLotteryDto.participantsIds()))
    }

    @GetMapping("/admin")
    @Secured("ADMIN")
    boolean isLotteryPerformed(@RequestParam("groupId") int groupId) {
        return lotteryFacade.isLotteryRunning(groupId);
    }

    @DeleteMapping
    @Secured("ADMIN")
    void resetLottery(ResetLotteryDto resetLotteryDto) {
        lotteryFacade.deleteActualLottery(resetLotteryDto.groupId());
    }

    @GetMapping
    @Secured("USER")
    RecipientWishes recipientsWishes(@RequestParam("groupId") int groupId) {
        return lotteryFacade.getMatchWishes(userFacade.loggedUserId(), groupId)
                .map(DtoMapper::getRecipientWishes)
                .orElseGet(DtoMapper::noWishes);
    }

    @PutMapping("wishes/lock")
    @Secured("USER")
    void lockWishes(LockWishesDto lockWishesDto) {
        lotteryFacade.lockWishes(userFacade.loggedUserId(), lockWishesDto.groupId());
    }

    @DeleteMapping("wishes/lock")
    @Secured("USER")
    void unlockWishes(UnlockWishesDto unlockWishesDto) {
        lotteryFacade.unlockWishes(userFacade.loggedUserId(), unlockWishesDto.groupId());
    }

}
