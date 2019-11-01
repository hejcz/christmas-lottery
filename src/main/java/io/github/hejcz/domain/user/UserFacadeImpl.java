package io.github.hejcz.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserProvider userProvider;

    @Override
    public void save(DtoUser dtoUser) {
        userProvider.save(dtoUser);
    }

    @Override
    public Collection<DtoUser> findRegularUsers() {
        return userProvider.all();
    }

    @Override
    public Optional<DtoUser> findByLogin(String username) {
        return userProvider.byLogin(username);
    }

    @Override
    public DbUser findById(Integer id) {
        return userProvider.byId(id);
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

}
