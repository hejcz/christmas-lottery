package io.github.hejcz.domain.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
interface UserRepository extends CrudRepository<DbUser, Integer> {

    Collection<DbUser> findAll();

    Optional<DbUser> findByLogin(String username);

    Optional<DbUser> findByEmail(String email);

    Collection<DbUser> findBySystemRole(SystemRole systemRole);
}
