package lesziy.carol.web.ui.dashboard;

import lesziy.carol.domain.lottery.DtoWishGiver;
import lesziy.carol.domain.lottery.LotteryFacade;
import lesziy.carol.domain.user.DtoUser;
import lesziy.carol.domain.user.SystemRole;
import lesziy.carol.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping(value = "/dashboard")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
class DashboardController {

    private final LotteryFacade lotteryFacade;

    private final UserFacade userFacade;

    @GetMapping
    public String dashboard(Model model) {
        fillDashboardModel(model);
        return "dashboard";
    }

    private void fillDashboardModel(Model model) {
        DtoUser loggedUser = userFacade.loggedUserOrException();
        Optional<DtoWishGiver> recipientWishes = lotteryFacade.actualRecipientWishes(loggedUser.id());
        model.addAttribute("isAdmin", loggedUser.systemRole() == SystemRole.ADMIN);
        model.addAttribute("canPerformLottery", canPerformLottery());
        model.addAttribute("myWishes", new WishesForm(lotteryFacade.wishesOf(loggedUser.id())));
        model.addAttribute("hasRecipient", recipientWishes.isPresent());
        recipientWishes.ifPresent(recipient -> model.addAttribute("recipientWithWishes", recipient));
    }

    private boolean canPerformLottery() {
        return lotteryFacade.annualLotteryNotPerformedYet();
    }

    @PostMapping("/lottery")
    public String startLottery() {
        if (canPerformLottery()) {
            lotteryFacade.performLottery();
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/editWishes")
    public String editWishes(@ModelAttribute WishesForm wishesForm) {
        System.out.println(wishesForm);
        return "redirect:/dashboard";
    }

}
