package io.github.hejcz.domain.lottery;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ForbiddenMatchRepository extends CrudRepository<ForbiddenMatch, Long> {

    @Query("SELECT m FROM ForbiddenMatch m where m.firstUserId in :ids OR m.secondUserId in :ids")
    Collection<ForbiddenMatch> findByFirstOrSecondIdIn(@Param("ids") Collection<Integer> ids);

}
