package lesziy.carol.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserProvider {
    private final UserRepository userRepository;

    Collection<DtoUser> all() {
        return userRepository.findBySystemRole(SystemRole.USER)
            .stream()
            .map(DbUser::toDto)
            .collect(Collectors.toSet());
    }

    void save(DtoUser dtoUser) {
        userRepository.save(dtoUser.toDb());
    }

    Optional<DtoUser> byLogin(String username) {
        return userRepository.findByLogin(username)
            .map(DbUser::toDto);
    }

    DbUser byId(Integer id) {
        return userRepository.findOne(id);
    }
}
