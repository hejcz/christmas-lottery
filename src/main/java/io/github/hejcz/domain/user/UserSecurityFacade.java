package io.github.hejcz.domain.user;

import java.util.Optional;

public interface UserSecurityFacade {

    void requestPasswordRecovery(String url, String email);

    Optional<String> recoveryEmail(String token);

    void newPassword(String email, String newPassword);

}
