package io.github.hejcz.web.resources;

import io.github.hejcz.domain.lottery.DtoWishRecipient;
import io.github.hejcz.domain.lottery.LotteryFacade;
import io.github.hejcz.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("user/wish-list")
@RequiredArgsConstructor
class WishListResource {

    private final LotteryFacade lotteryFacade;
    private final UserFacade userFacade;

    @GetMapping
    @Secured("USER")
    Collection<DtoWishRecipient> loggedUserWishList() {
        return lotteryFacade.wishesOf(userFacade.loggedUserOrException().id());
    }

}
