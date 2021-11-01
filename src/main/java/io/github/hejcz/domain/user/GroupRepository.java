package io.github.hejcz.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.annotation.Secured;

@RepositoryRestResource(path = "groups")
@Secured("ADMIN")
public interface GroupRepository extends JpaRepository<DbGroup, Integer> {
}
