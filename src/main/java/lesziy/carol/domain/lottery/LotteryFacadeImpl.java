package lesziy.carol.domain.lottery;

import lesziy.carol.domain.user.DbUser;
import lesziy.carol.domain.user.UserFacadeImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LotteryFacadeImpl implements LotteryFacade {

    private final UserFacadeImpl userFacade;

    private final MatchingEngine matchingEngine;

    private final MatchRepository matchRepository;

    private final WishesRepository wishesRepository;

    @Override
    public void performLottery() {
        Users users = new Users(users());
        if (users.moreThanOne()) {
            matchRepository.save(lotteryResults(users));
        }
    }

    @Override
    public boolean annualLotteryNotPerformedYet() {
        return matchRepository.findByCreationDateBetween(
            Timestamp.valueOf(LocalDateTime.now().withDayOfYear(1)),
            Timestamp.valueOf(LocalDateTime.now().withDayOfYear(1).plusYears(1))
        ).isEmpty();
    }

    private Set<User> users() {
        return userFacade.loadUsers()
            .stream()
            .map(userDto -> User.with(userDto.id()))
            .collect(Collectors.toSet());
    }

    private List<DbMatch> lotteryResults(Users users) {
        return matchingEngine.match(users, new MatchesHistory(new ArrayList<>()))
            .getMatches()
            .stream()
            .map(this::matchToDbMatch)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<DtoWishGiver> actualRecipientWishes(Integer giverId) {
        return loadActualRecipient(giverId)
            .map(this::createWishGiverDto);
    }

    @Override
    public Collection<DtoWishRecipient> wishesOf(Integer recipientId) {
        return wishesRepository.findByRecipientId(recipientId)
            .stream()
            .map(DbWish::toDto)
            .collect(Collectors.toList());
    }

    private Optional<DbUser> loadActualRecipient(Integer giverId) {
        return matchRepository.currentMatch(giverId)
            .map(DbMatch::getRecipient);
    }

    private DtoWishGiver createWishGiverDto(DbUser recipient) {
        return new DtoWishGiver(
            recipient.getLogin(),
            wishesOf(recipient.getId())
        );
    }

    @Override
    public void updateWishes(Integer recipientId, Collection<DtoWishRecipient> wishes) {
        Collection<DbWish> wishesInDb = wishesRepository.findByRecipientId(recipientId);
        Set<Integer> ids = wishes.stream().map(DtoWishRecipient::id).collect(Collectors.toSet());
        removeInvalidWishes(wishesInDb, ids);
        if (!wishes.isEmpty()) {
            saveWishes(recipientId, wishes);
        }
    }

    private void removeInvalidWishes(Collection<DbWish> wishesInDb, Set<Integer> ids) {
        wishesRepository.delete(
            wishesInDb.stream()
                .filter(wish -> !ids.contains(wish.getId()))
                .collect(Collectors.toSet())
        );
    }

    private void saveWishes(Integer recipientId, Collection<DtoWishRecipient> wishes) {
        DbUser recipient = userFacade.findById(recipientId);
        wishesRepository.save(
            wishes.stream()
                .map(dtoWishRecipient -> dtoWishRecipient.toDb(recipient))
                .collect(Collectors.toList())
        );
    }

    private DbMatch matchToDbMatch(Match match) {
        return new DbMatch(
            userFacade.findById(match.giver().getId()),
            userFacade.findById(match.recipient().getId())
        );
    }
}
