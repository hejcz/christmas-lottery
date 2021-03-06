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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
        return matchRepository.currentMatch(giverId)
            .map(this::createWishGiverDto);
    }

    private DtoWishGiver createWishGiverDto(DbMatch match) {
        return new DtoWishGiver(
            match.getRecipient().getName(),
            match.getRecipient().getSurname(),
            match.isLocked(),
            wishesOf(match.getRecipient().getId()).getWishes()
        );
    }

    @Override
    public WishList wishesOf(Integer recipientId) {
        boolean isLocked = currentMatch(recipientId).map(DbMatch::isLocked).orElse(false);
        return new WishList(
            isLocked,
            wishesRepository.findByRecipientId(recipientId)
                .stream()
                .sorted(Comparator.comparing(DbWish::getText))
                .map(DbWish::toDto)
                .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public void updateWishes(Integer recipientId, Set<DtoWishRecipient> currentWishes) {
        Collection<DbWish> wishesInDb = wishesRepository.findByRecipientId(recipientId);
        // hibernate could change wishesInDb elements after save
        Set<DtoWishRecipient> previousWishes = wishesInDb.stream().map(DbWish::toDto).collect(Collectors.toSet());
        final boolean nothingChanged = currentWishes.size() == previousWishes.size()
                && Sets.difference(currentWishes, previousWishes).isEmpty();
        if (nothingChanged) {
            return;
        }
        wishesRepository.deleteAll(wishesInDb);
        saveWishes(recipientId, currentWishes);
        sendEmailToGiverIfAssigned(recipientId, Lists.newLinkedList(previousWishes), currentWishes);
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
        return currentMatch(recipientId)
            .map(DbMatch::getGiver)
            .map(DbUser::getEmail);
    }

    private Optional<DbMatch> currentMatch(Integer recipientId) {
        return matchRepository.findByRecipientIdAndCreationDateIsBetween(
            recipientId, startOfCurrentYear(), startOfNextYear());
    }

    @Override
    @Transactional
    public void deleteActualLottery() {
        matchRepository.deleteByCreationDateBetween(startOfCurrentYear(), startOfNextYear());
    }

    @Override
    @Transactional
    public void lockWishes() {
        matchRepository.currentMatch(userFacade.loggedUserId())
            .ifPresent(match -> match.setLocked(true));
    }

    @Override
    @Transactional
    public void unlockWishes() {
        matchRepository.currentMatch(userFacade.loggedUserId())
            .ifPresent(match -> match.setLocked(false));
    }

    private DbMatch matchToDbMatch(Match match) {
        return new DbMatch(
            userFacade.findById(match.giver().getId()),
            userFacade.findById(match.recipient().getId())
        );
    }

}
