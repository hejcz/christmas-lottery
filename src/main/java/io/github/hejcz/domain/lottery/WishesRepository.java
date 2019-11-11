package io.github.hejcz.domain.lottery;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
interface WishesRepository extends CrudRepository<DbWish, Integer> {

    @Modifying
    @Query("UPDATE DbWish SET locked = FALSE")
    void unlockAllWishes();

    Collection<DbWish> findByRecipientId(Integer recipient);

    Optional<DbWish> findByRecipientIdAndId(Integer recipientId, Integer id);
}
