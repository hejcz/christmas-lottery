package lesziy.carol.web.ui.dashboard;

import lesziy.carol.domain.lottery.DtoWishRecipient;
import lesziy.carol.domain.lottery.LotteryFacade;
import lesziy.carol.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
class WishesController {

    private final LotteryFacade lotteryFacade;

    private final UserFacade userFacade;

    @GetMapping("/wishes")
    public Collection<DtoWishRecipient> myWishes() {
        return lotteryFacade.wishesOf(userFacade.loggedUserOrException().id());
    }

    @PutMapping("/wishes")
    public String myWishes(@RequestBody Collection<DtoWishRecipient> updatedWishes) {
        System.out.println(updatedWishes);
        return "OK";
    }

}
