package lesziy.carol.domain.registration;

import lesziy.carol.domain.user.DtoUser;
import lesziy.carol.domain.user.UserFacadeImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RegistrationFacade {
    private final UserFacadeImpl userFacade;
    private final PasswordEncoder passwordEncoder;

    public void register(DtoUser dtoUser) {
        userFacade.save(dtoUser.password(passwordEncoder.encode(dtoUser.password())));
    }
}
