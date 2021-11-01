package io.github.hejcz.web.resources;

import io.github.hejcz.domain.lottery.DtoWishGiver;
import io.github.hejcz.domain.lottery.DtoWishRecipient;
import io.github.hejcz.domain.lottery.StartLotteryRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {
    public static RecipientWishes getRecipientWishes(DtoWishGiver oldDto) {
        return new RecipientWishes(oldDto.recipientName(), oldDto.recipientSurname(),
                oldDto.locked(), mapWishes(oldDto.recipientWishes()));
    }

    public static List<Wish> mapWishes(Collection<DtoWishRecipient> wishes) {
        return wishes.stream()
                .map(oldWish -> new Wish(oldWish.id(), oldWish.text(), oldWish.url(), oldWish.power()))
                .collect(Collectors.toList());
    }

    public static RecipientWishes noWishes() {
        return new RecipientWishes(null, null, false, Collections.emptyList());
    }

    public static StartLotteryRequest map(StartLotteryDto dto) {
        return new StartLotteryRequest(dto.groupId(), List.copyOf(dto.participantsIds()));
    }
}
