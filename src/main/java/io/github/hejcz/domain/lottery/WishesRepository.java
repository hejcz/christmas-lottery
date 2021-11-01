package io.github.hejcz.domain.lottery;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
interface WishesRepository extends CrudRepository<DbWish, Integer> {

    Collection<DbWish> findByMatch_id(int matchId);

    void deleteByMatch_Group_Id(int groupId);
}
