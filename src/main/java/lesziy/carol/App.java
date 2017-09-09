package lesziy.carol;

import com.google.common.collect.ImmutableList;
import lesziy.carol.domain.lottery.DtoWishRecipient;
import lesziy.carol.domain.lottery.LotteryFacade;
import lesziy.carol.domain.registration.RegistrationFacade;
import lesziy.carol.domain.user.DtoUser;
import lesziy.carol.domain.user.SystemRole;
import lesziy.carol.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class App implements CommandLineRunner {
    private final RegistrationFacade registrationFacade;
    private final LotteryFacade lotteryFacade;
    private final UserFacade userFacade;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        registrationFacade
            .register(new DtoUser(0, "admin", "somepassword", "rubin94@gmail.com", SystemRole.ADMIN));
        registrationFacade
            .register(new DtoUser(1, "a", "somepassword", "rubin94@gmail.com", SystemRole.USER));
        registrationFacade
            .register(new DtoUser(2, "b", "somepassword", "rubin94@gmail.com", SystemRole.USER));
        registrationFacade
            .register(new DtoUser(3, "c", "somepassword", "rubin94@gmail.com", SystemRole.USER));

        lotteryFacade.performLottery();

        Integer userAId = userFacade.findByLogin("a").get().id();

        lotteryFacade.updateWishes(userAId, ImmutableList.of(
            new DtoWishRecipient(null, "Pluszowy miś"),
            new DtoWishRecipient(null, "Hulajnoga")
        ));

        lotteryFacade.updateWishes(userFacade.findByLogin("b").get().id(), ImmutableList.of(
            new DtoWishRecipient(null, "Harry Potter - film")
        ));

        lotteryFacade.updateWishes(userFacade.findByLogin("c").get().id(), ImmutableList.of(
            new DtoWishRecipient(null, "Percy Jackson - książka")
        ));

        System.out.println(lotteryFacade.actualRecipientWishes(userAId));
    }
}
