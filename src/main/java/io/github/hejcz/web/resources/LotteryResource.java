package io.github.hejcz.web.resources;

import io.github.hejcz.domain.lottery.DtoWishGiver;
import io.github.hejcz.domain.lottery.LotteryFacade;
import io.github.hejcz.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("lottery")
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
    @Secured({"USER"})
    RecipientWishes recipientsWishes() {
        return lotteryFacade.actualRecipientWishes(userFacade.loggedUserId())
            .map(this::getRecipientWishes)
            .orElseGet(this::noWishes);
    }

    private RecipientWishes getRecipientWishes(DtoWishGiver oldDto) {
        RecipientWishes recipientWishes = new RecipientWishes();
        recipientWishes.setRecipient(oldDto.recipient());
        recipientWishes.setWishes(oldDto.recipientWishes().stream().map(oldWish -> {
            Wish wish = new Wish();
            wish.setId(oldWish.getId());
            wish.setPower(oldWish.getPower());
            wish.setText(oldWish.getText());
            wish.setUrl(oldWish.getUrl());
            return wish;
        }).collect(Collectors.toList()));
        return recipientWishes;
    }

    private RecipientWishes noWishes() {
        RecipientWishes recipientWishes = new RecipientWishes();
        recipientWishes.setRecipient(null);
        recipientWishes.setWishes(Collections.emptyList());
        return recipientWishes;
    }

}
