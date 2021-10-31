package io.github.hejcz.domain.user;

import io.github.hejcz.integration.email.OutgoingEmails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class UserSecurityFacadeImpl implements UserSecurityFacade {

    private final PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;
    private final UserRepository userRepository;
    private final OutgoingEmails outgoingEmails;
    private final PasswordEncoder passwordEncoder;

    public UserSecurityFacadeImpl(PasswordRecoveryTokenRepository passwordRecoveryTokenRepository,
                                  UserRepository userRepository, OutgoingEmails outgoingEmails,
                                  PasswordEncoder passwordEncoder) {
        this.passwordRecoveryTokenRepository = passwordRecoveryTokenRepository;
        this.userRepository = userRepository;
        this.outgoingEmails = outgoingEmails;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${santa.url}")
    private String websiteAddress;

    @Override
    @Transactional
    public void requestPasswordRecovery(String email) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(it -> sendRecoveryTokenToUser(email));
    }

    private void sendRecoveryTokenToUser(String email) {
        passwordRecoveryTokenRepository.deleteByEmail(email);
        DbPasswordRecoveryToken token = newToken(email);
        passwordRecoveryTokenRepository.save(token);
        outgoingEmails.sendPasswordRecovery(email, websiteAddress + "/new-password?token=" + token.getToken());
    }

    @Override
    @Transactional
    public void changePassword(String token, String newPassword) {
        newPassword(recoveryEmail(token), newPassword);
    }

    private String recoveryEmail(String token) {
        DbPasswordRecoveryToken existingToken =
                passwordRecoveryTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Token does not exist"));
        passwordRecoveryTokenRepository.delete(existingToken);
        return existingToken.getEmail();
    }

    private void newPassword(String email, String newPassword) {
        DbUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("No user with such e-mail"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private DbPasswordRecoveryToken newToken(String email) {
        return new DbPasswordRecoveryToken(
                null,
                new RandomString(20).nextString(),
                email
        );
    }

}
