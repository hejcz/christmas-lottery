package io.github.hejcz.domain.lottery;

import io.github.hejcz.domain.user.DbUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
interface WishesRepository extends CrudRepository<DbWish, Integer> {

    @Query("UPDATE wishes SET locked = FALSE")
    void unlockAllWishes();

    Collection<DbWish> findByRecipientId(Integer recipient);

    Optional<DbWish> findByRecipientIdAndId(Integer recipientId, Integer id);
}
