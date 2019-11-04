package io.github.hejcz.domain.user;

public interface UserSecurityFacade {

    void requestPasswordRecovery(String email);

    void changePassword(String token, String newPassword);
}
