package lesziy.carol.domain.lottery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import lesziy.carol.domain.user.DbUser;
import lesziy.carol.domain.user.UserFacade;
import lesziy.carol.integration.email.OutgoingEmails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LotteryFacadeImpl implements LotteryFacade {

    private final UserFacade userFacade;

    private final MatchingEngine matchingEngine;

    private final OutgoingEmails outgoingEmails;

    private final MatchRepository matchRepository;

    private final WishesRepository wishesRepository;

    @Override
    public void performLottery(Collection<Integer> participatingUsersIds) {
        Users users = new Users(users(participatingUsersIds));
        if (users.moreThanOne()) {
            matchRepository.save(lotteryResults(users));
        }
    }

    private Set<User> users(Collection<Integer> participatingUsersIds) {
        return userFacade.loadUsers()
            .stream()
            .filter(dtoUser -> participatingUsersIds.contains(dtoUser.id()))
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
    public boolean annualLotteryNotPerformedYet() {
        return matchRepository.findByCreationDateBetween(startOfCurrentYear(), startOfNextYear())
            .isEmpty();
    }

    private Timestamp startOfCurrentYear() {
        return Timestamp.valueOf(LocalDateTime.now().withDayOfYear(1));
    }

    private Timestamp startOfNextYear() {
        return Timestamp.valueOf(LocalDateTime.now().withDayOfYear(1).plusYears(1));
    }

    @Override
    public Optional<DtoWishGiver> actualRecipientWishes(Integer giverId) {
        return loadActualRecipient(giverId)
            .map(this::createWishGiverDto);
    }

    private Optional<DbUser> loadActualRecipient(Integer giverId) {
        return matchRepository.currentMatch(giverId)
            .map(DbMatch::getRecipient);
    }

    private DtoWishGiver createWishGiverDto(DbUser recipient) {
        return new DtoWishGiver(
            recipient.formatName(),
            wishesOf(recipient.getId())
        );
    }

    @Override
    public Collection<DtoWishRecipient> wishesOf(Integer recipientId) {
        return wishesRepository.findByRecipientId(recipientId)
            .stream()
            .sorted(Comparator.comparing(DbWish::getCreationDate))
            .map(DbWish::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void updateWishes(Integer recipientId, Collection<DtoWishRecipient> wishes) {
        Collection<DbWish> wishesInDb = wishesRepository.findByRecipientId(recipientId);
        // hibernate could change wishesInDb elements after save
        List<DtoWishRecipient> wishesInDbDtos = wishesInDb.stream().map(DbWish::toDto).collect(Collectors.toList());
        Set<Integer> ids = wishes.stream().map(DtoWishRecipient::getId).collect(Collectors.toSet());
        removeInvalidWishes(wishesInDb, ids);
        if (!wishes.isEmpty()) {
            saveWishes(recipientId, wishes);
        }
        if (anythingChanged(wishesInDbDtos, wishes)) {
            sendEmailToGiver(recipientId, wishesInDbDtos, wishes);
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
                // poprawić żeby był update timestamp
                .map(dtoWishRecipient -> dtoWishRecipient.toDb(recipient))
                .collect(Collectors.toList())
        );
    }

    private boolean anythingChanged(List<DtoWishRecipient> wishesInDbDtos, Collection<DtoWishRecipient> wishes) {
        Set<String> oldWishes = wishesInDbDtos.stream()
            .map(DtoWishRecipient::getText)
            .collect(Collectors.toSet());
        Set<String> newWishes = wishes.stream()
            .map(DtoWishRecipient::getText)
            .collect(Collectors.toSet());
        return oldWishes.size() != newWishes.size()
            || !Sets.difference(oldWishes, newWishes).isEmpty();
    }

    private void sendEmailToGiver(Integer recipientId,
                                  List<DtoWishRecipient> oldWishes,
                                  Collection<DtoWishRecipient> newWishes) {
        findGiverEmail(recipientId).ifPresent(email ->
            outgoingEmails.send(
                email,
                new WishesUpdate(
                    oldWishes,
                    ImmutableList.copyOf(newWishes)
                )
            )
        );
    }

    private Optional<String> findGiverEmail(Integer recipientId) {
        return matchRepository.findByRecipientIdAndCreationDateIsBetween(
            recipientId, startOfCurrentYear(), startOfNextYear())
            .map(DbMatch::getGiver)
            .map(DbUser::getEmail);
    }

    @Override
    public void deleteActualLottery() {
        matchRepository.deleteByCreationDateBetween(startOfCurrentYear(), startOfNextYear());
    }

    private DbMatch matchToDbMatch(Match match) {
        return new DbMatch(
            userFacade.findById(match.giver().getId()),
            userFacade.findById(match.recipient().getId())
        );
    }
}
