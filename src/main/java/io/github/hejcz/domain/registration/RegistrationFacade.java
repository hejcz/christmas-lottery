package io.github.hejcz.domain.registration;

import io.github.hejcz.domain.user.DtoUser;
import io.github.hejcz.domain.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationFacade {
    private final UserFacade userFacade;
    private final PasswordEncoder passwordEncoder;

    public void register(DtoUser dtoUser) {
        userFacade.save(dtoUser.password(passwordEncoder.encode(dtoUser.password())));
    }
}
