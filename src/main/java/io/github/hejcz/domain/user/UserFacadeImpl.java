package io.github.hejcz.domain.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserFacadeImpl implements UserFacade {

    private final UserProvider userProvider;

    public UserFacadeImpl(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @Override
    public void save(DtoUser dtoUser) {
        userProvider.save(dtoUser);
    }

    @Override
    public Collection<DtoUser> findRegularUsers() {
        return userProvider.findRegularUsers();
    }

    @Override
    public Optional<DtoUser> findByLogin(String username) {
        return userProvider.byLogin(username);
    }

    @Override
    public Optional<DbUser> findById(Integer id) {
        return userProvider.findById(id);
    }

    @Override
    public DbUser getById(Integer id) {
        return userProvider.getById(id);
    }

    @Override
    public DtoUser loggedUserOrException() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return findByLogin(auth.getName()).orElseThrow(RuntimeException::new);
    }

    @Override
    public Integer loggedUserId() {
        return loggedUserOrException().id();
    }

    @Override
    public boolean isLoggedUserAdmin() {
        return loggedUserOrException().systemRole() == SystemRole.ADMIN;
    }

    @Override
    public Collection<DtoUser> findUsersForLottery(Collection<Integer> ids, int groupId) {
        return userProvider.findRegularUsersByGroupIdAndIds(ids, groupId).stream()
                .map(DbUser::toDto)
                .collect(Collectors.toList());
    }

}
