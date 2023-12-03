package io.github.hejcz.integration.email;

import io.github.hejcz.domain.lottery.DtoWishRecipient;
import io.github.hejcz.domain.lottery.WishListChange;
import org.hibernate.internal.util.collections.CollectionHelper;

public class EmailFormatter {

    private EmailFormatter() {
    }

    public static String formatWishlistChange(WishListChange wishListChange, String websiteAddress) {
        final StringBuilder message = new StringBuilder();

        if (!CollectionHelper.isEmpty(wishListChange.oldWishes())) {
            message.append("Poprzednia lista życzeń:\n\n");

            for (DtoWishRecipient wish : wishListChange.oldWishes()) {
                message.append("- %s (%s)\n".formatted(wish.text(), wish.url()));
            }

            message.append("\n");
        }

        message.append("Nowa lista życzeń:\n\n");

        for (DtoWishRecipient wish : wishListChange.newWishes()) {
            message.append("- %s (%s)\n".formatted(wish.text(), wish.url()));
        }

        message.append("\n")
                .append("Przejdź do loterii: ")
                .append(websiteAddress)
                .append("\n");

        return message.toString();
    }

    public static String formatPasswordReset(String resetUrl) {
        return "Aby zresetować hasło kliknij w poniższy link:\n%s".formatted(resetUrl);
    }

}
