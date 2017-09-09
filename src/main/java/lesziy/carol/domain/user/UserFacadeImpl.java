package lesziy.carol.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserFacadeImpl implements UserFacade {

    private final UserProvider userProvider;

    @Override
    public void save(DtoUser dtoUser) {
        userProvider.save(dtoUser);
    }

    @Override
    public Collection<DtoUser> loadUsers() {
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
}
