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
            .register(new DtoUser(0, "admin", "", "", "passwd", "rubin94@gmail.com", SystemRole.ADMIN));
        registrationFacade
            .register(new DtoUser(1, "jrubin", "Julian", "Rubin", "passwd", "rubin94@gmail.com", SystemRole.USER));
        registrationFacade
            .register(new DtoUser(2, "zrubin", "Zosia", "Rubin", "passwd", "rubin94@gmail.com", SystemRole.USER));
        registrationFacade
            .register(new DtoUser(3, "trubin", "Tomek", "Rubin", "passwd", "rubin94@gmail.com", SystemRole.USER));

        lotteryFacade.performLottery();

        userFacade.findByLogin("jrubin").ifPresent(dtoUser ->
            lotteryFacade.updateWishes(dtoUser.id(), ImmutableList.of(
                new DtoWishRecipient(null, "Pluszowy miś"),
                new DtoWishRecipient(null, "Hulajnoga")
            ))
        );

//        userFacade.findByLogin("zrubin").ifPresent(dtoUser ->
//            lotteryFacade.updateWishes(dtoUser.id(), ImmutableList.of(
//                new DtoWishRecipient(null, "Harry Potter - film")
//            ))
//        );
//
//        userFacade.findByLogin("trubin").ifPresent(dtoUser ->
//            lotteryFacade.updateWishes(dtoUser.id(), ImmutableList.of(
//                new DtoWishRecipient(null, "Percy Jackson - książka")
//            ))
//        );

    }
}
