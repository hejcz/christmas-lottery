package lesziy.carol.domain.user;

import java.util.Collection;
import java.util.Optional;

public interface UserFacade {

    void save(DtoUser dtoUser);

    Collection<DtoUser> loadUsers();

    Optional<DtoUser> findByLogin(String username);

    DbUser findById(Integer id);

    Optional<DtoUser> loggedUser();

    DtoUser loggedUserOrException();
}
