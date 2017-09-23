package lesziy.carol.web.email;

import lesziy.carol.domain.lottery.WishesUpdate;

public interface OutgoingEmails {

    void send(String giverEmail, WishesUpdate wishesUpdate);
}
