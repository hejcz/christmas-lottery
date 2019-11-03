package io.github.hejcz.web.resources;

import io.github.hejcz.domain.lottery.LotteryFacade;
import io.github.hejcz.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("api/lottery")
@RequiredArgsConstructor
class LotteryResource {

    private final LotteryFacade lotteryFacade;
    private final UserFacade userFacade;

    @PutMapping
    @Secured("ADMIN")
    void startLottery(@RequestBody Collection<Integer> participantsIds) {
        if (lotteryFacade.annualLotteryNotPerformedYet()) {
            lotteryFacade.performLottery(participantsIds);
        }
    }

    @DeleteMapping
    @Secured("ADMIN")
    void resetLottery() {
        lotteryFacade.deleteActualLottery();
    }

    @GetMapping
    @Secured("USER")
    RecipientWishes recipientsWishes() {
        return lotteryFacade.actualRecipientWishes(userFacade.loggedUserId())
            .map(DtoMapper::getRecipientWishes)
            .orElseGet(DtoMapper::noWishes);
    }

    @PutMapping("wishes/{id}/lock")
    @Secured("USER")
    @ResponseStatus(HttpStatus.OK)
    void lockWish(@PathVariable("id") Integer wishId) {
        lotteryFacade.lock(wishId);
    }

    @DeleteMapping("wishes/{id}/lock")
    @Secured("USER")
    @ResponseStatus(HttpStatus.OK)
    void unlockWish(@PathVariable("id") Integer wishId) {
        lotteryFacade.unlock(wishId);
    }

}
