package io.github.hejcz.domain.user;

import java.util.Collection;
import java.util.Optional;

public interface UserFacade {

    void save(DtoUser dtoUser);

    Collection<DtoUser> loadUsers();

    Optional<DtoUser> findByLogin(String username);

    DbUser findById(Integer id);

    DtoUser loggedUserOrException();

    boolean isLoggedUserAdmin();

}
