package io.github.hejcz.domain.user;

import java.util.Collection;
import java.util.Optional;

public interface UserFacade {

    void save(DtoUser dtoUser);

    Collection<DtoUser> findRegularUsers();

    Optional<DtoUser> findByLogin(String username);

    Optional<DbUser> findById(Integer id);

    DbUser getById(Integer id);

    DtoUser loggedUserOrException();

    Integer loggedUserId();

    boolean isLoggedUserAdmin();

    Collection<DtoUser> findUsersForLottery(Collection<Integer> ids, int groupId);

}
