package io.github.hejcz.domain.registration;

import io.github.hejcz.domain.user.DtoUser;
import io.github.hejcz.domain.user.UserFacade;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RegistrationFacade {

    private final UserFacade userFacade;
    private final PasswordEncoder passwordEncoder;

    public RegistrationFacade(UserFacade userFacade, PasswordEncoder passwordEncoder) {
        this.userFacade = userFacade;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(DtoUser dtoUser) {
        userFacade.save(dtoUser.withPassword(passwordEncoder.encode(dtoUser.password())));
    }

}
