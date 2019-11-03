package io.github.hejcz.web.resources;

import io.github.hejcz.domain.lottery.LotteryFacade;
import io.github.hejcz.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users/current/wish-list")
@RequiredArgsConstructor
class WishListResource {

    private final LotteryFacade lotteryFacade;
    private final UserFacade userFacade;

    @GetMapping
    @Secured("USER")
    Collection<Wish> loggedUserWishList() {
        return DtoMapper.mapWishes(lotteryFacade.wishesOf(userFacade.loggedUserId()));
    }

    @PutMapping
    @Secured("USER")
    void updateLoggedUserWishList(@RequestBody Collection<Wish> wishList) {
        lotteryFacade.updateWishes(
            userFacade.loggedUserId(),
            wishList.stream().map(Wish::toOldDto).collect(Collectors.toList()));
    }

}
