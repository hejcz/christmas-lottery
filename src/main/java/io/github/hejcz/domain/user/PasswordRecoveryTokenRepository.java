package io.github.hejcz.domain.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface PasswordRecoveryTokenRepository extends CrudRepository<DbPasswordRecoveryToken, Integer> {

    Optional<DbPasswordRecoveryToken> findByToken(String token);
    void deleteByEmail(String email);
}
