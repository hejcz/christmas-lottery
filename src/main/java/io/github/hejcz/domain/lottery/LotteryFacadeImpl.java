package io.github.hejcz.domain.lottery;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.hejcz.domain.user.DbGroup;
import io.github.hejcz.domain.user.DbUser;
import io.github.hejcz.domain.user.GroupRepository;
import io.github.hejcz.domain.user.UserFacade;
import io.github.hejcz.integration.email.OutgoingEmails;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
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
public class LotteryFacadeImpl implements LotteryFacade {

    private final UserFacade userFacade;
    private final MatchingEngine matchingEngine;
    private final OutgoingEmails outgoingEmails;
    private final MatchRepository matchRepository;
    private final WishesRepository wishesRepository;
    private final GroupRepository groupRepository;
    private final ForbiddenMatchRepository forbiddenMatchRepository;

    public LotteryFacadeImpl(UserFacade userFacade, MatchingEngine matchingEngine, OutgoingEmails outgoingEmails,
                             MatchRepository matchRepository, WishesRepository wishesRepository,
                             GroupRepository groupRepository, ForbiddenMatchRepository forbiddenMatchRepository) {
        this.userFacade = userFacade;
        this.matchingEngine = matchingEngine;
        this.outgoingEmails = outgoingEmails;
        this.matchRepository = matchRepository;
        this.wishesRepository = wishesRepository;
        this.groupRepository = groupRepository;
        this.forbiddenMatchRepository = forbiddenMatchRepository;
    }

    @Override
    @Transactional
    public void performLottery(int groupId, List<Integer> participantsIds) {
        if (isLotteryRunning(groupId)) {
            return;
        }
        final Set<UserId> lotteryParticipantsIds =
                userFacade.findUsersForLottery(participantsIds, groupId)
                        .stream()
                        .map(userDto -> new UserId(userDto.id()))
                        .collect(Collectors.toSet());
        final LotteryParticipants lotteryParticipants = new LotteryParticipants(lotteryParticipantsIds);
        if (lotteryParticipants.ids().size() > 1) {
            final Set<Match> matchesHistory = matchRepository.findByGroupId(groupId).stream()
                    .map(DbMatch::asMatch)
                    .collect(Collectors.toSet());
            final Collection<ForbiddenMatch> forbiddenMatches =
                    forbiddenMatchRepository.findForbiddenMatchesBetweenUsersInLottery(lotteryParticipants.toActualIds());
            final AnnualMatches newMatches = matchingEngine.match(lotteryParticipants,
                    new MatchesHistory(Set.of(new AnnualMatches(matchesHistory))), forbiddenMatches);
            matchRepository.saveAll(newMatches.matches().stream()
                    .map(match -> matchToDbMatch(match, groupRepository.getById(groupId)))
                    .collect(Collectors.toList()));
            wishesRepository.deleteByMatch_Group_Id(groupId);
        }
    }

    @Override
    public boolean isLotteryRunning(int groupId) {
        return matchRepository.countByCreationDateBetweenAndGroupId(startOfCurrentYear(),
                startOfNextYear(), groupId) > 0;
    }

    private Timestamp startOfCurrentYear() {
        return Timestamp.valueOf(LocalDateTime.now().withDayOfYear(1));
    }

    private Timestamp startOfNextYear() {
        return Timestamp.valueOf(LocalDateTime.now().withDayOfYear(1).plusYears(1));
    }

    @Override
    public Optional<DtoWishGiver> getMatchWishes(Integer giverId, int groupId) {
        return matchRepository.currentMatch(giverId, groupId)
                .map(match -> createWishGiverDto(match, groupId));
    }

    private DtoWishGiver createWishGiverDto(DbMatch match, int groupId) {
        return new DtoWishGiver(
                match.getRecipient().getName(),
                match.getRecipient().getSurname(),
                match.isLocked(),
                wishesOf(match.getRecipient().getId(), groupId).wishes()
        );
    }

    @Override
    public WishList wishesOf(Integer recipientId, int groupId) {
        Optional<DbMatch> dbMatch = currentMatch(recipientId, groupId);
        if (dbMatch.isEmpty()) {
            return new WishList(false, List.of());
        }
        boolean isLocked = dbMatch
                .map(DbMatch::isLocked)
                .orElse(false);
        return new WishList(
                isLocked,
                wishesRepository.findByMatch_id(dbMatch.get().getId())
                        .stream()
                        .sorted(Comparator.comparing(DbWish::getText))
                        .map(DbWish::toDto)
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public void updateWishes(Integer recipientId, int groupId, Set<DtoWishRecipient> currentWishes) {
        Optional<DbMatch> dbMatch = currentMatch(recipientId, groupId);
        if (dbMatch.isEmpty()) {
            return;
        }
        Collection<DbWish> wishesInDb = wishesRepository.findByMatch_id(dbMatch.get().getId());
        // copy wishes, so we can pass old wishlist to e-mail sender.
        Set<DtoWishRecipient> previousWishes = wishesInDb.stream().map(DbWish::toDto).collect(Collectors.toSet());
        final boolean nothingChanged = currentWishes.size() == previousWishes.size()
                && Sets.difference(currentWishes, previousWishes).isEmpty();
        if (nothingChanged) {
            return;
        }
        // TODO this looks weird - why do I delete all wishes? Is this for simplicity?
        wishesRepository.deleteAll(wishesInDb);
        saveWishes(dbMatch.get(), currentWishes);
        sendEmailToGiverIfAssigned(recipientId, Lists.newLinkedList(previousWishes), currentWishes, groupId);
    }

    private void saveWishes(DbMatch match, Collection<DtoWishRecipient> wishes) {
        wishesRepository.saveAll(
                wishes.stream()
                        // poprawić żeby był update timestamp
                        .map(dtoWishRecipient -> dtoWishRecipient.toDb(match))
                        .collect(Collectors.toList()));
    }

    private void sendEmailToGiverIfAssigned(Integer recipientId,
                                            List<DtoWishRecipient> oldWishes,
                                            Collection<DtoWishRecipient> newWishes, int groupId) {
        findGiverEmail(recipientId, groupId).ifPresent(email ->
                outgoingEmails.sendWishesUpdate(
                        email,
                        new WishListChange(
                                oldWishes,
                                new ArrayList<>(newWishes)
                        )
                )
        );
    }

    private Optional<String> findGiverEmail(Integer recipientId, int groupId) {
        return currentMatch(recipientId, groupId)
                .map(DbMatch::getGiver)
                .map(DbUser::getEmail);
    }

    private Optional<DbMatch> currentMatch(Integer recipientId, int groupId) {
        return matchRepository.findDistinctByRecipientIdAndCreationDateIsBetweenAndGroup_id(
                recipientId, startOfCurrentYear(), startOfNextYear(), groupId);
    }

    @Override
    @Transactional
    public void deleteActualLottery(int groupId) {
        matchRepository.deleteByCreationDateBetweenAndGroupId(startOfCurrentYear(), startOfNextYear(),
                groupId);
    }

    @Override
    @Transactional
    public void lockWishes(Integer giverId, int groupId) {
        matchRepository.currentMatch(giverId, groupId)
                .ifPresent(match -> match.setLocked(true));
    }

    @Override
    @Transactional
    public void unlockWishes(Integer giverId, int groupId) {
        matchRepository.currentMatch(giverId, groupId)
                .ifPresent(match -> match.setLocked(false));
    }

    private DbMatch matchToDbMatch(Match match, DbGroup group) {
        return new DbMatch(null, null, userFacade.getById(match.giver().id()),
                userFacade.getById(match.recipient().id()), false, group);
    }

}
