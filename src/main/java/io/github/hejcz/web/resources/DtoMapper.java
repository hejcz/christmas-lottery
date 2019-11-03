package io.github.hejcz.web.resources;

import io.github.hejcz.domain.lottery.DtoWishGiver;
import io.github.hejcz.domain.lottery.DtoWishRecipient;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {
    public static RecipientWishes getRecipientWishes(DtoWishGiver oldDto) {
        RecipientWishes recipientWishes = new RecipientWishes();
        recipientWishes.setFirstName(oldDto.recipientName());
        recipientWishes.setLastName(oldDto.recipientSurname());
        recipientWishes.setWishes(mapWishes(oldDto.recipientWishes()));
        return recipientWishes;
    }

    public static List<Wish> mapWishes(Collection<DtoWishRecipient> wishes) {
        return wishes.stream().map(oldWish -> {
            Wish wish = new Wish();
            wish.setId(oldWish.getId());
            wish.setPower(oldWish.getPower());
            wish.setTitle(oldWish.getText());
            wish.setUrl(oldWish.getUrl());
            wish.setLocked(oldWish.isLocked());
            return wish;
        }).collect(Collectors.toList());
    }

    public static RecipientWishes noWishes() {
        RecipientWishes recipientWishes = new RecipientWishes();
        recipientWishes.setWishes(Collections.emptyList());
        return recipientWishes;
    }
}
