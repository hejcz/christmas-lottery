package io.github.hejcz.web.ui.dashboard;

import io.github.hejcz.domain.lottery.DtoWishGiver;
import io.github.hejcz.domain.lottery.DtoWishRecipient;
import io.github.hejcz.domain.lottery.LotteryFacade;
import io.github.hejcz.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/dashboard")
@RequiredArgsConstructor
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
        model.addAttribute("users", new LotteryForm(userFacade.findRegularUsers()));
    }

    private void fillModelForUser(Model model) {
        Integer loggedUserId = loggedUserId();
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
        lotteryFacade.updateWishes(loggedUserId(), skipNullEntries(wishesForm));
        return "redirect:/dashboard";
    }

    private Integer loggedUserId() {
        return userFacade.loggedUserId();
    }

    private List<DtoWishRecipient> skipNullEntries(@ModelAttribute WishesForm wishesForm) {
        if (wishesForm.getWishes() == null) {
            wishesForm.setWishes(Collections.emptyList());
        }

        return wishesForm.getWishes()
            .stream()
            .filter(Objects::nonNull)
            .filter(dtoWishRecipient -> dtoWishRecipient.getText() != null)
            .filter(dtoWishRecipient -> !Objects.equals(dtoWishRecipient.getText(), ""))
            .collect(Collectors.toList());
    }

}
