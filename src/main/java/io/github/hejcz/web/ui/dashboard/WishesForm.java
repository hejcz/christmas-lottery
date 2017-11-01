package io.github.hejcz.web.ui.dashboard;

import io.github.hejcz.domain.lottery.DtoWishRecipient;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
class WishesForm {

    private List<DtoWishRecipient> wishes;

    WishesForm(Collection<DtoWishRecipient> dtoWishRecipients) {
        wishes = new ArrayList<>(dtoWishRecipients);
    }
}
