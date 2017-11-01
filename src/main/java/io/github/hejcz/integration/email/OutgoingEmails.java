package io.github.hejcz.integration.email;

import io.github.hejcz.domain.lottery.WishesUpdate;

public interface OutgoingEmails {

    void sendWishesUpdate(String giverEmail, WishesUpdate wishesUpdate);

    void sendPasswordRecovery(String giverEmail, String passwordResetUrl);
}
