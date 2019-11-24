package io.github.hejcz.domain.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
interface UserRepository extends CrudRepository<DbUser, Integer> {

    Collection<DbUser> findAll();

    Optional<DbUser> findByLoginIgnoreCase(String username);

    Optional<DbUser> findByEmailIgnoreCase(String email);

    Collection<DbUser> findBySystemRole(SystemRole systemRole);

}
