package io.github.hejcz.domain.lottery;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.hejcz.domain.user.DbUser;
import io.github.hejcz.domain.user.UserFacade;
import io.github.hejcz.integration.email.OutgoingEmails;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LotteryFacadeImpl implements LotteryFacade {

    private final UserFacade userFacade;
    private final MatchingEngine matchingEngine;
    private final OutgoingEmails outgoingEmails;
    private final MatchRepository matchRepository;
    private final WishesRepository wishesRepository;
    private final ForbiddenMatchRepository forbiddenMatchRepository;
    private final Clock clock;

    public LotteryFacadeImpl(UserFacade userFacade, MatchingEngine matchingEngine, OutgoingEmails outgoingEmails,
            MatchRepository matchRepository, WishesRepository wishesRepository,
            ForbiddenMatchRepository forbiddenMatchRepository, Clock clock) {
        this.userFacade = userFacade;
        this.matchingEngine = matchingEngine;
        this.outgoingEmails = outgoingEmails;
        this.matchRepository = matchRepository;
        this.wishesRepository = wishesRepository;
        this.forbiddenMatchRepository = forbiddenMatchRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void performLottery(Collection<Integer> participatingUsersIds) {
        Group group = new Group(users(participatingUsersIds));
        if (group.hasMultipleMembers()) {
            matchRepository.saveAll(runLottery(group));
            wishesRepository.deleteAll();
        }
    }

    private Set<UserId> users(Collection<Integer> participatingUsersIds) {
        return userFacade.findRegularUsers()
                .stream()
                .filter(dtoUser -> participatingUsersIds.contains(dtoUser.id()))
                .map(userDto -> new UserId(userDto.id()))
                .collect(Collectors.toSet());
    }

    private List<DbMatch> runLottery(Group group) {
        Map<Match, Long> matchesHistory = matchRepository.findAll().stream()
                .collect(Collectors.groupingBy(DbMatch::asMatch, Collectors.counting()));
        Collection<ForbiddenMatch> forbiddenMatches =
                forbiddenMatchRepository.findForbiddenMatchesBetweenUsersInLottery(group.membersIds());
        return matchingEngine.match(group, new MatchesHistory(matchesHistory), forbiddenMatches)
                .matches()
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
        return Timestamp.from(LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC)
                .with(TemporalAdjusters.firstDayOfYear())
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC));
    }

    private Timestamp startOfNextYear() {
        return Timestamp.from(LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC)
                .with(TemporalAdjusters.firstDayOfNextYear())
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC));
    }

    @Override
    public Optional<DtoWishGiver> actualRecipientWishes(Integer giverId) {
        return currentMatch(giverId, clock.instant())
                .map(this::createWishGiverDto);
    }

    private DtoWishGiver createWishGiverDto(DbMatch match) {
        return new DtoWishGiver(
                match.getRecipient().getName(),
                match.getRecipient().getSurname(),
                match.isLocked(),
                wishesOf(match.getRecipient().getId()).wishes()
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
        // copy wishes, so we can pass old wishlist to e-mail sender.
        Set<DtoWishRecipient> previousWishes = wishesInDb.stream().map(DbWish::toDto).collect(Collectors.toSet());
        final boolean nothingChanged = currentWishes.size() == previousWishes.size()
                && Sets.difference(currentWishes, previousWishes).isEmpty();
        if (nothingChanged) {
            return;
        }
        // TODO this looks weird - why do I delete all wishes? Is this for simplicity?
        wishesRepository.deleteAll(wishesInDb);
        saveWishes(recipientId, currentWishes);
        sendEmailToGiverIfAssigned(recipientId, Lists.newLinkedList(previousWishes), currentWishes);
    }

    private void saveWishes(Integer recipientId, Collection<DtoWishRecipient> wishes) {
        DbUser recipient = userFacade.findById(recipientId);
        Instant now = clock.instant();
        wishesRepository.saveAll(
                wishes.stream()
                        // poprawić żeby był update timestamp
                        .map(dtoWishRecipient -> dtoWishRecipient.toDb(recipient, now))
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
        currentMatch(userFacade.loggedUserId(), clock.instant())
                .ifPresent(match -> match.setLocked(true));
    }

    @Override
    @Transactional
    public void unlockWishes() {
        currentMatch(userFacade.loggedUserId(), clock.instant())
                .ifPresent(match -> match.setLocked(false));
    }

    private DbMatch matchToDbMatch(Match match) {
        return new DbMatch(
                null, Timestamp.from(clock.instant()),
                userFacade.findById(match.giver().id()),
                userFacade.findById(match.recipient().id()),
                false
        );
    }

    private Optional<DbMatch> currentMatch(Integer giverId, Instant now) {
        int currentYear = ZonedDateTime.ofInstant(now, ZoneOffset.UTC).getYear();
        return matchRepository.findByGiverId(giverId)
                .stream()
                .filter(dbMatch ->
                        dbMatch.getCreationDate().toLocalDateTime().getYear() == currentYear)
                .findFirst();
    }
}
