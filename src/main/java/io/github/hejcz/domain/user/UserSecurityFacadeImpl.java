package io.github.hejcz.domain.user;

import io.github.hejcz.integration.email.OutgoingEmails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class UserSecurityFacadeImpl implements UserSecurityFacade {

    private final PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;
    private final UserRepository userRepository;
    private final OutgoingEmails outgoingEmails;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void requestPasswordRecovery(String requestUrl, String email) {
        passwordRecoveryTokenRepository.deleteByEmail(email);
        DbPasswordRecoveryToken token = newToken(email);
        passwordRecoveryTokenRepository.save(token);
        outgoingEmails.sendPasswordRecovery(email, requestUrl + "/recover?token=" + token.getToken());
    }

    @Override
    @Transactional
    public Optional<String> recoveryEmail(String token) {
        Optional<DbPasswordRecoveryToken> existingToken =
            passwordRecoveryTokenRepository.findByToken(token);
        existingToken.ifPresent(passwordRecoveryTokenRepository::delete);
        return existingToken.map(DbPasswordRecoveryToken::getEmail);
    }

    @Override
    public void newPassword(String email, String newPassword) {
        userRepository.findByEmail(email)
            .ifPresent(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
            });
    }

    private DbPasswordRecoveryToken newToken(String email) {
        return new DbPasswordRecoveryToken(
            null,
            new RandomString(20).nextString(),
            email
        );
    }
}
