package io.github.hejcz.domain.user;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<DbUser, Integer> {

    Optional<DbUser> findByLoginIgnoreCase(String username);

    Optional<DbUser> findByEmailIgnoreCase(String email);

    Collection<DbUser> findBySystemRole(SystemRole systemRole);

    @Query("SELECT u from DbUser u JOIN FETCH u.groups g where u.id in :ids and u.systemRole = :role and g.id = :groupId")
    Collection<DbUser> findByIdInAndSystemRoleAndGroups_Id(
            @Param("ids") Collection<Integer> ids,
            @Param("role") SystemRole systemRole,
            @Param("groupId") int groupId);
}
