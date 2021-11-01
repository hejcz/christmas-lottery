package io.github.hejcz.domain.lottery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
interface MatchRepository extends JpaRepository<DbMatch, Integer> {

    Collection<DbMatch> findByGroupId(int groupId);

    Collection<DbMatch> findByGiverIdAndGroupId(Integer giverId, int groupId);

    long countByCreationDateBetweenAndGroupId(Timestamp lastYear, Timestamp nextYear, int groupId);

    // TODO simplify using @Query and use findByGiverIdAndGroupId
    default Optional<DbMatch> currentMatch(Integer giverId, int groupId) {
        return findByGiverIdAndGroupId(giverId, groupId)
                .stream()
                .filter(dbMatch ->
                        dbMatch.getCreationDate().toLocalDateTime().getYear() == LocalDateTime.now().getYear())
                .findFirst();
    }

    // TODO add Lottery entity and clear with cascade
    void deleteByCreationDateBetweenAndGroupId(Timestamp startOfCurrentYear, Timestamp startOfNextYear, int groupId);

    // TODO why is it range? Can't we just look on current year?
    Optional<DbMatch> findDistinctByRecipientIdAndCreationDateIsBetweenAndGroup_id
            (Integer recipientId, Timestamp startOfCurrentYear, Timestamp startOfNextYear, int groupId);
}
