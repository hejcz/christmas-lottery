package io.github.hejcz.domain.lottery;

import io.github.hejcz.domain.user.DbUser;
import io.github.hejcz.domain.user.UserFacade;
import io.github.hejcz.integration.email.OutgoingEmails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LotteryFacadeImpl implements LotteryFacade {

    private final UserFacade userFacade;

    private final MatchingEngine matchingEngine;

    private final OutgoingEmails outgoingEmails;

    private final MatchRepository matchRepository;

    private final WishesRepository wishesRepository;

    private final ForbiddenMatchRepository forbiddenMatchRepository;

    @Override
    @Transactional
    public void performLottery(Collection<Integer> participatingUsersIds) {
        Group group = new Group(users(participatingUsersIds));
        if (group.hasMultipleMembers()) {
            matchRepository.saveAll(lotteryResults(group));
            wishesRepository.deleteAll();
        }
    }

    private Set<User> users(Collection<Integer> participatingUsersIds) {
        return userFacade.findRegularUsers()
            .stream()
            .filter(dtoUser -> participatingUsersIds.contains(dtoUser.id()))
            .map(userDto -> User.with(userDto.id()))
            .collect(Collectors.toSet());
    }

    private List<DbMatch> lotteryResults(Group group) {
        AnnualMatches history =
            new AnnualMatches(matchRepository.findAll().stream().map(DbMatch::asMatch).collect(Collectors.toSet()));
        Collection<ForbiddenMatch> forbiddenMatches =
            forbiddenMatchRepository.findForbiddenMatchesBetweenUsersInLottery(group.membersIds());
        return matchingEngine.match(group, new MatchesHistory(Collections.singleton(history)), forbiddenMatches)
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
            recipient.getName(),
            recipient.getSurname(),
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
    @Transactional
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
            sendEmailToGiverIfAssigned(recipientId, wishesInDbDtos, wishes);
        }
    }

    @Override
    @Transactional
    public void lock(Integer wishId) {
        modifyWishLock(wishId, true);
    }

    @Override
    @Transactional
    public void unlock(Integer wishId) {
        modifyWishLock(wishId, false);
    }

    private void modifyWishLock(Integer wishId, boolean lock) {
        Integer loggedUserId = userFacade.loggedUserId();
        loadActualRecipient(loggedUserId)
            .flatMap(recipient -> wishesRepository.findByRecipientIdAndId(recipient.getId(), wishId))
            .ifPresent(wish -> {
                wish.setLocked(lock);
                wishesRepository.save(wish);
            });
    }

    private void removeInvalidWishes(Collection<DbWish> wishesInDb, Set<Integer> ids) {
        wishesRepository.deleteAll(
            wishesInDb.stream()
                .filter(wish -> !ids.contains(wish.getId()))
                .collect(Collectors.toSet())
        );
    }

    private void saveWishes(Integer recipientId, Collection<DtoWishRecipient> wishes) {
        DbUser recipient = userFacade.findById(recipientId);
        wishesRepository.saveAll(
            wishes.stream()
                // poprawić żeby był update timestamp
                .map(dtoWishRecipient -> dtoWishRecipient.toDb(recipient))
                .collect(Collectors.toList())
        );
    }

    private boolean anythingChanged(Collection<DtoWishRecipient> oldWishes, Collection<DtoWishRecipient> newWishes) {
        return oldWishes.size() != newWishes.size()
            || oldWishes.stream().anyMatch(it -> !newWishes.contains(it));
    }

    private void sendEmailToGiverIfAssigned(Integer recipientId,
                                            List<DtoWishRecipient> oldWishes,
                                            Collection<DtoWishRecipient> newWishes) {
        findGiverEmail(recipientId).ifPresent(email ->
            outgoingEmails.sendWishesUpdate(
                email,
                new WishListChange(
                    oldWishes,
                    new ArrayList<>(newWishes)
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
    @Transactional
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
