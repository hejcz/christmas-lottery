package lesziy.carol.domain.lottery;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
interface MatchRepository extends CrudRepository<DbMatch, Integer> {

    Collection<DbMatch> findByGiverId(Integer giverId);

    Collection<DbMatch> findByCreationDateBetween(Timestamp lastYear, Timestamp nextYear);

    default Optional<DbMatch> currentMatch(Integer giverId) {
        return findByGiverId(giverId)
            .stream()
            .filter(dbMatch ->
                dbMatch.getCreationDate().toLocalDateTime().getYear() == LocalDateTime.now().getYear())
            .findFirst();
    }
}
