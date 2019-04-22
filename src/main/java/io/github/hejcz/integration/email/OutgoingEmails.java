package io.github.hejcz.integration.email;

import io.github.hejcz.domain.lottery.WishListChange;

public interface OutgoingEmails {

    void sendWishesUpdate(String giverEmail, WishListChange wishListChange);

    void sendPasswordRecovery(String giverEmail, String passwordResetUrl);

}
