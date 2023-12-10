package io.github.hejcz.domain.lottery;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

@Repository
interface MatchRepository extends CrudRepository<DbMatch, Integer> {

    Collection<DbMatch> findAll();

    Collection<DbMatch> findByGiverId(Integer giverId);

    Collection<DbMatch> findByCreationDateBetween(Timestamp lastYear, Timestamp nextYear);

    void deleteByCreationDateBetween(Timestamp startOfCurrentYear, Timestamp startOfNextYear);

    Optional<DbMatch> findByRecipientIdAndCreationDateIsBetween(Integer recipientId,
                                                                Timestamp startOfCurrentYear,
                                                                Timestamp startOfNextYear);

}
