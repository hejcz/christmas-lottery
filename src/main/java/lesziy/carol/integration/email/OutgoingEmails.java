package lesziy.carol.integration.email;

import lesziy.carol.domain.lottery.WishesUpdate;

public interface OutgoingEmails {

    void send(String giverEmail, WishesUpdate wishesUpdate);
}
