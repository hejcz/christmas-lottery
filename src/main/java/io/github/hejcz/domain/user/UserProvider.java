package io.github.hejcz.domain.user;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
class UserProvider {

    private final UserRepository userRepository;

    public UserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    Collection<DtoUser> findRegularUsers() {
        return userRepository.findBySystemRole(SystemRole.USER)
                .stream()
                .map(DbUser::toDto)
                .collect(Collectors.toSet());
    }

    void save(DtoUser dtoUser) {
        userRepository.save(dtoUser.toDb());
    }

    Optional<DtoUser> byLogin(String username) {
        return userRepository.findByLoginIgnoreCase(username)
                .map(DbUser::toDto);
    }

    Optional<DbUser> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Collection<DbUser> findRegularUsersByGroupIdAndIds(Collection<Integer> ids, int groupId) {
        return userRepository.findByIdInAndSystemRoleAndGroups_Id(ids, SystemRole.USER, groupId);
    }

    public DbUser getById(Integer id) {
        return userRepository.getById(id);
    }
}
