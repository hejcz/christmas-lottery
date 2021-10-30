package io.github.hejcz.web.resources;

import io.github.hejcz.domain.lottery.LotteryFacade;
import io.github.hejcz.domain.user.UserFacade;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users/current/wish-list")
class WishListResource {

    private final LotteryFacade lotteryFacade;
    private final UserFacade userFacade;

    public WishListResource(LotteryFacade lotteryFacade, UserFacade userFacade) {
        this.lotteryFacade = lotteryFacade;
        this.userFacade = userFacade;
    }

    @GetMapping
    @Secured("USER")
    WishList loggedUserWishList() {
        io.github.hejcz.domain.lottery.WishList wishList = lotteryFacade.wishesOf(userFacade.loggedUserId());
        return new WishList(DtoMapper.mapWishes(wishList.wishes()), wishList.isLocked());
    }

    @PutMapping
    @Secured("USER")
    void updateLoggedUserWishList(@RequestBody Collection<Wish> wishList) {
        lotteryFacade.updateWishes(
                userFacade.loggedUserId(),
                wishList.stream().map(Wish::toOldDto).collect(Collectors.toSet()));
    }

}
