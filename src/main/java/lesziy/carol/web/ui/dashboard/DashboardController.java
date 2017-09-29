package lesziy.carol.web.ui.dashboard;

import com.google.common.base.Strings;
import lesziy.carol.domain.lottery.DtoWishGiver;
import lesziy.carol.domain.lottery.DtoWishRecipient;
import lesziy.carol.domain.lottery.LotteryFacade;
import lesziy.carol.domain.user.DtoUser;
import lesziy.carol.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/dashboard")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
class DashboardController {

    private final LotteryFacade lotteryFacade;

    private final UserFacade userFacade;

    @GetMapping
    public String dashboard(Model model) {
        if (userFacade.isLoggedUserAdmin()) {
            fillModelForAdmin(model);
            return "admin_dashboard";
        }

        fillModelForUser(model);
        return "dashboard";
    }

    private void fillModelForAdmin(Model model) {
        model.addAttribute("canPerformLottery", canPerformLottery());
        model.addAttribute("users", new LotteryForm(userFacade.loadUsers()));
    }

    private void fillModelForUser(Model model) {
        Integer loggedUserId = loggedUser().id();
        Optional<DtoWishGiver> recipientWishes = lotteryFacade.actualRecipientWishes(loggedUserId);
        model.addAttribute("myWishes", new WishesForm(lotteryFacade.wishesOf(loggedUserId)));
        model.addAttribute("hasRecipient", recipientWishes.isPresent());
        model.addAttribute("lotteryPerformed", !lotteryFacade.annualLotteryNotPerformedYet());
        recipientWishes.ifPresent(recipient -> model.addAttribute("recipientWithWishes", recipient));
    }

    private boolean canPerformLottery() {
        return lotteryFacade.annualLotteryNotPerformedYet();
    }

    @Transactional
    @PostMapping("/lottery")
    public String startLottery(@ModelAttribute LotteryForm users) {
        if (canPerformLottery()) {
            lotteryFacade.performLottery(skipUnchecked(users));
        }
        return "redirect:/dashboard";
    }

    private Collection<Integer> skipUnchecked(LotteryForm lotteryForm) {
        return lotteryForm.getUsersForLottery()
            .stream()
            .filter(UserForLottery::isConsideredInLottery)
            .map(UserForLottery::getId)
            .collect(Collectors.toSet());
    }

    @Transactional
    @PostMapping("/resetLottery")
    public String resetLottery() {
        lotteryFacade.deleteActualLottery();
        return "redirect:/dashboard";
    }

    @PostMapping("/editWishes")
    public String editWishes(@ModelAttribute WishesForm wishesForm) {
        System.out.println(wishesForm);
        lotteryFacade.updateWishes(loggedUser().id(), skipNullEntries(wishesForm));
        return "redirect:/dashboard";
    }

    private DtoUser loggedUser() {
        return userFacade.loggedUserOrException();
    }

    private List<DtoWishRecipient> skipNullEntries(@ModelAttribute WishesForm wishesForm) {
        if (wishesForm.getWishes() == null) {
            wishesForm.setWishes(Collections.emptyList());
        }

        return wishesForm.getWishes()
            .stream()
            .filter(dtoWishRecipient -> dtoWishRecipient != null)
            .filter(dtoWishRecipient -> !Strings.isNullOrEmpty(dtoWishRecipient.getText()))
            .collect(Collectors.toList());
    }

}
